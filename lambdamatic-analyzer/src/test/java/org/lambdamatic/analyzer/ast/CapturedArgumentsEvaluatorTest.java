/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveIntValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getStringValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.CapturedArgumentRef;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.StringLiteral;

import com.sample.model.TestPojo;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class CapturedArgumentsEvaluatorTest {

	@Test
	public void shouldSubstituteCapturedArgumentInTwoMethodInvocationSourceExpressions() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsMethodInvocation = new MethodInvocation(testPojo, Object_equals, new MethodInvocation(new CapturedArgument(new TestPojo()), TestPojo_getStringValue));
		final MethodInvocation equalsFieldAccess = new MethodInvocation(testPojo, Object_equals, new FieldAccess(new CapturedArgument(new TestPojo()), "field"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsMethodInvocation, equalsFieldAccess);
		// when
		final CapturedArgumentsEvaluator expressionRewriter = new CapturedArgumentsEvaluator(Collections.emptyList());
		expression.accept(expressionRewriter);
		// then
		final InfixExpression expectedResult = new InfixExpression(InfixOperator.CONDITIONAL_OR, new MethodInvocation(testPojo, Object_equals, 
				new StringLiteral("foo")), new MethodInvocation(testPojo, Object_equals, new StringLiteral("bar")));
		assertThat(expression).isEqualTo(expectedResult);
	}
	
	@Test
	public void shouldSubstituteOneCapturedArgumentInMethodInvocationSourceExpression() {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(new CapturedArgument(new TestPojo()), TestPojo_getStringValue);
		final MethodInvocation equalsGetStringValue = new MethodInvocation(testPojo, Object_equals, getStringValueMethod);
		// when
		final CapturedArgumentsEvaluator expressionRewriter = new CapturedArgumentsEvaluator(Collections.emptyList());
		equalsGetStringValue.accept(expressionRewriter);
		// then
		final MethodInvocation expectedResult = new MethodInvocation(testPojo, Object_equals, new StringLiteral("foo"));
		assertThat(equalsGetStringValue).isEqualTo(expectedResult);
	}
	
	@Test
	public void shouldSubstituteCapturedArgumentRefInMethodInvocationSourceExpression() {
		// given
		final List<Object> capturedArguments = Arrays.asList(new TestPojo()); 
		final MethodInvocation getStringValueMethod = new MethodInvocation(new CapturedArgumentRef(0, capturedArguments.get(0)), TestPojo_getStringValue);
		final MethodInvocation equalsGetStringValue = new MethodInvocation(getStringValueMethod, Object_equals, getStringValueMethod);
		// when
		final CapturedArgumentsEvaluator expressionRewriter = new CapturedArgumentsEvaluator(capturedArguments);
		equalsGetStringValue.accept(expressionRewriter);
		// then
		final MethodInvocation expectedResult = new MethodInvocation(new StringLiteral("foo"), Object_equals, new StringLiteral("foo"));
		assertThat(equalsGetStringValue).isEqualTo(expectedResult);
	}

	@Test
	public void shouldSubstituteCapturedArgumentRefInMethodInvocationArguments() {
		// given
		final List<Object> capturedArguments = Arrays.asList("bar", 42); 
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(testPojo, TestPojo_getStringValue);
		final MethodInvocation getStringValueEqualsBar = new MethodInvocation(getStringValueMethod, Object_equals, new CapturedArgumentRef(0, capturedArguments.get(0)));
		final MethodInvocation getPrimitiveIntValueMethod = new MethodInvocation(testPojo, TestPojo_getPrimitiveIntValue);
		final MethodInvocation getPrimitiveIntValueEquals42 = new MethodInvocation(getPrimitiveIntValueMethod, Object_equals, new CapturedArgumentRef(1, capturedArguments.get(1)));
		final InfixExpression actualExpr = new InfixExpression(InfixOperator.CONDITIONAL_OR, getStringValueEqualsBar, getPrimitiveIntValueEquals42);
		// when
		final CapturedArgumentsEvaluator expressionRewriter = new CapturedArgumentsEvaluator(capturedArguments);
		actualExpr.accept(expressionRewriter);
		// then
		final InfixExpression expectedExpr = new InfixExpression(InfixOperator.CONDITIONAL_OR, 
				new MethodInvocation(getStringValueMethod, Object_equals, new StringLiteral("bar")),
				new MethodInvocation(getPrimitiveIntValueMethod, Object_equals, new NumberLiteral(42)))
				;
		assertThat(actualExpr).isEqualTo(expectedExpr);
	}
	
}
