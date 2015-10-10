/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package com.sample;

import org.lambdamatic.mongodb.annotations.EmbeddedDocument;

/**
 * 
 * @author Xavier Coulon
 * 
 * @see http://docs.mongodb.org/manual/reference/operator/projection/elemMatch/
 */
@EmbeddedDocument
public class Student {

  private String name;

  private String school;

  private int age;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the school
   */
  public String getSchool() {
    return school;
  }

  /**
   * @param school the school to set
   */
  public void setSchool(String school) {
    this.school = school;
  }

  /**
   * @return the age
   */
  public int getAge() {
    return age;
  }

  /**
   * @param age the age to set
   */
  public void setAge(int age) {
    this.age = age;
  }

}
