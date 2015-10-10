/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast;

import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.List;

import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.CapturedArgumentRef;
import org.objectweb.asm.Type;

/**
 * Info about the nested Lambda Expression location.
 * 
 * @see SerializedLambda
 */
public class EmbeddedSerializedLambdaInfo extends SerializedLambdaInfo {

  /**
   * Full constructor
   * 
   * @param implClassName the fully qualified name of the implementation class.
   * @param implMethodName the name of the implementation method.
   * @param implMethodDesc the signature of the implementation method.
   * @param capturedArgumentRefs the (potentially empty) list of references to some
   *        {@link CapturedArgument}.
   * @param capturedArguments the captured arguments when calling the Lambda expression
   */
  public EmbeddedSerializedLambdaInfo(final String implClassName, final String implMethodName,
      final String implMethodDesc, final List<CapturedArgumentRef> capturedArgumentRefs,
      final List<CapturedArgument> capturedArguments) {
    super(Type.getObjectType(implClassName).getClassName(), implMethodName, implMethodDesc,
        getRelevantCapturedArguments(capturedArguments, capturedArgumentRefs));
  }

  /**
   * Returns the relevant captured arguments.
   * 
   * @param capturedArguments all the actual captured arguments
   * @param capturedArgumentRefs the references to the captured arguments to keep
   * @return a {@link List} of {@link CapturedArgument} which only contains the values mentioned in
   *         the given capturedArgumentRefs
   */
  static List<CapturedArgument> getRelevantCapturedArguments(
      final List<CapturedArgument> capturedArguments,
      final List<CapturedArgumentRef> capturedArgumentRefs) {
    final List<CapturedArgument> relevantArguments = new ArrayList<>();
    capturedArgumentRefs
        .forEach(ref -> relevantArguments.add(capturedArguments.get(ref.getArgumentIndex())));
    return relevantArguments;
  }

}
