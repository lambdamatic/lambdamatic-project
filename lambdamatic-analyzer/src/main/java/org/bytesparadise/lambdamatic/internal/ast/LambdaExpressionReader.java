/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.bytesparadise.lambdamatic.internal.ast.node.ASTNode;
import org.bytesparadise.lambdamatic.internal.ast.node.BooleanLiteral;
import org.bytesparadise.lambdamatic.internal.ast.node.CapturedArgument;
import org.bytesparadise.lambdamatic.internal.ast.node.Expression;
import org.bytesparadise.lambdamatic.internal.ast.node.IfStatement;
import org.bytesparadise.lambdamatic.internal.ast.node.InfixExpression;
import org.bytesparadise.lambdamatic.internal.ast.node.LocalVariable;
import org.bytesparadise.lambdamatic.internal.ast.node.MethodInvocation;
import org.bytesparadise.lambdamatic.internal.ast.node.NumberLiteral;
import org.bytesparadise.lambdamatic.internal.ast.node.ReturnStatement;
import org.bytesparadise.lambdamatic.internal.ast.node.Statement;
import org.bytesparadise.lambdamatic.internal.ast.node.StringLiteral;
import org.bytesparadise.lambdamatic.internal.ast.node.InfixExpression.InfixOperator;
import org.bytesparadise.lambdamatic.query.FilterExpression;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An internal utility class that uses ASM to read the bytecode of a (desugared lambda expression) method and converts
 * it into an {@link ASTNode}.
 * 
 * @author xcoulon
 *
 */
class LambdaExpressionReader {

	/** The usual Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LambdaExpressionReader.class);

	/**
	 * Reads the given {@link List} of (bytecode) {@link AbstractInsnNode}s and computes a simplified {@link Statement}
	 * based tree representing the initial lambda expression.
	 * 
	 * @param instructions
	 *            the bytecode instructions
	 * @param localVariables
	 *            the table of local variables available during the execution of the give instructions.
	 * @param labels
	 * @param nodes
	 * @return the {@link Statement}-based tree
	 * @throws IOException
	 */
	<T> Statement readBytecodeStatement(final FilterExpression<T> expression) throws IOException {
		final SerializedLambda serializedLambda = getSerializedLambda(expression);
		final List<Object> capturedArgs = new ArrayList<>();
		for (int i = 0; i < serializedLambda.getCapturedArgCount(); i++) {
			capturedArgs.add(serializedLambda.getCapturedArg(i));
		}
		final LambdaExpressionClassVisitor desugaredExpressionVisitor = new LambdaExpressionClassVisitor(
				serializedLambda.getImplClass(), serializedLambda.getImplMethodName(),
				serializedLambda.getImplMethodSignature());
		final ClassReader classReader = new ClassReader(serializedLambda.getImplClass());
		classReader.accept(desugaredExpressionVisitor, 0);
		final InsnList instructions = desugaredExpressionVisitor.getInstructions();
		final List<LocalVariableNode> localVariables = desugaredExpressionVisitor.getLocalVariables();
		final Map<String, AbstractInsnNode> labels = desugaredExpressionVisitor.getLabels();
		return readStatementSubTree(instructions.getFirst(), new Stack<>(), capturedArgs, localVariables, labels);
	}

	/**
	 * @return {@code true} if the Java {@link Method} has a {@code static} modifier, false otherwise.
	 * 
	 * @param className
	 *            the name of the class to analyze
	 * @param methodName
	 *            the name of the method to analyze
	 * @param methodSignature
	 *            the signature of the method to analyze private static boolean isStaticMethod(final String className,
	 *            final String methodName, final String methodSignature) { try { final Class<?> clazz =
	 *            Class.forName(className); final Type[] parameterTypes = Type.getArgumentTypes(methodSignature); final
	 *            Class<?>[] parameterClasses = new Class<?>[parameterTypes.length]; for(int i = 0; i <
	 *            parameterTypes.length; i++) { parameterClasses[i] = Class.forName(parameterTypes[i].getClassName()); }
	 *            final Method method = clazz.getDeclaredMethod(methodName, parameterClasses); return
	 *            ((method.getModifiers() & Modifier.STATIC) > 0); } catch(ClassNotFoundException |
	 *            NoSuchMethodException | SecurityException e) { LOGGER.error("Failed to check if " + className + "." +
	 *            methodName + " (" + methodSignature + ") is static:", e); return false; } }
	 */

