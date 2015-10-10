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
import org.objectweb.asm.Type;

/**
 * Info about the Lambda Expression location.
 * 
 * @see SerializedLambda
 */
public class SerializedLambdaInfo {

  /** the fully qualified name of the implementation class. */
  private final String implClassName;

  /** the name of the implementation method. */
  private final String implMethodName;

  /** the signature of the implementation method. */
  private final String implMethodDesc;

  /**
   * the (potentially empty) list of actual {@link CapturedArgument}.
   */
  private final List<CapturedArgument> capturedArguments;

  /**
   * Full constructor
   * 
   * @param serializedLambda the fully {@link SerializedLambda} carrying all the required info.
   */
  public SerializedLambdaInfo(final SerializedLambda serializedLambda) {
    this(Type.getObjectType(serializedLambda.getImplClass()).getClassName(),
        serializedLambda.getImplMethodName(), serializedLambda.getImplMethodSignature(),
        getCapturedArguments(serializedLambda));
  }

  /**
   * Constructor.
   * 
   * @param implClassName the fully qualified name of the Lambda implementation Class
   * @param implMethodName the name of the Lambda implementation method
   * @param implMethodSignature the signature of the Lambda implementation method
   * @param capturedArguments the captured arguments when calling the Lambda expression
   */
  SerializedLambdaInfo(final String implClassName, final String implMethodName,
      final String implMethodSignature, final List<CapturedArgument> capturedArguments) {
    this.implClassName = implClassName;
    this.implMethodName = implMethodName;
    this.implMethodDesc = implMethodSignature;
    this.capturedArguments = capturedArguments;
  }

  /**
   * Returns the list of {@link CapturedArgument} for the {@link SerializedLambda}.
   * 
   * @param serializedLambda the {@link SerializedLambda} to analyze.
   * @return the list of {@link CapturedArgument}
   */
  public static List<CapturedArgument> getCapturedArguments(
      final SerializedLambda serializedLambda) {
    final List<CapturedArgument> capturedArguments = new ArrayList<>();
    for (int i = 0; i < serializedLambda.getCapturedArgCount(); i++) {
      capturedArguments.add(new CapturedArgument(serializedLambda.getCapturedArg(i)));
    }
    return capturedArguments;
  }

  /**
   * @return the (potentially empty) list of actual {@link CapturedArgument}.
   */
  public List<CapturedArgument> getCapturedArguments() {
    return this.capturedArguments;
  }

  /**
   * @return the fully qualified name of the implementation class.
   */
  public String getImplClassName() {
    return this.implClassName;
  }

  /**
   * @return the name of the implementation method.
   */
  public String getImplMethodName() {
    return this.implMethodName;
  }

  /**
   * @return the signature of the implementation method.
   */
  public String getImplMethodDesc() {
    return this.implMethodDesc;
  }

  /**
   * @return the fully qualified location of the Lambda Expression implementation.
   */
  public String getImplMethodId() {
    return this.implClassName + "." + this.implMethodName + "(" + this.implMethodDesc + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.implClassName == null) ? 0 : this.implClassName.hashCode());
    result = prime * result + ((this.implMethodDesc == null) ? 0 : this.implMethodDesc.hashCode());
    result = prime * result + ((this.implMethodName == null) ? 0 : this.implMethodName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SerializedLambdaInfo other = (SerializedLambdaInfo) obj;
    if (this.implClassName == null) {
      if (other.implClassName != null) {
        return false;
      }
    } else if (!this.implClassName.equals(other.implClassName)) {
      return false;
    }
    if (this.implMethodDesc == null) {
      if (other.implMethodDesc != null) {
        return false;
      }
    } else if (!this.implMethodDesc.equals(other.implMethodDesc)) {
      return false;
    }
    if (this.implMethodName == null) {
      if (other.implMethodName != null) {
        return false;
      }
    } else if (!this.implMethodName.equals(other.implMethodName)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "SerializedLambdaInfo for " + this.implClassName + "." + this.implMethodName;
  }

}
