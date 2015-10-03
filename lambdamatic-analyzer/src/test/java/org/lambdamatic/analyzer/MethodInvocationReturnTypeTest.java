package org.lambdamatic.analyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.List_get;
import static org.lambdamatic.testutils.JavaMethods.Map_get;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.lambdamatic.SerializableConsumer;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.analyzer.ast.node.ExpressionStatement;
import org.lambdamatic.analyzer.ast.node.FieldAccess;
import org.lambdamatic.analyzer.ast.node.LambdaExpression;
import org.lambdamatic.analyzer.ast.node.LocalVariable;
import org.lambdamatic.analyzer.ast.node.MethodInvocation;
import org.lambdamatic.analyzer.ast.node.NumberLiteral;
import org.lambdamatic.analyzer.ast.node.StringLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.model.EmbeddedTestPojo;
import com.sample.model.TestPojo;

/**
 * Checks that after a method invocation on a {@link Collection}, the return type is what the developer specified
 * in the arguments of the declared {@link Collection} (using generics) and not {@link Object}.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class MethodInvocationReturnTypeTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvocationReturnTypeTest.class);

	private final LambdaExpressionAnalyzer analyzer = LambdaExpressionAnalyzer.getInstance();

	@Test
	public void shouldReturnDomainTypeOnList() throws IOException, NoSuchMethodException, SecurityException {
		// given
		final SerializableConsumer<TestPojo> expression = (SerializableConsumer<TestPojo>) (t -> t.elementList.get(0).field.equals("foo"));
		// when
		final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess e_dot_elementList = new FieldAccess(testPojo, "elementList");
		final MethodInvocation e_dot_elementList_dot_get0 = new MethodInvocation(e_dot_elementList, List_get, new NumberLiteral(0));
		final FieldAccess e_dot_elementList_dot_get0_dot_field = new FieldAccess(e_dot_elementList_dot_get0, "field");
		final Expression expected = new MethodInvocation(e_dot_elementList_dot_get0_dot_field, Object_equals, new StringLiteral("foo"));
		// verification
		LOGGER.info("Result: {}", resultExpression);
		assertThat(resultExpression.getBody()).containsExactly(new ExpressionStatement(expected));
		final ExpressionStatement statement = (ExpressionStatement) resultExpression.getBody().get(0);
		final MethodInvocation elementListGetFirstMethodInvocation = (MethodInvocation) statement.getExpression(); 
		final FieldAccess fieldAccess = (FieldAccess) elementListGetFirstMethodInvocation.getSource();
		assertThat(((MethodInvocation)(fieldAccess.getSource())).getReturnType()).isEqualTo(EmbeddedTestPojo.class);
	}

	@Test
	public void shouldReturnDomainTypeOnMap() throws IOException, NoSuchMethodException, SecurityException {
		// given
		final SerializableConsumer<TestPojo> expression = (SerializableConsumer<TestPojo>) (t -> t.elementMap.get("foo").field.equals("foo"));
		// when
		final LambdaExpression resultExpression = analyzer.analyzeExpression(expression);
		// then
		final LocalVariable testPojo = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess e_dot_elementMap = new FieldAccess(testPojo, "elementMap");
		final MethodInvocation e_dot_elementList_dot_getFoo = new MethodInvocation(e_dot_elementMap, Map_get, new StringLiteral("foo"));
		final FieldAccess e_dot_elementList_dot_getFoo_dot_field = new FieldAccess(e_dot_elementList_dot_getFoo, "field");
		final Expression expected = new MethodInvocation(e_dot_elementList_dot_getFoo_dot_field, Object_equals, new StringLiteral("foo"));
		// verification
		LOGGER.info("Result: {}", resultExpression);
		assertThat(resultExpression.getBody()).containsExactly(new ExpressionStatement(expected));
		final ExpressionStatement statement = (ExpressionStatement) resultExpression.getBody().get(0);
		final MethodInvocation elementListGetFirstMethodInvocation = (MethodInvocation) statement.getExpression(); 
		final FieldAccess fieldAccess = (FieldAccess) elementListGetFirstMethodInvocation.getSource();
		assertThat(((MethodInvocation)(fieldAccess.getSource())).getReturnType()).isEqualTo(EmbeddedTestPojo.class);
	}
}

