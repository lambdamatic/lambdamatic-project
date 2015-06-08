package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.analyzer.ast.node.Assignment;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.Operation;
import org.lambdamatic.analyzer.ast.node.Operation.Operator;
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
		final SerializableConsumer<TestPojo> expression = (SerializableConsumer<TestPojo>) ((
				TestPojo t) -> {t.stringValue = "foo"; t.field = "bar"; t.primitiveIntValue++;});
		// when
		final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
		// then
		final LocalVariable t = new LocalVariable(1, "t", TestPojo.class);
		final FieldAccess t_dot_stringValue = new FieldAccess(t, "stringValue");
		final ExpressionStatement set_t_dot_stringValue = new ExpressionStatement(new Assignment(t_dot_stringValue, new StringLiteral("foo")));
		final FieldAccess t_dot_field = new FieldAccess(t, "field");
		final ExpressionStatement set_t_dot_field = new ExpressionStatement(new Assignment(t_dot_field, new StringLiteral("bar")));
		final FieldAccess t_dot_primitiveIntValue = new FieldAccess(t, "primitiveIntValue");
		final ExpressionStatement inc_primitiveInt = new ExpressionStatement(new Assignment(t_dot_primitiveIntValue, new Operation(Operator.ADD, t_dot_primitiveIntValue, new NumberLiteral(1))));
		// verification
		LOGGER.info("Result: {}", resultExpression);
		assertThat(resultExpression.getArgumentName()).isEqualTo("t");
		assertThat(resultExpression.getArgumentType()).isEqualTo(TestPojo.class);
		assertThat(resultExpression.getBody()).containsExactly(set_t_dot_stringValue, set_t_dot_field, inc_primitiveInt);
	}
}

