package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.lambdamatic.FilterExpression;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.model.TestPojo;

/**
 * <p>
 * Running test in an isolated class to simplify the bytecode reading.
 * </p>
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class IsolatedLambdaBytecodeAnalyzerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(IsolatedLambdaBytecodeAnalyzerTest.class);

	private final LambdaExpressionAnalyzer analyzer = new LambdaExpressionAnalyzer();

	@Test
	public void shouldParseExpression() throws IOException {
		// given
		final FilterExpression<TestPojo> expression = (TestPojo t) -> t.intValue == 42;
		// when
		final LambdaExpression resultExpression = analyzer.analyzeLambdaExpression(expression);
		// then
		LOGGER.info("Number of InfixExpressions used during the process: {}",
				(new InfixExpression(InfixOperator.CONDITIONAL_AND).getId() - 1));
		final LocalVariable testPojo = new LocalVariable("t", TestPojo.class);
		final FieldAccess testPojoField = new FieldAccess(testPojo, "intValue");
		final InfixExpression equals42Expression = new InfixExpression(InfixOperator.EQUALS, testPojoField, new NumberLiteral(42));
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(equals42Expression);
	}
}