	/**
	 * 
	 * @param expression
	 * @return
	 * 
	 * @see http://docs.oracle.com/javase/8/docs/api/java/lang/invoke/SerializedLambda.html
	 * @see http 
	 *      ://stackoverflow.com/questions/21860875/printing-debug-info-on-errors-with-java-8-lambda-expressions/21879031
	 *      #21879031
	 */
	private static <T> SerializedLambda getSerializedLambda(final FilterExpression<T> expression) {
		final Class<?> cl = expression.getClass();
		try {
			// TODO: use invokedynamic ? does it make sense at all ?
			Method m = cl.getDeclaredMethod("writeReplace");
			m.setAccessible(true);
			Object replacement = m.invoke(expression);
			if (replacement instanceof SerializedLambda) {
				final SerializedLambda serializedLambda = (SerializedLambda) replacement;
				LOGGER.debug(" Lambda FunctionalInterface: {}.{} ({})", serializedLambda.getFunctionalInterfaceClass(),
						serializedLambda.getFunctionalInterfaceMethodName(),
						serializedLambda.getFunctionalInterfaceMethodSignature());
				LOGGER.debug(" Lambda Implementation: {}.{} ({})", serializedLambda.getImplClass(),
						serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature());
				for (int i = 0; i < serializedLambda.getCapturedArgCount(); i++) {
					LOGGER.debug("  with Captured Arg(" + i + "): " + serializedLambda.getCapturedArg(i));
				}
				return serializedLambda;
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			LOGGER.error("Failed to find the Serialized form for th given Lambda Expression", e);
		}
		return null;
	}

	/**
	 * 
	 * @param instruction
	 *            the instruction to read
	 * @param expressionStack
	 *            the expression stack to put on or pop from.
	 * @param localVariables
	 *            the local variables
	 * @param labels
	 *            the labels
	 * @return
	 */
	private Statement readStatementSubTree(final AbstractInsnNode instruction, final Stack<Expression> expressionStack,
			final List<Object> capturedArgs, final List<LocalVariableNode> localVariables,
			final Map<String, AbstractInsnNode> labels) {
		AbstractInsnNode currentInstruction = instruction;
		while (currentInstruction != null) {
			switch (currentInstruction.getType()) {
			case AbstractInsnNode.VAR_INSN:
				final VarInsnNode varInstruction = (VarInsnNode) currentInstruction;
				if (varInstruction.var < capturedArgs.size()) {
					final Object capturedArg = capturedArgs.get(varInstruction.var);
					expressionStack.add(new CapturedArgument(capturedArg));
				} else {
					final LocalVariableNode var = localVariables.get(varInstruction.var);
					expressionStack.add(new LocalVariable(var.name, var.desc));
				}
				break;
			case AbstractInsnNode.LDC_INSN:
				// let's move this instruction on top of the stack until it
				// is used as an argument during a method call
				final LdcInsnNode ldcInsnNode = (LdcInsnNode) currentInstruction;
				// FIXME: use whatever possible to determine the type of literal
				// to instantiate
				final StringLiteral constant = new StringLiteral((String) ldcInsnNode.cst);
				LOGGER.debug("Stacking constant {}", constant);
				expressionStack.add(constant);
				break;
			case AbstractInsnNode.METHOD_INSN:
				final MethodInsnNode methodInsnNode = (MethodInsnNode) currentInstruction;
				final int argsNumber = Type.getArgumentTypes(methodInsnNode.desc).length;
				final List<Expression> args = new ArrayList<>();
				for (int i = 0; i < argsNumber; i++) {
					args.add(expressionStack.pop());
				}
				// arguments appear in reverse order in the bytecode
				Collections.reverse(args);
				switch (methodInsnNode.getOpcode()) {
				case Opcodes.INVOKEVIRTUAL:
					final MethodInvocation invokedVirtualMethod = new MethodInvocation(expressionStack.pop(),
							methodInsnNode.name, args);
					expressionStack.add(invokedVirtualMethod);
					break;
				case Opcodes.INVOKESPECIAL:
					final MethodInvocation invokedSpecialMethod = new MethodInvocation(expressionStack.pop(),
							methodInsnNode.name, args);
					expressionStack.add(invokedSpecialMethod);
					break;
				case Opcodes.INVOKESTATIC:
					final LocalVariableNode var = localVariables.get(0);
					final Type type = Type.getType(var.desc);
					MethodInvocation invokedStaticMethod;
					try {
						invokedStaticMethod = new MethodInvocation(new CapturedArgument(Class.forName(type.getClassName())),
								methodInsnNode.name, args);
						expressionStack.add(invokedStaticMethod);
					} catch (ClassNotFoundException e) {
						LOGGER.error("Failed to retrieve class for " + var.name, e);
					}
					break;
				default:
					LOGGER.error("Unexpected method invocation type: {}", methodInsnNode.getOpcode());
				}
				break;
			case AbstractInsnNode.JUMP_INSN:
				return readJumpInstruction(currentInstruction, expressionStack, capturedArgs, localVariables, labels);
			case AbstractInsnNode.INSN:
				final Statement instructionStmt = readInstruction(currentInstruction, expressionStack, localVariables,
						labels);
				if (instructionStmt != null) {
					return instructionStmt;
				}
				break;
			default:
				LOGGER.error(
						"Ouch, this is embarrassing... We've reached an unsupported (or unexpected) instruction operator: {}",
						currentInstruction.getOpcode());
			}
			currentInstruction = currentInstruction.getNext();

		}
		return null;
	}

	/**
	 * Reads the current {@link AbstractInsnNode} instruction and returns a {@link Statement} or {@code null} if the
	 * instruction is not a full statement (in that case, the instruction is stored in the given Expression
	 * {@link Stack}).
	 * 
	 * @param instruction
	 *            the instruction to read
	 * @param expressionStack
	 *            the expression stack to put on or pop from.
	 * @param localVariables
	 *            the local variables
	 * @param labels
	 *            the labels
	 * @return a {@link Statement} or {@code null}
	 */
	private Statement readInstruction(final AbstractInsnNode instruction, final Stack<Expression> expressionStack,
			final List<LocalVariableNode> localVariables, final Map<String, AbstractInsnNode> labels) {
		final InsnNode insnNode = (InsnNode) instruction;
		switch (insnNode.getOpcode()) {
		case Opcodes.IRETURN:
			// here, we know that the return value should be a boolean literal
			// or an expression whose result will be a boolean, no matter what's
			// on the ExpressionStack (it should be converted)
			final Expression returnValue = convert(expressionStack.pop());
			return new ReturnStatement(returnValue);
		case Opcodes.ICONST_0:
			expressionStack.add(new NumberLiteral(0));
			break;
		case Opcodes.ICONST_1:
			expressionStack.add(new NumberLiteral(1));
			break;
		}
		// no statement to return for now, instruction was put on top of
		// ExpressionStack for further usage
		return null;
	}

	/**
	 * Converter Constructor: attempts to convert the given value {@link Expression} into a {@link BooleanLiteral} or a
	 * {@link MethodInvocation}, or throws an {@link IllegalArgumentException} if the conversion was not possible.
	 * 
	 * @param value
	 *            the initial value
	 * @return a {@link BooleanLiteral} or a {@link MethodInvocation}
	 * @throws IllegalArgumentException
	 *             if the conversion is not possible
	 */
	static Expression convert(final Expression value) throws IllegalArgumentException {
		switch (value.getExpressionType()) {
		case METHOD_INVOCATION:
		case BOOLEAN_LITERAL:
			return value;
		case NUMBER_LITERAL:
			final Number numberLiteralValue = ((NumberLiteral) value).getValue();
			if (numberLiteralValue.intValue() == 0) {
				return new BooleanLiteral(false);
			} else if (numberLiteralValue.intValue() == 1) {
				return new BooleanLiteral(true);
			}
		default:
			throw new IllegalArgumentException("Could not convert '" + value + "' into a Boolean Literal");
		}
	}

	/**
	 * The {@link AbstractInsnNode#getOpcode()} value should be one of {@code IFEQ}, {@code IFNE}, {@code IFLT},
	 * {@code IFGE}, {@code IFGT}, {@code IFLE}, {@code IF_ICMPEQ}, {@code IF_ICMPNE}, {@code IF_ICMPLT},
	 * {@code IF_ICMPGE}, {@code IF_ICMPGT}, {@code IF_ICMPLE}, {@code IF_ACMPEQ}, {@code IF_ACMPNE}, {@code GOTO},
	 * {@code JSR}, {@code IFNULL} or {@code IFNONNULL}
	 * 
	 * 
	 * @param instruction
	 * @param expressionStack
	 * @param localVariables
	 * @param labels
	 * @return
	 */
	private Statement readJumpInstruction(final AbstractInsnNode instruction, final Stack<Expression> expressionStack,
			final List<Object> capturedArgs, final List<LocalVariableNode> localVariables,
			final Map<String, AbstractInsnNode> labels) {
		final JumpInsnNode jumpInsnNode = (JumpInsnNode) instruction;
		final LabelNode jumpLabel = jumpInsnNode.label;
		final AbstractInsnNode jumpInstruction = labels.get(jumpLabel.getLabel().toString());
		switch (jumpInsnNode.getOpcode()) {
		case Opcodes.IFEQ:
		case Opcodes.IFNE:
			return buildEqualityStatement(jumpInsnNode, expressionStack, capturedArgs, localVariables, labels);
		case Opcodes.IF_ICMPLE:
		case Opcodes.IF_ICMPLT:
		case Opcodes.IF_ICMPGE:
		case Opcodes.IF_ICMPGT:
			return buildComparisonStatement(jumpInsnNode, expressionStack, capturedArgs, localVariables, labels);
		case Opcodes.GOTO:
			return readStatementSubTree(jumpInstruction, expressionStack, capturedArgs, localVariables, labels);
		default:
			LOGGER.error("Unexpected JumpInsnNode OpCode: {}", jumpInsnNode.getOpcode());
			return null;
		}

	}

	// FIXME: remove labels arg ? (and on other methods, too)
	private Statement buildComparisonStatement(final JumpInsnNode jumpInsnNode,
			final Stack<Expression> expressionStack, final List<Object> capturedArgs,
			final List<LocalVariableNode> localVariables, final Map<String, AbstractInsnNode> labels) {
		final LabelNode jumpLabel = jumpInsnNode.label;
		final AbstractInsnNode jumpInstruction = labels.get(jumpLabel.getLabel().toString());
		final Expression compareRightSideExpression = expressionStack.pop();
		final Expression compareLeftSideExpression = expressionStack.pop();
		final InfixExpression comparisonExpression = new InfixExpression(InfixOperator.from(jumpInsnNode.getOpcode()),
				compareLeftSideExpression, compareRightSideExpression);
		final Statement elseStatement = (Statement) readStatementSubTree(jumpInstruction, expressionStack,
				capturedArgs, localVariables, labels);
		final Statement thenStatement = (Statement) readStatementSubTree(jumpInsnNode.getNext(), expressionStack,
				capturedArgs, localVariables, labels);
		return new IfStatement(comparisonExpression.inverse(), thenStatement, elseStatement);
	}

	private Statement buildEqualityStatement(final JumpInsnNode jumpInsnNode, final Stack<Expression> expressionStack,
			final List<Object> capturedArgs, final List<LocalVariableNode> localVariables,
			final Map<String, AbstractInsnNode> labels) {
		final LabelNode jumpLabel = jumpInsnNode.label;
		final AbstractInsnNode jumpInstruction = labels.get(jumpLabel.getLabel().toString());
		final Expression equalityExpression = expressionStack.pop();
		final Statement thenStatement = (Statement) readStatementSubTree(jumpInsnNode.getNext(), expressionStack,
				capturedArgs, localVariables, labels);
		final Statement elseStatement = (Statement) readStatementSubTree(jumpInstruction, expressionStack,
				capturedArgs, localVariables, labels);
		switch (jumpInsnNode.getOpcode()) {
		case Opcodes.IFEQ:
			return new IfStatement(equalityExpression, thenStatement, elseStatement);
		case Opcodes.IFNE:
			return new IfStatement(equalityExpression.inverse(), thenStatement, elseStatement);
		default:
			LOGGER.error("Well, this is embarrassing, there's no reason we reached this case with JumpInsc OpCode=",
					jumpInsnNode.getOpcode());
			return null;
		}
	}

}