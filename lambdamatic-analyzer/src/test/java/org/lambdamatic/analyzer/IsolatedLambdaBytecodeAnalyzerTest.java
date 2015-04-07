package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_elementMatch;

import java.io.IOException;

import org.junit.Test;
import org.lambdamatic.SerializableFunction;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
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
		final String foo = "foo";
		final SerializableFunction<TestPojo, Boolean> expression = (SerializableFunction<TestPojo, Boolean>) ((
				TestPojo t) -> t.elementMatch(e -> e.field.equals(foo)));
		// when
		final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
		// then
		final LocalVariable e = new LocalVariable(0, "e", TestPojo.class);
		final MethodInvocation fieldEqualsFooMethod = new MethodInvocation(new FieldAccess(e, "field"), Object_equals, new StringLiteral("foo"));
		final LambdaExpression nestedExpression = new LambdaExpression(fieldEqualsFooMethod, TestPojo.class);
		final LocalVariable t = new LocalVariable(1, "t", TestPojo.class);
		final MethodInvocation elementMatchMethod = new MethodInvocation(t, TestPojo_elementMatch, nestedExpression);
		final LambdaExpression result = new LambdaExpression(elementMatchMethod, TestPojo.class);
		// verification
		LOGGER.info("Result: {}", resultExpression);
		assertThat(resultExpression).isEqualTo(result);
	}
}

