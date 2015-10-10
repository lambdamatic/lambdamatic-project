/*******************************************************************************
 * Copyright (c) 2015 Red Hat.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Red Hat - Initial Contribution
 *******************************************************************************/
package org.lambdamatic.analyzer.ast;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lambdamatic.analyzer.ast.node.ClassLiteral;
import org.lambdamatic.analyzer.ast.node.Expression;
import org.lambdamatic.testutils.TestWatcher;

@RunWith(Parameterized.class)
public class ExpressionJavaTypeTest {
  @Rule
  public TestWatcher watcher = new TestWatcher();

  @Parameters(name = "[{index}] expect {1}")
  public static Object[][] data() {

    return new Object[][] {new Object[] {new ClassLiteral(Integer.class), Integer.class}};
  }

  @Parameter(value = 0)
  public Expression expression;

  @Parameter(value = 1)
  public Class<?> expectedJavaType;

  @Test
  public void shouldGetJavaType() throws IOException {
    assertThat(expression.getJavaType()).isEqualTo(expectedJavaType);
  }


}
