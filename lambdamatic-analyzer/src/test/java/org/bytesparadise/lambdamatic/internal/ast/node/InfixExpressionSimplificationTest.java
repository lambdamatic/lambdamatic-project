/**
 * 
 */
package org.bytesparadise.lambdamatic.internal.ast.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.bytesparadise.lambdamatic.internal.ast.BooleanAlgebraHelper;
import org.bytesparadise.lambdamatic.internal.ast.node.InfixExpression.InfixOperator;
import org.junit.Test;

/**
 * @author xcoulon
 *
 */
public class InfixExpressionSimplificationTest {

	@Test
	public void shouldApplyIdempotentLawWithConditionnalOR() {
		// given '(a + a)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsA1Method = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsA2Method = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsA1Method,
				equalsA2Method);
		// when
		final Expression result = BooleanAlgebraHelper.applyIdempotentLaws(expression);
		// then expect '(a)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsA1Method));
	}

	@Test
	public void shouldApplyIdempotentLawWithConditionnalAND() {
		// given '(a + a)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsA1Method = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsA2Method = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsA1Method,
				equalsA2Method);
		// when
		final Expression result = BooleanAlgebraHelper.applyIdempotentLaws(expression);
		// then expect '(a)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsA1Method));
	}

	@Test
	public void shouldApplyAssociativeLawWithConditionnalOR() {
		// given '(a + (b + c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsBMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyAssociativeLaws(expression);
		// then expect '(a + b + c)'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod, equalsCMethod));
	}

	@Test
	public void shouldNotApplyAssociativeLawWithConditionnalOR() {
		// given '(a + (b . c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsBMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyAssociativeLaws(expression);
		// then expect no change
		assertThat(result).isEqualTo(expression);
	}
	
	@Test
	public void shouldApplyAssociativeLawWithConditionnalAND() {
		// given '(a . (b . c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsBMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyAssociativeLaws(expression);
		// then expect '(a.b.c)'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod, equalsCMethod));
	}

	@Test
	public void shouldNotApplyAssociativeLawWithConditionnalAND() {
		// given '(a . (b + c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsBMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyAssociativeLaws(expression);
		// then expect no change
		assertThat(result).isEqualTo(expression);
	}
	
	@Test
	public void shouldApplyEmptySetLawsWithConditionalOR() {
		// given '(a + O)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final BooleanLiteral emptySetOperator = new BooleanLiteral(Boolean.FALSE);
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				emptySetOperator);
		// when
		final Expression result = BooleanAlgebraHelper.applyEmptySetLaws(expression);
		// then expect '(a)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod));
	}

	@Test
	public void shouldApplyEmptySetLawsWithConditionalAND() {
		// given '(a . O)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final BooleanLiteral emptySetOperator = new BooleanLiteral(Boolean.FALSE);
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				emptySetOperator);
		// when
		final Expression result = BooleanAlgebraHelper.applyEmptySetLaws(expression);
		// then expect '(O)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_AND, emptySetOperator));
	}

	@Test
	public void shouldApplyUniversalSetLawsWithConditionalOR() {
		// given '(a + 1)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final BooleanLiteral universalSetOperator = new BooleanLiteral(Boolean.TRUE);
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				universalSetOperator);
		// when
		final Expression result = BooleanAlgebraHelper.applyUniversalSetLaws(expression);
		// then expect '(1)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_OR, universalSetOperator));
	}

	@Test
	public void shouldApplyUniversalSetLawsWithConditionalAND() {
		// given '(a . 1)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final BooleanLiteral universalSetOperator = new BooleanLiteral(Boolean.TRUE);
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				universalSetOperator);
		// when
		final Expression result = BooleanAlgebraHelper.applyUniversalSetLaws(expression);
		// then expect '(a)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod));
	}

	@Test
	public void shouldApplyUnaryOperationLawsWithConditionalOR() {
		// given '(a + !a)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A")).inverse();
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				notEqualsAMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyUnaryOperationLaws(expression);
		// then expect '(1à'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_OR, new BooleanLiteral(Boolean.TRUE)));
	}

	@Test
	public void shouldApplyUnaryOperationLawsWithConditionalAND() {
		// given '(a . !a)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A")).inverse();
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				notEqualsAMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyUnaryOperationLaws(expression);
		// then expect '(0)'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_AND, new BooleanLiteral(Boolean.FALSE)));
	}

	@Test
	public void shouldApplyAbsorptionLawOnAllOperandsWithConditionalAND() {
		// given '(a.(a + b).(a + c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyAbsorptionLaw(expression);
		// then expect '(a)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod));
	}

	@Test
	public void shouldNotApplyAbsorptionLawOnAllOperandsWithConditionalAND() {
		// given '(a.(a.b).(a.c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyAbsorptionLaw(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyAbsorptionLawOnSomeOperandsWithConditionalAND() {
		// given '(a.(a + b).(a + c).d)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyAbsorptionLaw(expression);
		// then expect '(a.d)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsDMethod));
	}

	@Test
	public void shouldNotApplyAbsorptionLawOnSomeOperandsWithConditionalAND() {
		// given '(a.(a.b).(a.c).d)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyAbsorptionLaw(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyAbsorptionLawOnAllOperandsWithConditionalOR() {
		// given '(a + (a.b) + (a.c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyAbsorptionLaw(expression);
		// then expect '(a)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod));
	}

	@Test
	public void shouldNotApplyAbsorptionLawOnAllOperandsWithConditionalOR() {
		// given '(a + (a + b) + (a + c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyAbsorptionLaw(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyAbsorptionLawOnSomeOperandsWithConditionalOR() {
		// given '(a + (a.b) + (a.c) + d)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyAbsorptionLaw(expression);
		// then expect '(a + d)'
		assertThat(result).isEqualTo(new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsDMethod));
	}

	@Test
	public void shouldNotApplyAbsorptionLawOnSomeOperandsWithConditionalOR() {
		// given '(a + (a + b) + (a + c) + d)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyAbsorptionLaw(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyRedundancyLawOnAllOperandsWithConditionalAND() {
		// given '(a.(!a + b).(!a + c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A")).inverse();
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, notEqualsAMethod, equalsBMethod),
				new InfixExpression(InfixOperator.CONDITIONAL_OR, notEqualsAMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyRedundancyLaws(expression);
		// then expect '(a.b.c)'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod, equalsCMethod));
	}

	@Test
	public void shouldNotApplyRedundancyLawOnAllOperandsWithConditionalAND() {
		// given '(a.(a + b).(a + c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyRedundancyLaws(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyRedundancyLawOnSomeOperandsWithConditionalAND() {
		// given '(a.(!a + b).(!a + c).d)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A")).inverse();
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, notEqualsAMethod, equalsBMethod),
				new InfixExpression(InfixOperator.CONDITIONAL_OR, notEqualsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyRedundancyLaws(expression);
		// then expect '(a.b.c.d)'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod, equalsCMethod,
						equalsDMethod));
	}

	@Test
	public void shouldNotApplyRedundancyLawOnSomeOperandsWithConditionalAND() {
		// given '(a.(a + b).(a + c).d)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyRedundancyLaws(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyRedundancyLawOnAllOperandsWithConditionalOR() {
		// given '(a + (!a.b) + (!a.c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A")).inverse();
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, notEqualsAMethod, equalsBMethod),
				new InfixExpression(InfixOperator.CONDITIONAL_AND, notEqualsAMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyRedundancyLaws(expression);
		// then expect '(a + b + c)'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod, equalsCMethod));
	}

	@Test
	public void shouldNotApplyRedundancyLawOnAllOperandsWithConditionalOR() {
		// given '(a + (a.b) + (a.c))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyRedundancyLaws(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyRedundancyLawOnSomeOperandsWithConditionalOR() {
		// given '(a + (!a.b) + (!a.c) + d)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation notEqualsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A")).inverse();
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, notEqualsAMethod, equalsBMethod),
				new InfixExpression(InfixOperator.CONDITIONAL_AND, notEqualsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyRedundancyLaws(expression);
		// then expect '(a + b + c + d)'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod, equalsCMethod,
						equalsDMethod));
	}

	@Test
	public void shouldNotApplyRedundancyLawOnSomeOperandsWithConditionalOR() {
		// given '(a + (a.b) + (a.c) + d)'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod,
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new InfixExpression(
						InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod), equalsDMethod);
		// when
		final Expression result = BooleanAlgebraHelper.applyRedundancyLaws(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyMutuallyDistributiveLawOnAllOperandsWithConditionalOR() {
		// given '((a.b) + (a.c) + (a.d))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod), new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsDMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyMutuallyDistributiveLaw(expression);
		// then expect '(a.(b + c + d))'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_AND, equalsAMethod, new InfixExpression(
						InfixOperator.CONDITIONAL_OR, equalsBMethod, equalsCMethod, equalsDMethod)));
	}

	@Test
	public void shouldNotApplyMutuallyDistributiveLawOnAllOperandsWithConditionalOR() {
		// given '((a.b) + (a.c) + (b.d))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_OR, new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsBMethod), new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsAMethod, equalsCMethod), new InfixExpression(
				InfixOperator.CONDITIONAL_AND, equalsBMethod, equalsDMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyMutuallyDistributiveLaw(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

	@Test
	public void shouldApplyMutuallyDistributiveLawOnAllOperandsWithConditionalAND() {
		// given '((a + b).(a + c).(a + d))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, new InfixExpression(
				InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new InfixExpression(
				InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod), new InfixExpression(
				InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsDMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyMutuallyDistributiveLaw(expression);
		// then expect '(a + (b.c.d))'
		assertThat(result).isEqualTo(
				new InfixExpression(InfixOperator.CONDITIONAL_OR, equalsAMethod, new InfixExpression(
						InfixOperator.CONDITIONAL_AND, equalsBMethod, equalsCMethod, equalsDMethod)));
	}

	@Test
	public void shouldNotApplyMutuallyDistributiveLawOnAllOperandsWithConditionalAND() {
		// given '((a + b).(a + c).(b + d))'
		final LocalVariable var = new LocalVariable("t", "Lorg/bytesparadise/mongoose/model/TestPojo;");
		final MethodInvocation equalsAMethod = new MethodInvocation(var, "equals", new StringLiteral("A"));
		final MethodInvocation equalsBMethod = new MethodInvocation(var, "equals", new StringLiteral("B"));
		final MethodInvocation equalsCMethod = new MethodInvocation(var, "equals", new StringLiteral("C"));
		final MethodInvocation equalsDMethod = new MethodInvocation(var, "equals", new StringLiteral("D"));
		final InfixExpression expression = new InfixExpression(InfixOperator.CONDITIONAL_AND, new InfixExpression(
				InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsBMethod), new InfixExpression(
				InfixOperator.CONDITIONAL_OR, equalsAMethod, equalsCMethod), new InfixExpression(
				InfixOperator.CONDITIONAL_OR, equalsBMethod, equalsDMethod));
		// when
		final Expression result = BooleanAlgebraHelper.applyMutuallyDistributiveLaw(expression);
		// then expect same expression (no change)
		assertThat(result).isEqualTo(expression);
	}

}
