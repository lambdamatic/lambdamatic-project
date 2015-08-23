package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_elementMatch;

import java.io.IOException;

import org.junit.Test;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

import com.sample.model.TestPojo;

/**
 * Running test in an isolated class to simplify the bytecode reading.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class IsolatedLambdaBytecodeAnalyzerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(IsolatedLambdaBytecodeAnalyzerTest.class);

	private final LambdaExpressionAnalyzer analyzer = LambdaExpressionAnalyzer.getInstance();

	@Test
	public void shouldParseExpression() throws IOException, NoSuchMethodException, SecurityException {
		// given
		final TestPojo anotherPojo = new TestPojo();
		final SerializableConsumer<TestPojo> expression = (SerializableConsumer<TestPojo>) (t -> t.elementMatch(e -> e.field.equals(anotherPojo.getStringValue())));
		// when
		final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess e_dot_field = new FieldAccess(new LocalVariable(0, "e", TestPojo.class), "field");
		final LambdaExpression e_dot_field_equals_foo = new LambdaExpression(
				new ReturnStatement(new MethodInvocation(e_dot_field, Object_equals, new StringLiteral("foo"))),
				TestPojo.class, "e");
		
		final Expression expected = new MethodInvocation(testPojo, TestPojo_elementMatch, e_dot_field_equals_foo);
		// verification
		LOGGER.info("Result: {}", resultExpression);
		assertThat(resultExpression.getBody()).containsExactly(new ExpressionStatement(expected));
	}
}

