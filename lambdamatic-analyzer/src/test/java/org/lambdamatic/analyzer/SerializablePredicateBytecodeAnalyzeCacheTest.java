/**
 * 
 */
package org.lambdamatic.analyzer;

import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getStringValue;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.analyzer.ast.node.InfixExpression;
import org.lambdamatic.analyzer.ast.node.InfixExpression.InfixOperator;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.ReturnStatement;
import org.lambdamatic.analyzer.ast.node.StringLiteral;

import com.sample.model.TestPojo;

import net.jcip.annotations.NotThreadSafe;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
@NotThreadSafe
public class SerializablePredicateBytecodeAnalyzeCacheTest {

	private LambdaExpressionAnalyzerListenerImpl listener;
	private LambdaExpressionAnalyzer lambdaAnalyzer;

	@Before
	public void registerListener() {
		listener = new LambdaExpressionAnalyzerListenerImpl();
		lambdaAnalyzer = LambdaExpressionAnalyzer.getInstance();
		lambdaAnalyzer.addListener(listener);
	}
	
	@After
	public void unregisterListener() {
		lambdaAnalyzer.removeListener(listener);
	}
	
	@Test
	public void shouldNotAnalyzeTwice() throws NoSuchMethodException, SecurityException {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation getStringValue = new MethodInvocation(testPojo, TestPojo_getStringValue);
		// when (first call) 
		final LambdaExpression lambdaExpression1 = getLambdaExpression();
		// then 
		Assertions.assertThat(lambdaExpression1.getBody()).containsExactly(new ReturnStatement(new InfixExpression(InfixOperator.CONDITIONAL_OR, 
				new MethodInvocation(getStringValue, Object_equals, new StringLiteral("john")),
				new MethodInvocation(getStringValue, Object_equals, new StringLiteral("jack"))
				)));
		Assertions.assertThat(listener.getCacheMisses()).isEqualTo(1);
		Assertions.assertThat(listener.getCacheHits()).isEqualTo(0);
		// given
		listener.resetHitCounters();
		// when (second call)
		final LambdaExpression lambdaExpression2 = getLambdaExpression();
		// then 
		Assertions.assertThat(lambdaExpression2.getBody()).containsExactly(new ReturnStatement(new InfixExpression(InfixOperator.CONDITIONAL_OR, 
				new MethodInvocation(getStringValue, Object_equals, new StringLiteral("john")),
				new MethodInvocation(getStringValue, Object_equals, new StringLiteral("jack"))
				)));
		Assertions.assertThat(listener.getCacheMisses()).isEqualTo(0);
		Assertions.assertThat(listener.getCacheHits()).isEqualTo(1);
	}

	@Test
	public void shouldNotAnalyzeTwiceWithCapturedArguments() throws NoSuchMethodException, SecurityException {
		// given
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation getStringValue = new MethodInvocation(testPojo, TestPojo_getStringValue);
		// when (first call) 
		final LambdaExpression lambdaExpression1 = getLambdaExpression("john1", "jack1");
		// then 
		Assertions.assertThat(lambdaExpression1.getBody()).containsExactly(new ReturnStatement(new InfixExpression(InfixOperator.CONDITIONAL_OR, 
				new MethodInvocation(getStringValue, Object_equals, new StringLiteral("john1")),
				new MethodInvocation(getStringValue, Object_equals, new StringLiteral("jack1"))
				)));
		Assertions.assertThat(listener.getCacheMisses()).isEqualTo(1);
		Assertions.assertThat(listener.getCacheHits()).isEqualTo(0);
		// given
		listener.resetHitCounters();
		// when (second call)
		final LambdaExpression lambdaExpression2 = getLambdaExpression("john2", "jack2");
		// then 
		Assertions.assertThat(lambdaExpression2.getBody()).containsExactly(new ReturnStatement(new InfixExpression(InfixOperator.CONDITIONAL_OR, 
				new MethodInvocation(getStringValue, Object_equals, new StringLiteral("john2")),
				new MethodInvocation(getStringValue, Object_equals, new StringLiteral("jack2"))
				)));
		Assertions.assertThat(listener.getCacheMisses()).isEqualTo(0);
		Assertions.assertThat(listener.getCacheHits()).isEqualTo(1);
		
	}
	
	private LambdaExpression getLambdaExpression() {
		// given
		final SerializablePredicate<TestPojo> expr = ((TestPojo t) -> t.getStringValue().equals("john") || t.getStringValue().equals("jack"));
		// when
		return LambdaExpressionAnalyzer.getInstance().analyzeExpression(expr);
	}

	private LambdaExpression getLambdaExpression(final String stringField1, final String stringField2) {
		// given
		final SerializablePredicate<TestPojo> expr = ((TestPojo t) -> t.getStringValue().equals(stringField1) || t.getStringValue().equals(stringField2));
		// when
		return LambdaExpressionAnalyzer.getInstance().analyzeExpression(expr);
	}

}
