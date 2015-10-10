/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/
/**
 * 
 */

package com.sample;

import java.util.List;

import org.lambdamatic.mongodb.annotations.Document;

/**
 * @author Xavier Coulon
 * @see http://docs.mongodb.org/manual/reference/operator/projection/elemMatch/
 */
@Document(collection = "schools")
public class School {

  private String zipcode;

  private List<Student> students;

  /**
   * @return the zipcode
   */
  public String getZipcode() {
    return zipcode;
  }

  /**
   * @param zipcode the zipcode to set
   */
  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  /**
   * @return the students
   */
  public List<Student> getStudents() {
    return students;
  }

  /**
   * @param students the students to set
   */
  public void setStudents(List<Student> students) {
    this.students = students;
  }

}
