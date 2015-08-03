/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.testutils.JavaMethods.Object_equals;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getEnumPojo;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getPrimitiveIntValue;
import static org.lambdamatic.testutils.JavaMethods.TestPojo_getStringValue;

import java.util.Collection;

import org.junit.Test;
import org.lambdamatic.analyzer.ast.node.CompoundExpression.CompoundExpressionOperator;

import com.sample.model.EnumPojo;
import com.sample.model.TestPojo;

/**
 * @author xcoulon
 *
 */
public class CompoundExpressionSimplificationTest {

	@Test
	public void shouldApplyIdempotentLawWithConditionnalOROnTwoOperands() {
		// given '(a + a)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsA1Method = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsA2Method = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsA1Method, equalsA2Method);
		// when
		final Collection<Expression> results = expression.applyIdempotentLaw();
		// then expect '(a)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(equalsA1Method);
	}

	@Test
	public void shouldApplyIdempotentLawWithConditionnalOROnThreeOperands() {
		// given '(a + a + b)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsA1Method = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsA2Method = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsA1Method, equalsA2Method, equalsBMethod);
		// when
		final Collection<Expression> results = expression.applyIdempotentLaw();
		// then expect '(a)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsA1Method, equalsBMethod));
	}
	
	@Test
	public void shouldApplyIdempotentLawWithConditionnalANDOnTwoOperands() {
		// given '(a.a)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsA1Method = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsA2Method = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsA1Method, equalsA2Method);
		// when
		final Collection<Expression> results = expression.applyIdempotentLaw();
		// then expect '(a)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(equalsA1Method);
	}

	@Test
	public void shouldApplyIdempotentLawWithConditionnalANDOnThreeOperands() {
		// given '(a.a.b)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsA1Method = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsA2Method = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsA1Method, equalsA2Method, equalsBMethod);
		// when
		final Collection<Expression> results = expression.applyIdempotentLaw();
		// then expect '(a.b)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsA1Method, equalsBMethod));
	}
	
	@Test
	public void shouldApplyAssociativeLawWithConditionnalOR() {
		// given '(a + (b + c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyAssociativeLaw();
		// then expect '(a + b + c)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod, equalsCMethod));
	}

	@Test
	public void shouldNotApplyAssociativeLawWithConditionnalOR() {
		// given '(a + (b . c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyAssociativeLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyAssociativeLawWithConditionnalAND() {
		// given '(a . (b . c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyAssociativeLaw();
		// then expect '(a.b.c)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod, equalsCMethod));
	}

