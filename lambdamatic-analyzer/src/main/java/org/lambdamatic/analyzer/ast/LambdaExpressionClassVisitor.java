package org.lambdamatic.analyzer.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal Utility Class that locates and reads the bytecode associated with the lambda expression converted into a
 * classic Java method in the capturing class at compilation time.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 * 
 */
class LambdaExpressionClassVisitor extends ClassVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LambdaExpressionClassVisitor.class);

	private final String lambdaImplClassName;
	private final String lambdaImplMethodName;
	private final String lambdaImplMethodSignature;
	private DesugaredLambdaExpressionMethodVisitor desugaredLambdaExpressionMethodVisitor;

	/** Flag to indicate if the caller class to the lambda expression is an interface.*/
	private final boolean isInterface;

	/**
	 * Constructor
	 * @param lambdaImplClassName
	 * @param lambdaImplMethodName
	 * @param lambdaImplMethodSignature
	 */
	LambdaExpressionClassVisitor(final SerializedLambdaInfo lambdaInfo) {
		super(Opcodes.ASM5);
		this.lambdaImplClassName = lambdaInfo.getImplClassName();
		this.lambdaImplMethodName = lambdaInfo.getImplMethodName();
		this.lambdaImplMethodSignature = lambdaInfo.getImplMethodDesc();
		LOGGER.debug("About to analyze {}.{}", this.lambdaImplClassName, this.lambdaImplMethodName);
		this.isInterface = isInterface(this.lambdaImplClassName);
	}

	private boolean isInterface(final String targetClassName) {
		try {
			return Class.forName(targetClassName).isInterface();
		} catch (ClassNotFoundException e) {
			LOGGER.error("Could not resolve if {} is an interface", targetClassName, e);
		}
		return false;
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String methodName, final String desc, final String signature, final String[] exceptions) {
		//FIXME: should stop visiting methods once the target one has been found.
		if (methodName.equals(this.lambdaImplMethodName) && desc.equals(this.lambdaImplMethodSignature)) {
			LOGGER.trace("** Visiting {}.{} ({}) **", lambdaImplClassName, methodName, desc);
			this.desugaredLambdaExpressionMethodVisitor = new DesugaredLambdaExpressionMethodVisitor(this, desc);
			return desugaredLambdaExpressionMethodVisitor;
		}
		return null;
	}
	
	public InsnList getInstructions() {
		return desugaredLambdaExpressionMethodVisitor.getInstructions();
	}

	public List<LocalVariableNode> getLocalVariables() {
		return desugaredLambdaExpressionMethodVisitor.getLocalVariables();
	}

	public Map<String, AbstractInsnNode> getLabels() {
		return desugaredLambdaExpressionMethodVisitor.getLabels();
	}

	static class DesugaredLambdaExpressionMethodVisitor extends MethodVisitor {

		private final LambdaExpressionClassVisitor parentClassVisitor;
		
		private final InsnList instructions = new InsnList();

		private final List<LocalVariableNode> localVariables = new ArrayList<>();

		private final Map<String, AbstractInsnNode> labels = new HashMap<>();

		private Stack<String> pendingLabels = new Stack<>();
		
		/**
		 * Full constructor
		 * @param parentClassVisitor the parent ASM class visitor
		 * @param desc the desugared lambda method descriptor (parameters types and return type: @see {@link Type})
		 */
		DesugaredLambdaExpressionMethodVisitor(final LambdaExpressionClassVisitor parentClassVisitor, final String desc) {
			super(Opcodes.ASM5);
			this.parentClassVisitor = parentClassVisitor;
		}

		public Map<String, AbstractInsnNode> getLabels() {
			return labels;
		}

		public InsnList getInstructions() {
			return instructions;
		}

		public List<LocalVariableNode> getLocalVariables() {
			return localVariables;
		}

		private void addInstruction(final AbstractInsnNode instruction) {
			instructions.add(instruction);
			if (!pendingLabels.isEmpty()) {
				labels.put(pendingLabels.pop(), instruction);
			}
		}

		@Override
		public void visitAttribute(final Attribute attr) {
			LOGGER.trace("** IGNORED ** Attribute {}", attr);
		}

		@Override
		public void visitInsn(final int opcode) {
			LOGGER.trace("Insn {}", Printer.OPCODES[opcode]);
			addInstruction(new InsnNode(opcode));
		}

		@Override
		public void visitIntInsn(final int opcode, final int operand) {
			LOGGER.trace("IntInsn {} {}", Printer.OPCODES[opcode], operand);
			addInstruction(new IntInsnNode(opcode, operand));
		}

		@Override
		public void visitVarInsn(final int opcode, final int var) {
			LOGGER.trace("VarInsn {} #{}", Printer.OPCODES[opcode], var);
			addInstruction(new VarInsnNode(opcode, var));
		}

		@Override
		public void visitTypeInsn(final int opcode, final String type) {
			LOGGER.trace("TypeInsn {} {}", Printer.OPCODES[opcode], type);
			addInstruction(new TypeInsnNode(opcode, type));
		}

		@Override
		public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
			LOGGER.trace("InvokeDynamicInsn {} (desc={})", name, desc);
			addInstruction(new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs));
		}
		
		@Override
		public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
			LOGGER.trace("MethodInsn {} {}.{} (desc={})", Printer.OPCODES[opcode], owner, name, desc);
			addInstruction(new MethodInsnNode(opcode, owner, name, desc, this.parentClassVisitor.isInterface));
		}

		@Override
		public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
			LOGGER.trace("MethodInsn {} {}.{} (desc={})", Printer.OPCODES[opcode], owner, name, desc);
			addInstruction(new MethodInsnNode(opcode, owner, name, desc, this.parentClassVisitor.isInterface));
		}
		
		@Override
		public void visitLdcInsn(final Object cst) {
			LOGGER.trace("LdcInsn {}", cst);
			addInstruction(new LdcInsnNode(cst));
		}

		@Override
		public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
			LOGGER.trace("FieldInsn {} {}.{} (desc={})", Printer.OPCODES[opcode], owner, name, desc);
			addInstruction(new FieldInsnNode(opcode, owner, name, desc));
		}

		@Override
		public void visitIincInsn(final int var, final int increment) {
			LOGGER.trace("** IGNORED ** IincInsn #{} {}", var, increment);
		}

		@Override
		public void visitJumpInsn(final int opcode, final Label label) {
			LOGGER.trace("JumpInsn {} {}", Printer.OPCODES[opcode], label.toString());
			addInstruction(new JumpInsnNode(opcode, new LabelNode(label)));
		}

		@Override
		public void visitLabel(final Label label) {
			LOGGER.trace("Label {}", label);
			pendingLabels.add(label.toString());
		}

		@Override
		public void visitParameter(final String name, final int access) {
			LOGGER.trace("Parameter {} (ignored)", name);
		}

		@Override
		public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
			LOGGER.trace("LocalVariable {} (desc={}) index={}", name, desc, index);
			// fill localVariables list with null values if necessary (for example, if the method being visited is static, the slot #0 reserved to 'this' will remain empty.
			final int size = localVariables.size();
			for(int i = index; i >= size; i--) {
				localVariables.add(null);
			}
			localVariables.set(index, new LocalVariableNode(name, desc, signature, new LabelNode(start), new LabelNode(
					end), index));
		}
		
	}
}
