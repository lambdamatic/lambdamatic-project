/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.sample;

import java.util.Date;

import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.EmbeddedDocument;
import org.lambdamatic.mongodb.types.geospatial.Location;

@EmbeddedDocument
public class Bar {

  public static class BarBuilder {
    
    private String stringField;
    
    private byte primitiveByteField;
    
    private short primitiveShortField;
    
    private int primitiveIntField;
    
    private long primitiveLongField;
    
    private float primitiveFloatField;
    
    private double primitiveDoubleField;
    
    private boolean primitiveBooleanField;
    
    private char primitiveCharField;
    
    private EnumBar enumBar;
    
    private Location location;
    
    private Date date;

    public BarBuilder withPrimitiveByteField(final byte primitiveByteField) {
      this.primitiveByteField = primitiveByteField;
      return this;
    }

    public BarBuilder withPrimitiveShortField(final short primitiveShortField) {
      this.primitiveShortField = primitiveShortField;
      return this;
    }

    public BarBuilder withPrimitiveIntField(final int primitiveIntField) {
      this.primitiveIntField = primitiveIntField;
      return this;
    }

    public BarBuilder withPrimitiveLongField(final long primitiveLongField) {
      this.primitiveLongField = primitiveLongField;
      return this;
    }

    public BarBuilder withPrimitiveFloatField(final float primitiveFloatField) {
      this.primitiveFloatField = primitiveFloatField;
      return this;
    }

    public BarBuilder withPrimitiveDoubleField(final double primitiveDoubleField) {
      this.primitiveDoubleField = primitiveDoubleField;
      return this;
    }

    public BarBuilder withPrimitiveBooleanField(final boolean primitiveBooleanField) {
      this.primitiveBooleanField = primitiveBooleanField;
      return this;
    }

    public BarBuilder withPrimitiveCharField(final char primitiveCharField) {
      this.primitiveCharField = primitiveCharField;
      return this;
    }

    public BarBuilder withStringField(final String stringField) {
      this.stringField = stringField;
      return this;
    }

    public BarBuilder withEnumBar(final EnumBar enumBar) {
      this.enumBar = enumBar;
      return this;
    }

    public BarBuilder withLocation(final double latitude, final double longitude) {
      this.location = new Location(latitude, longitude);
      return this;
    }

    public BarBuilder withDate(Date date) {
      this.date = date;
      return this;
    }

    public Bar build() {
      return new Bar(this);
    }

  }

  @DocumentField
  private String stringField;

  @DocumentField
  private byte primitiveByteField;

  @DocumentField
  private short primitiveShortField;

  @DocumentField
  private int primitiveIntField;

  @DocumentField
  private long primitiveLongField;

  @DocumentField
  private float primitiveFloatField;

  @DocumentField
  private double primitiveDoubleField;

  @DocumentField
  private boolean primitiveBooleanField;

  @DocumentField
  private char primitiveCharField;

  @DocumentField
  private EnumBar enumBar;

  @DocumentField
  private Location location;

  @DocumentField
  private Date date;

  public Bar() {

  }

