package org.lambdamatic.analyzer.ast;

import java.lang.reflect.Field;
import java.util.Optional;

import org.lambdamatic.analyzer.ast.node.BooleanLiteral;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.ComplexExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.Expression.ExpressionType;
import org.lambdamatic.analyzer.ast.node.ExpressionFactory;
import org.lambdamatic.analyzer.ast.node.ExpressionVisitor;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a new {@link Expression} where method calls such as
 * {@link Long#longValue()}, etc. are removed because they are not relevant in
 * our case (they are just underlying conversion methods) Finally,
 * {@link InfixExpression} with a boolean conditions (eg:
 * {@link MethodInvocation} == {@link BooleanLiteral}) are simplified as well:
 * the {@link InfixExpression} is replaced with the meaningful operand.
 *
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class LambdaExpressionRewriter extends ExpressionVisitor {

	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LambdaExpressionRewriter.class);

	/**
	 * If the {@link InfixExpression} with a boolean conditions (eg:
	 * {@link MethodInvocation} == {@link BooleanLiteral}), replace it with the meaningful operand (which may need to be inverted).
	 * 
	 * @param expr the {@link InfixExpression} to analyze
	 */
	@Override
	public boolean visitInfixExpression(final InfixExpression expr) {
		// manually visit all operands, first
		expr.getOperands().stream().forEach(operand -> operand.accept(this));
		if ((expr.getOperator() == InfixOperator.EQUALS || expr.getOperator() == InfixOperator.NOT_EQUALS)
				&& expr.getOperands().size() == 2
				&& expr.getOperands().stream().anyMatch(e -> e.getExpressionType() == ExpressionType.BOOLEAN_LITERAL)) {
			final ComplexExpression parentExpression = expr.getParent();
			final Optional<Expression> replacementExpr = expr.getOperands().stream().filter(e -> e.getExpressionType() != ExpressionType.BOOLEAN_LITERAL).findFirst();
			if(replacementExpr.isPresent()) {
				parentExpression.replaceElement(expr,
						(expr.getOperator() == InfixOperator.EQUALS) ? replacementExpr.get() : replacementExpr.get()
								.inverse());
			}
		}
		// the branch was already visited
		return true;
	}
	
	@Override
	public boolean visitMethodInvocationExpression(final MethodInvocation methodInvocation) {
		final Class<?> sourceType = methodInvocation.getSourceExpression().getJavaType();
		final String methodName = methodInvocation.getMethodName();
		if(methodInvocation.getArguments().isEmpty()) {
			if(sourceType.equals(Boolean.class) && methodName.equals("booleanValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Byte#byteValue() method
			else if(sourceType.equals(Byte.class) && methodName.equals("byteValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Short#shortValue() method
			else if(sourceType.equals(Short.class) && methodName.equals("shortValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Integer#intValue() method
			else if(sourceType.equals(Integer.class) && methodName.equals("intValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Long#longValue() method
			else if(sourceType.equals(Long.class) && methodName.equals("longValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Float#floatValue() method
			else if(sourceType.equals(Float.class) && methodName.equals("floatValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Double#doubleValue() method
			else if(sourceType.equals(Double.class) && methodName.equals("doubleValue")) {
				methodInvocation.delete();
			}
			// drop invocation of Character#charValue() method
			else if(sourceType.equals(Character.class) && methodName.equals("charValue")) {
				methodInvocation.delete();
			}
		} 
		return true;
	}

	@Override
	public boolean visitFieldAccessExpression(final FieldAccess fieldAccess) {
		if (fieldAccess.getSourceExpression().getExpressionType() == ExpressionType.CLASS_LITERAL) {
			final ClassLiteral sourceClass = (ClassLiteral) fieldAccess.getSourceExpression();
			final String fieldName = fieldAccess.getFieldName();
			try {
				final Class<?> source = sourceClass.getValue();
				final Field f = ReflectionUtils.getFieldToInvoke(source, fieldName);
				f.setAccessible(true);
				final Object replacement = f.get(source);
				final ComplexExpression parentExpression = fieldAccess.getParent();
				if (parentExpression != null) {
					final Expression fieldAccessReplacement = ExpressionFactory.getExpression(replacement);
					LOGGER.trace(" replacing {} ({}) with {} ({})", fieldAccess, fieldAccess.getExpressionType(), fieldAccessReplacement, fieldAccessReplacement.getExpressionType());  
					parentExpression.replaceElement(fieldAccess, fieldAccessReplacement);
				}
				// no further visiting on this (obsolete) branch of the expression tree.
				return false;
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
				LOGGER.error("Failed to execute method '{}' on class '{}'", fieldName, sourceClass.toString());
			}
			
		}
		return super.visitFieldAccessExpression(fieldAccess);
	}

}
