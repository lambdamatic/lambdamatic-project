package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.lambdamatic.LambdaExpression;
import org.lambdamatic.SerializableFunction;
import org.lambdamatic.analyzer.ast.node.ArrayVariable;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
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

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(IsolatedLambdaBytecodeAnalyzerTest.class);

	private final LambdaExpressionAnalyzer analyzer = LambdaExpressionAnalyzer.getInstance();

	@Test
	public void shouldParseExpression() throws IOException {
		// given
		final SerializableFunction<TestPojo, Object[]> expression = (SerializableFunction<TestPojo, Object[]>) ((
				TestPojo t) -> ArrayUtil.toArray(t.stringValue, t.dateValue));
		// when
		final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final ArrayVariable methodArgs = new ArrayVariable(Object[].class, new FieldAccess(testPojo, "stringValue"), new FieldAccess(testPojo, "dateValue"));
		final MethodInvocation result = new MethodInvocation(new ClassLiteral(ArrayUtil.class), "toArray", Object[].class, methodArgs);
		// verification
		assertThat(resultExpression.getExpression()).isEqualTo(result);
	}
}