  /**
   * Constructor.
   * @param stringField the stringField
   * @param primitiveIntField the primitiveIntField
   */
  public Bar(String stringField, int primitiveIntField) {
    super();
    this.stringField = stringField;
    this.primitiveIntField = primitiveIntField;
  }
  
  
  /**
   * Constructor.
   * @param barBuilder the builder
   */
  public Bar(final BarBuilder barBuilder) {
    this.enumBar = barBuilder.enumBar;
    this.location = barBuilder.location;
    this.stringField = barBuilder.stringField;
    this.primitiveBooleanField = barBuilder.primitiveBooleanField;
    this.primitiveByteField = barBuilder.primitiveByteField;
    this.primitiveCharField = barBuilder.primitiveCharField;
    this.primitiveDoubleField = barBuilder.primitiveDoubleField;
    this.primitiveFloatField = barBuilder.primitiveFloatField;
    this.primitiveIntField = barBuilder.primitiveIntField;
    this.primitiveLongField = barBuilder.primitiveLongField;
    this.primitiveShortField = barBuilder.primitiveShortField;
    this.primitiveShortField = barBuilder.primitiveShortField;
    this.date = barBuilder.date;
  }

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }

  public byte getPrimitiveByteField() {
    return primitiveByteField;
  }

  public void setPrimitiveByteField(byte primitiveByteField) {
    this.primitiveByteField = primitiveByteField;
  }

  public short getPrimitiveShortField() {
    return primitiveShortField;
  }

  public void setPrimitiveShortField(short primitiveShortField) {
    this.primitiveShortField = primitiveShortField;
  }

  public int getPrimitiveIntField() {
    return primitiveIntField;
  }

  public void setPrimitiveIntField(int primitiveIntField) {
    this.primitiveIntField = primitiveIntField;
  }

  public long getPrimitiveLongField() {
    return primitiveLongField;
  }

  public void setPrimitiveLongField(long primitiveLongField) {
    this.primitiveLongField = primitiveLongField;
  }

  public float getPrimitiveFloatField() {
    return primitiveFloatField;
  }

  public void setPrimitiveFloatField(float primitiveFloatField) {
    this.primitiveFloatField = primitiveFloatField;
  }

  public double getPrimitiveDoubleField() {
    return primitiveDoubleField;
  }

  public void setPrimitiveDoubleField(double primitiveDoubleField) {
    this.primitiveDoubleField = primitiveDoubleField;
  }

  public boolean isPrimitiveBooleanField() {
    return primitiveBooleanField;
  }

  public void setPrimitiveBooleanField(boolean primitiveBooleanField) {
    this.primitiveBooleanField = primitiveBooleanField;
  }

  public char getPrimitiveCharField() {
    return primitiveCharField;
  }

  public void setPrimitiveCharField(char primitiveCharField) {
    this.primitiveCharField = primitiveCharField;
  }

  public EnumBar getEnumBar() {
    return enumBar;
  }

  public void setEnumBar(EnumBar enumBar) {
    this.enumBar = enumBar;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((enumBar == null) ? 0 : enumBar.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + (primitiveBooleanField ? 1231 : 1237);
    result = prime * result + primitiveByteField;
    result = prime * result + primitiveCharField;
    long temp;
    temp = Double.doubleToLongBits(primitiveDoubleField);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + Float.floatToIntBits(primitiveFloatField);
    result = prime * result + primitiveIntField;
    result = prime * result + (int) (primitiveLongField ^ (primitiveLongField >>> 32));
    result = prime * result + primitiveShortField;
    result = prime * result + ((stringField == null) ? 0 : stringField.hashCode());
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
    Bar other = (Bar) obj;
    if (enumBar != other.enumBar) {
      return false;
    }
    if (location == null) {
      if (other.location != null) {
        return false;
      }
    } else if (!location.equals(other.location)) {
      return false;
    }
    if (primitiveBooleanField != other.primitiveBooleanField) {
      return false;
    }
    if (primitiveByteField != other.primitiveByteField) {
      return false;
    }
    if (primitiveCharField != other.primitiveCharField) {
      return false;
    }
    if (Double.doubleToLongBits(primitiveDoubleField) != Double
        .doubleToLongBits(other.primitiveDoubleField)) {
      return false;
    }
    if (Float.floatToIntBits(primitiveFloatField) != Float
        .floatToIntBits(other.primitiveFloatField)) {
      return false;
    }
    if (primitiveIntField != other.primitiveIntField) {
      return false;
    }
    if (primitiveLongField != other.primitiveLongField) {
      return false;
    }
    if (primitiveShortField != other.primitiveShortField) {
      return false;
    }
    if (stringField == null) {
      if (other.stringField != null) {
        return false;
      }
    } else if (!stringField.equals(other.stringField)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Foo [stringField=" + stringField + ", primitiveByteField=" + primitiveByteField
        + ", primitiveShortField=" + primitiveShortField + ", primitiveIntField="
        + primitiveIntField + ", primitiveLongField=" + primitiveLongField
        + ", primitiveFloatField=" + primitiveFloatField + ", primitiveDoubleField="
        + primitiveDoubleField + ", primitiveBooleanField=" + primitiveBooleanField
        + ", primitiveCharField=" + primitiveCharField + ", enumBar=" + enumBar + ", location="
        + location + ", date=" + date + "]";
  }

}