	@Test
	public void shouldNotApplyAssociativeLawWithConditionnalAND() {
		// given '(a . (b + c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyAssociativeLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyEmptySetLawsOnConditionalOROnTwoOperands() {
		// given '(a + O)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final BooleanLiteral emptySetOperator = new BooleanLiteral(false);
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, emptySetOperator);
		// when
		final Collection<Expression> results = expression.applyEmptySetLaw();
		// then expect '(a)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(equalsAMethod);
	}

	@Test
	public void shouldApplyEmptySetLawsOnConditionalOROnThreeOperands() {
		// given '(a + b + O)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final BooleanLiteral emptySetOperator = new BooleanLiteral(false);
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod, emptySetOperator);
		// when
		final Collection<Expression> results = expression.applyEmptySetLaw();
		// then expect '(a + b)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod));
	}
	
	@Test
	public void shouldApplyEmptySetLawsOnConditionalANDOnTwoOperands() {
		// given '(a.O)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, BooleanLiteral.EMPTY_SET_OPERATOR);
		// when
		final Collection<Expression> results = expression.applyEmptySetLaw();
		// then expect '(O)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(BooleanLiteral.EMPTY_SET_OPERATOR);
	}

	@Test
	public void shouldApplyEmptySetLawsOnConditionalANDOnThreeOperands() {
		// given '(a.b.O)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod, BooleanLiteral.EMPTY_SET_OPERATOR);
		// when
		final Collection<Expression> results = expression.applyEmptySetLaw();
		// then expect '(O)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(BooleanLiteral.EMPTY_SET_OPERATOR);
	}
	
	@Test
	public void shouldApplyUniversalSetLawsOnConditionalOROnTwoOperands() {
		// given '(a + 1)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, BooleanLiteral.UNIVERSAL_OPERATOR);
		// when
		final Collection<Expression> results = expression.applyUniversalSetLaw();
		// then expect '(1)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(BooleanLiteral.UNIVERSAL_OPERATOR);
	}

	@Test
	public void shouldApplyUniversalSetLawsOnConditionalOROnThreeOperands() {
		// given '(a + b + 1)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod, BooleanLiteral.UNIVERSAL_OPERATOR);
		// when
		final Collection<Expression> results = expression.applyUniversalSetLaw();
		// then expect '(1)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(BooleanLiteral.UNIVERSAL_OPERATOR);
	}
	
	@Test
	public void shouldApplyUniversalSetLawsOnConditionalANDWithTwoOperands() {
		// given '(a.1)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, BooleanLiteral.UNIVERSAL_OPERATOR);
		// when
		final Collection<Expression> results = expression.applyUniversalSetLaw();
		// then expect '(a)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(equalsAMethod);
		assertThat(results.iterator().next().getId()).isEqualTo(expression.getId());
	}

	@Test
	public void shouldApplyUniversalSetLawsOnConditionalANDWithThreeOperands() {
		// given '(a.b.1)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod, BooleanLiteral.UNIVERSAL_OPERATOR);
		// when
		final Collection<Expression> results = expression.applyUniversalSetLaw();
		// then expect '(a.b)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod));
	}

	@Test
	public void shouldApplyUnaryOperationLawsOnConditionalOROnTwoOperands() {
		// given '(a + !a)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A")).inverse();
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, notEqualsAMethod);
		// when
		final Collection<Expression> results = expression.applyUnaryOperationLaw();
		// then expect '(1)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(BooleanLiteral.UNIVERSAL_OPERATOR);
	}

	@Test
	public void shouldApplyUnaryOperationLawsOnConditionalOROnThreeOperands() {
		// given '(a + !a + b)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A")).inverse();
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, notEqualsAMethod, equalsBMethod);
		// when
		final Collection<Expression> results = expression.applyUnaryOperationLaw();
		// then expect '(1)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(equalsBMethod);
	}
	
	@Test
	public void shouldApplyUnaryOperationLawsOnConditionalANDOnTwoOperands() {
		// given '(a . !a)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A")).inverse();
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, notEqualsAMethod);
		// when
		final Collection<Expression> results = expression.applyUnaryOperationLaw();
		// then expect '(0)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(BooleanLiteral.EMPTY_SET_OPERATOR);
	}

	@Test
	public void shouldApplyUnaryOperationLawsOnConditionalANDOnThreeOperands() {
		// given '(a . !a . b)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A")).inverse();
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, notEqualsAMethod, equalsBMethod);
		// when
		final Collection<Expression> results = expression.applyUnaryOperationLaw();
		// then expect '(0)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(BooleanLiteral.EMPTY_SET_OPERATOR);
	}

	@Test
	public void shouldApplyAbsorptionLawOnAllOperandsOnConditionalAND() {
		// given '(a.(a + b).(a + c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyAbsorptionLaw();
		// then expect '(a)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(equalsAMethod);
	}

	@Test
	public void shouldNotApplyAbsorptionLawOnAllOperandsOnConditionalAND() {
		// given '(a.(a.b).(a.c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyAbsorptionLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyAbsorptionLawOnSomeOperandsOnConditionalAND() {
		// given '(a.(a + b).(a + c).d)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Collection<Expression> results = expression.applyAbsorptionLaw();
		// then expect '(a.d)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsDMethod));
	}

	@Test
	public void shouldNotApplyAbsorptionLawOnSomeOperandsOnConditionalAND() {
		// given '(a.(a.b).(a.c).d)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Collection<Expression> results = expression.applyAbsorptionLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyAbsorptionLawOnAllOperandsOnConditionalOR() {
		// given '(a + (a.b) + (a.c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyAbsorptionLaw();
		// then expect '(a)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(equalsAMethod);
	}

	@Test
	public void shouldNotApplyAbsorptionLawOnAllOperandsOnConditionalOR() {
		// given '(a + (a + b) + (a + c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyAbsorptionLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyAbsorptionLawOnSomeOperandsOnConditionalOR() {
		// given '(a + (a.b) + (a.c) + d)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Collection<Expression> results = expression.applyAbsorptionLaw();
		// then expect '(a + d)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsDMethod));
	}

	@Test
	public void shouldNotApplyAbsorptionLawOnSomeOperandsOnConditionalOR() {
		// given '(a + (a + b) + (a + c) + d)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Collection<Expression> results = expression.applyAbsorptionLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyRedundancyLawOnAllMethodInvocationOperandsOnConditionalAND() {
		// given '(a.(!a + b).(!a + c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A")).inverse();
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, notEqualsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				notEqualsAMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect '(a.b.c)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod, equalsCMethod));
	}

	@Test
	public void shouldNotApplyRedundancyLawOnAllOperandsOnConditionalAND() {
		// given '(a.(a + !b).(a + !c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B")).inverse();
		final MethodInvocation notEqualsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C")).inverse();
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, notEqualsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, notEqualsCMethod));
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyRedundancyLawOnSomeOperandsOnConditionalAND() {
		// given '(a.(!a + b).(!a + c).d)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A")).inverse();
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, notEqualsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				notEqualsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect '(a.b.c.d)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod, equalsCMethod, equalsDMethod));
	}

	@Test
	public void shouldNotApplyRedundancyLawOnSomeOperandsOnConditionalAND() {
		// given '(a.(a + b).(a + c).d)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyRedundancyLawOnAllMethodInvocationOperandsOnConditionalOR() {
		// given '(a + (!a.!b) + (!a.c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A")).inverse();
		final MethodInvocation notEqualsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B")).inverse();
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, notEqualsAMethod, notEqualsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				notEqualsAMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect '(a + !b + c)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, notEqualsBMethod, equalsCMethod));
	}

	@Test
	public void shouldApplyRedundancyLawOnAllComplexInfixExpressionOperandsOnConditionalOR() {
		// given '(p.(!f+!e) + (f.e))' (ie, same as: a.!b + b = a + b)
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final CompoundExpression primitiveIntValueEquals42Expression = new CompoundExpression(CompoundExpressionOperator.EQUALS, new FieldAccess(var, "primitiveIntValue"), new NumberLiteral(42));
		final CompoundExpression fieldEqualsFooExpression = new CompoundExpression(CompoundExpressionOperator.EQUALS, new FieldAccess(var, "field"), new StringLiteral("FOO"));
		final CompoundExpression enumPojoEqualsFOOExpression = new CompoundExpression(CompoundExpressionOperator.EQUALS, new FieldAccess(var, "enumPojo"), new EnumLiteral(EnumPojo.FOO));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, primitiveIntValueEquals42Expression,
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, fieldEqualsFooExpression.inverse(), enumPojoEqualsFOOExpression.inverse())),
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, fieldEqualsFooExpression, enumPojoEqualsFOOExpression));
				
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect 'p + (f.e)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
						primitiveIntValueEquals42Expression,
						new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, fieldEqualsFooExpression, enumPojoEqualsFOOExpression)));
	}
	
	@Test
	public void shouldNotApplyRedundancyLawOnAllOperandsOnConditionalOR() {
		// given '(a + (a.b) + (a.c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsCMethod));
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyRedundancyLawOnSomeOperandsOnConditionalOR() {
		// given '(a + (!a.!b) + (!a.c) + d)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A")).inverse();
		final MethodInvocation notEqualsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B")).inverse();
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, notEqualsAMethod, notEqualsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				notEqualsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect '(a + b + c + d)'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, notEqualsBMethod, equalsCMethod, equalsDMethod));
	}

	@Test
	public void shouldNotApplyRedundancyLawOnSomeOperandsOnConditionalOR() {
		// given '(a + (a.b) + (a.c) + d)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Collection<Expression> results = expression.applyRedundancyLaw();
		// then expect no result
		assertThat(results).hasSize(0);
	}

	@Test
	public void shouldApplyFactorizationLawOnAllOperandsOnConditionalOR() {
		// given '((a.b) + (a.c) + (a.d))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsCMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsDMethod));
		// when
		final Collection<Expression> results = expression.applyFactorizationLaw();
		// then expect '(a.(b + c + d))'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
						equalsBMethod, equalsCMethod, equalsDMethod)));
	}

	@Test
	public void shouldApplyFactorizationLawOnSomeOperandsOnConditionalOR() {
		// given '((a.b) + (b.d) + (a.c) )'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsCMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod, equalsDMethod));
		// when
		final Collection<Expression> results = expression.applyFactorizationLaw();
		// then expect '(a.(b + c) + (b.d))' and '(b.(a + d) + (a.c))'
		assertThat(results).hasSize(2);
		final CompoundExpression expectedExpression1 = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod,
						equalsCMethod)), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod, equalsDMethod));
		final CompoundExpression expectedExpression2 = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod,
						equalsDMethod)), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod));
		assertThat(results).contains(expectedExpression1, expectedExpression2);
	}

	@Test
	public void shouldApplyFactorizationLawOnAllOperandsOnConditionalAND() {
		// given '((a + b).(a + c).(a + d))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, equalsCMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsDMethod));
		// when
		final Collection<Expression> results = expression.applyFactorizationLaw();
		// then expect '(a + (b.c.d))'
		assertThat(results).hasSize(1);
		assertThat(results).contains(
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
						equalsBMethod, equalsCMethod, equalsDMethod)));
	}

	@Test
	public void shouldApplyFactorizationLawOnSomeOperandsOnConditionalAND() {
		// given '((a + b).(b + d).(a + c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsAMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, Object_equals,  new StringLiteral("D"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, equalsCMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod, equalsDMethod));
		// when
		final Collection<Expression> results = expression.applyFactorizationLaw();
		// then expect '((a + (b.c)).(b + d))' and '((b + (a.c)).(a + c))'
		assertThat(results).hasSize(2);
		final CompoundExpression expectedExpression1 = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod,
						equalsCMethod)), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod, equalsDMethod));
		final CompoundExpression expectedExpression2 = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsBMethod, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod,
						equalsDMethod)), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod));
		assertThat(results).contains(expectedExpression1, expectedExpression2);
	}

	// ******************************************************************************************************
	// more global tests including mixtures of expressions.
	// ******************************************************************************************************
	@Test
	public void shouldSimplifyMixOfContionalOrIncludingConditionalAndOperandsWithFieldAccesses() {
		// given ((!a.c) + (a.b) + (a.!b.c))
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess fieldA = new FieldAccess(var, "fieldA");
		final FieldAccess fieldB = new FieldAccess(var, "fieldB");
		final FieldAccess fieldC = new FieldAccess(var, "fieldC");
		final MethodInvocation equalsAMethod = new MethodInvocation(fieldA, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(fieldB, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(fieldC, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod.inverse(), equalsCMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod.inverse(),
				equalsCMethod));
		// when
		final Expression result = expression.simplify();
		// then expect ((a.b) + c)
		final CompoundExpression expectedExpression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), equalsCMethod);
		assertThat(result).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldSimplifyUsingFactorizationThenUnaryOperatorLawsOnConditionalOR() {
		// given ((!a.c) + (a.b) + (a.c))
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess fieldA = new FieldAccess(var, "fieldA");
		final FieldAccess fieldB = new FieldAccess(var, "fieldB");
		final FieldAccess fieldC = new FieldAccess(var, "fieldC");
		final MethodInvocation equalsAMethod = new MethodInvocation(fieldA, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(fieldB, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(fieldC, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod.inverse(), equalsCMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND,
				equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod));
		// when
		final Expression result = expression.simplify();
		// then expect ((a.b) || c)
		final CompoundExpression expectedExpression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsCMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod));
		assertThat(result).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldSimplifyUsingFactorizationThenUnaryOperatorLawsOnConditionalAND() {
		// given ((!a + c).(a + b).(a + c))
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess fieldA = new FieldAccess(var, "fieldA");
		final FieldAccess fieldB = new FieldAccess(var, "fieldB");
		final FieldAccess fieldC = new FieldAccess(var, "fieldC");
		final MethodInvocation equalsAMethod = new MethodInvocation(fieldA, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(fieldB, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(fieldC, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod.inverse(), equalsCMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				equalsAMethod, equalsBMethod), new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod));
		// when
		final Expression result = expression.simplify();
		// then expect ((a + b).c)
		final CompoundExpression expectedExpression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsCMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod));
		assertThat(result).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldNotSimplifyMixOfContionalOrIncludingConditionalAndOperandsWithFieldAccesses() {
		// given '((a . b) + c))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess fieldA = new FieldAccess(var, "fieldA");
		final FieldAccess fieldB = new FieldAccess(var, "fieldB");
		final FieldAccess fieldC = new FieldAccess(var, "fieldC");
		final MethodInvocation equalsAMethod = new MethodInvocation(fieldA, Object_equals,  new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(fieldB, Object_equals,  new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(fieldC, Object_equals,  new StringLiteral("C"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod, equalsCMethod));
		// when
		final Expression result = expression.simplify();
		// then expect same expression
		final CompoundExpression expectedExpression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsAMethod, new CompoundExpression(
				CompoundExpressionOperator.CONDITIONAL_AND, equalsBMethod, equalsCMethod));
		assertThat(result).isEqualTo(expectedExpression);
	}

	@Test
	public void shouldApplyDistributiveLawOnConditionalORWithTwoMethodInvocationOperands() {
		// given '(foo.(bar + !baz)) + (!foo.!baz)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final FieldAccess fieldF = new FieldAccess(var, "f");
		final MethodInvocation equalsFoo = new MethodInvocation(fieldF, Object_equals,  new StringLiteral("foo"));
		final MethodInvocation equalsBar = new MethodInvocation(fieldF, Object_equals,  new StringLiteral("bar"));
		final MethodInvocation equalsBaz = new MethodInvocation(fieldF, Object_equals,  new StringLiteral("baz"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, 
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo, new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsBar, equalsBaz.inverse())),
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo.inverse(), equalsBaz.inverse()));
		// when
		final Collection<Expression> results = expression.applyDistributiveLaw();
		// then expect '(foo.bar + foo.!baz) + (!foo.!baz)' 
		final CompoundExpression expectedExpression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, 
						new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo, equalsBar), 
						new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo, equalsBaz.inverse())),
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo.inverse(), equalsBaz.inverse()));
		assertThat(results).hasSize(1);
		assertThat(results).contains(expectedExpression);
	}

	@Test
	public void shouldApplyDistributiveLawOnConditionalORWithTwoInfixExpressionOperands() {
		// given '((p.(f+ (!f.e))) + (!p.e))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final CompoundExpression primitiveIntValueEquals42Expression = new CompoundExpression(CompoundExpressionOperator.EQUALS, new FieldAccess(var, "primitiveIntValue"), new NumberLiteral(42));
		final CompoundExpression fieldEqualsFooExpression = new CompoundExpression(CompoundExpressionOperator.EQUALS, new FieldAccess(var, "field"), new StringLiteral("FOO"));
		final CompoundExpression enumPojoEqualsFOOExpression = new CompoundExpression(CompoundExpressionOperator.EQUALS, new FieldAccess(var, "enumPojo"), new EnumLiteral(EnumPojo.FOO));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, primitiveIntValueEquals42Expression,
						new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, fieldEqualsFooExpression, 
								new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, fieldEqualsFooExpression.inverse(), enumPojoEqualsFOOExpression))),
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, primitiveIntValueEquals42Expression.inverse(), enumPojoEqualsFOOExpression));

		// when
		final Collection<Expression> results = expression.applyDistributiveLaw();
		// then expect should expect: '((p.f) + (p.!f.e) + (!p.e))' 
		final CompoundExpression expectedExpression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR,
						new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, primitiveIntValueEquals42Expression, fieldEqualsFooExpression),
						new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, primitiveIntValueEquals42Expression, 
								new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, fieldEqualsFooExpression.inverse(), enumPojoEqualsFOOExpression))),
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, primitiveIntValueEquals42Expression.inverse(), enumPojoEqualsFOOExpression));
		assertThat(results).hasSize(1);
		assertThat(results).contains(expectedExpression);
	}
	
	@Test
	public void shouldNotApplyDistributiveLawOnConditionalANDWithThreeOperands() {
		// given '((foo.bar) + (foo.(!bar.!baz)))'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFoo = new MethodInvocation(var, Object_equals,  new StringLiteral("foo"));
		final MethodInvocation equalsBar = new MethodInvocation(var, Object_equals,  new StringLiteral("bar"));
		final MethodInvocation equalsBaz = new MethodInvocation(var, Object_equals,  new StringLiteral("baz"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, 
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo, equalsBar),
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo, 
						new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsBar.inverse(), equalsBaz.inverse())));
		// when
		final Collection<Expression> results = expression.applyDistributiveLaw();
		// then expect no result (associative law may be used, but not in this test)
		assertThat(results).hasSize(0);
	}
	
	
	@Test
	public void shouldSimplifyToConditionalORWithThreeOperands() {
		// given (foo + (!foo.bar) + (!foo.!bar.baz))
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation equalsFoo = new MethodInvocation(var, Object_equals,  new StringLiteral("foo"));
		final MethodInvocation equalsBar = new MethodInvocation(var, Object_equals,  new StringLiteral("bar"));
		final MethodInvocation equalsBaz = new MethodInvocation(var, Object_equals,  new StringLiteral("baz"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsFoo,
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo.inverse(), equalsBar),
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFoo.inverse(), equalsBar.inverse(), equalsBaz));
		// when
		final Expression result = expression.simplify();
		// then expect (foo || bar || baz)
		final CompoundExpression expectedExpression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsFoo, equalsBar, equalsBaz);
		assertThat(result).isEqualTo(expectedExpression);
	}
	
	@Test
	public void shouldNotApplyDistributiveLawOnConditionalORWithThreeOperands() {
		// given '(foo + (!foo.bar) + (!foo.bar.!baz)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(var, TestPojo_getStringValue);
		final CompoundExpression equalsFooExpr = new CompoundExpression(CompoundExpressionOperator.EQUALS, getStringValueMethod, new StringLiteral("foo"));
		final CompoundExpression equalsBarExpr = new CompoundExpression(CompoundExpressionOperator.EQUALS, getStringValueMethod, new StringLiteral("bar"));
		final CompoundExpression equalsBazExpr = new CompoundExpression(CompoundExpressionOperator.EQUALS, getStringValueMethod, new StringLiteral("baz"));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, equalsFooExpr, 
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFooExpr.inverse(), equalsBarExpr), 
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, equalsFooExpr.inverse(), equalsBarExpr.inverse(), equalsBazExpr));
				
		// when
		final Collection<Expression> results = expression.applyDistributiveLaw();
		// then expect nothing 
		assertThat(results).hasSize(0);
	}
	
	@Test
	public void shouldSimplifyToConditionalORWithThreeOperands2() {
		// given '(foo + (!foo.bar) + (!foo.bar.!baz)'
		final LocalVariable var = new LocalVariable(0, "t", TestPojo.class);
		final MethodInvocation getStringValueMethod = new MethodInvocation(var, TestPojo_getStringValue);
		final MethodInvocation getEnumPojoMethod = new MethodInvocation(var, TestPojo_getEnumPojo);
		final MethodInvocation getPrimitiveIntMethod = new MethodInvocation(var, TestPojo_getPrimitiveIntValue);
		final CompoundExpression getStringValueMethodEqualsFoo = new CompoundExpression(CompoundExpressionOperator.EQUALS, getStringValueMethod, new StringLiteral("foo"));
		final CompoundExpression getEnumPojoMethodEqualsBar = new CompoundExpression(CompoundExpressionOperator.EQUALS, getEnumPojoMethod, new EnumLiteral(EnumPojo.BAR));
		final CompoundExpression getPrimitiveIntMethodEquals42 = new CompoundExpression(CompoundExpressionOperator.EQUALS, getPrimitiveIntMethod, new NumberLiteral(42));
		final CompoundExpression expression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, getPrimitiveIntMethodEquals42, 
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsBar), 
				new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_AND, getPrimitiveIntMethodEquals42.inverse(), getEnumPojoMethodEqualsBar.inverse(), getStringValueMethodEqualsFoo));
		// when
		final Expression result = expression.simplify();
		// then expect (foo || bar || baz)
		final CompoundExpression expectedExpression = new CompoundExpression(CompoundExpressionOperator.CONDITIONAL_OR, getPrimitiveIntMethodEquals42, getEnumPojoMethodEqualsBar, getStringValueMethodEqualsFoo);
		assertThat(result).isEqualTo(expectedExpression);
	}
}

