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
package com.sample.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.lambdamatic.SerializablePredicate;

/**
 * Test domain class.
 * 
 * @author Xavier Coulon
 *
 */
public class OtherTestPojo {

  public int primitiveIntValue = 42;

  public String field = "bar";

  public String stringValue = "foo";

  public Date dateValue = new Date();

  public EnumPojo enumPojo = EnumPojo.FOO;

  public List<EmbeddedTestPojo> elementList = new ArrayList<>();

  public OtherTestPojo() {}

  /**
   * constructor for testing purpose only.
   * @param stringValue the stringValue to assign
   * @param primitiveIntValue the primitiveIntValue to assign
   */
  public OtherTestPojo(String stringValue, int primitiveIntValue) {
    super();
    this.stringValue = stringValue;
    this.primitiveIntValue = primitiveIntValue;
  }


  public boolean getPrimitiveBooleanValue() {
    return true;
  }

  public Boolean getBooleanValue() {
    return Boolean.TRUE;
  }

  public byte getPrimitiveByteValue() {
    return (byte) 0;
  }

  public Byte getByteValue() {
    return new Byte("0");
  }

  public short getPrimitiveShortValue() {
    return (short) 0;
  }

  public Short getShortValue() {
    return new Short((short) 0);
  }

  public int getPrimitiveIntValue() {
    return 0;
  }

  public Integer getIntegerValue() {
    return new Integer(0);
  }

  public long getPrimitiveLongValue() {
    return 1L;
  }

  public Long getLongValue() {
    return new Long(1L);
  }

  public char getPrimitiveCharValue() {
    return 'a';
  }

  public Character getCharacterValue() {
    return new Character('a');
  }

  public float getPrimitiveFloatValue() {
    return 1f;
  }

  public Float getFloatValue() {
    return new Float(1f);
  }

  public double getPrimitiveDoubleValue() {
    return 2d;
  }

  public Double getDoubleValue() {
    return new Double(1d);
  }

  public String getStringValue() {
    return stringValue;
  }

  public Date getDateValue() {
    return dateValue;
  }

  public EnumPojo getEnumPojo() {
    return enumPojo;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }

  public boolean include(final Object... values) {
    return true;
  }

  public boolean matches(final OtherTestPojo[] otherPojos) {
    return true;
  }

  public boolean matches(final String[] values) {
    return true;
  }

  public boolean elementMatch(final SerializablePredicate<OtherTestPojo> expression) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
    result = prime * result + ((elementList == null) ? 0 : elementList.hashCode());
    result = prime * result + ((enumPojo == null) ? 0 : enumPojo.hashCode());
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    result = prime * result + primitiveIntValue;
    result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
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
    OtherTestPojo other = (OtherTestPojo) obj;
    if (dateValue == null) {
      if (other.dateValue != null) {
        return false;
      }
    } else if (!dateValue.equals(other.dateValue)) {
      return false;
    }
    if (elementList == null) {
      if (other.elementList != null) {
        return false;
      }
    } else if (!elementList.equals(other.elementList)) {
      return false;
    }
    if (enumPojo != other.enumPojo) {
      return false;
    }
    if (field == null) {
      if (other.field != null) {
        return false;
      }
    } else if (!field.equals(other.field)) {
      return false;
    }
    if (primitiveIntValue != other.primitiveIntValue) {
      return false;
    }
    if (stringValue == null) {
      if (other.stringValue != null) {
        return false;
      }
    } else if (!stringValue.equals(other.stringValue)) {
      return false;
    }
    return true;
  }


}


