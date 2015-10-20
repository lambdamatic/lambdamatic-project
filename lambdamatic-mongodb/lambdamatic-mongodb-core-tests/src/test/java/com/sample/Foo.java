/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.sample;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.types.geospatial.Location;

import com.sample.Bar.BarBuilder;

@Document(collection = "foo")
public class Foo {

  public static class FooBuilder {
    private ObjectId id;
    
    private String stringField;
    
    private byte primitiveByteField;
    
    private short primitiveShortField;
    
    private int primitiveIntField;
    
    private long primitiveLongField;
    
    private float primitiveFloatField;
    
    private double primitiveDoubleField;
    
    private boolean primitiveBooleanField;
    
    private char primitiveCharField;
    
    private EnumFoo enumFoo;
    
    private Location location;
    
    private Date date;
    
    private Bar bar;
    
    private List<Bar> barList;
    
    private Map<String, Bar> barMap;
    
    private EnumBar[] enumBarArray;
    
    private List<String> stringList;
    
    private Set<String> stringSet;
    
    private String[] stringArray;
    
    private Map<String, String> stringMap;
    
    private byte[] bytes;

    public FooBuilder withId(final ObjectId id) {
      this.id = id;
      return this;
    }

    public FooBuilder withPrimitiveByteField(final byte primitiveByteField) {
      this.primitiveByteField = primitiveByteField;
      return this;
    }

    public FooBuilder withPrimitiveShortField(final short primitiveShortField) {
      this.primitiveShortField = primitiveShortField;
      return this;
    }

    public FooBuilder withPrimitiveIntField(final int primitiveIntField) {
      this.primitiveIntField = primitiveIntField;
      return this;
    }

    public FooBuilder withPrimitiveLongField(final long primitiveLongField) {
      this.primitiveLongField = primitiveLongField;
      return this;
    }

    public FooBuilder withPrimitiveFloatField(final float primitiveFloatField) {
      this.primitiveFloatField = primitiveFloatField;
      return this;
    }

    public FooBuilder withPrimitiveDoubleField(final double primitiveDoubleField) {
      this.primitiveDoubleField = primitiveDoubleField;
      return this;
    }

    public FooBuilder withPrimitiveBooleanField(final boolean primitiveBooleanField) {
      this.primitiveBooleanField = primitiveBooleanField;
      return this;
    }

    public FooBuilder withPrimitiveCharField(final char primitiveCharField) {
      this.primitiveCharField = primitiveCharField;
      return this;
    }

    public FooBuilder withStringField(final String stringField) {
      this.stringField = stringField;
      return this;
    }

    public FooBuilder withEnumFoo(final EnumFoo enumFoo) {
      this.enumFoo = enumFoo;
      return this;
    }

    public FooBuilder withLocation(final double latitude, final double longitude) {
      this.location = new Location(latitude, longitude);
      return this;
    }

    public FooBuilder withDate(Date date) {
      this.date = date;
      return this;
    }

    public FooBuilder withBar(Bar bar) {
      this.bar = bar;
      return this;
    }

    public FooBuilder withBarList(final Bar... values) {
      this.barList = Arrays.asList(values);
      return this;
    }

    public FooBuilder withBarMap(final Map<String, Bar> barMap) {
      this.barMap = barMap;
      return this;
    }

    public FooBuilder withEnumBarArray(final EnumBar... values) {
      this.enumBarArray = values;
      return this;
    }

    public FooBuilder withStringList(final String... values) {
      this.stringList = Arrays.asList(values);
      return this;
    }

    public FooBuilder withStringSet(final String... values) {
      this.stringSet = new TreeSet<String>(Arrays.asList(values));
      return this;
    }

    public FooBuilder withStringArray(final String... values) {
      this.stringArray = values;
      return this;
    }

    public FooBuilder withStringMap(final Map<String, String> stringMap) {
      this.stringMap = stringMap;
      return this;
    }

    public FooBuilder withBinary(byte[] bytes) {
      this.bytes = bytes;
      return this;
    }
    
    public Foo build() {
      return new Foo(this);
    }

  }

  @DocumentId
  private ObjectId id;

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
  private EnumFoo enumFoo;

  @DocumentField
  private Location location;

  @DocumentField
  private List<Bar> barList;

  @DocumentField
  private Map<String, Bar> barMap;

  private Bar bar;

  @DocumentField
  private Date date;

  private EnumBar[] enumBarArray;

  private List<String> stringList;

  private Set<String> stringSet;

  private String[] stringArray;

  private Map<String, String> stringMap;
  
  @DocumentField(name = "raw_content")
  private byte[] bytes;

  public Foo() {

  }

  /**
   * Constructor.
   * @param fooBuilder the builder
   */
  public Foo(final FooBuilder fooBuilder) {
    this.id = fooBuilder.id;
    this.enumFoo = fooBuilder.enumFoo;
    this.location = fooBuilder.location;
    this.stringField = fooBuilder.stringField;
    this.primitiveBooleanField = fooBuilder.primitiveBooleanField;
    this.primitiveByteField = fooBuilder.primitiveByteField;
    this.primitiveCharField = fooBuilder.primitiveCharField;
    this.primitiveDoubleField = fooBuilder.primitiveDoubleField;
    this.primitiveFloatField = fooBuilder.primitiveFloatField;
    this.primitiveIntField = fooBuilder.primitiveIntField;
    this.primitiveLongField = fooBuilder.primitiveLongField;
    this.primitiveShortField = fooBuilder.primitiveShortField;
    this.primitiveShortField = fooBuilder.primitiveShortField;
    this.date = fooBuilder.date;
    this.bar = fooBuilder.bar;
    this.barList = fooBuilder.barList;
    this.barMap = fooBuilder.barMap;
    this.enumBarArray = fooBuilder.enumBarArray;
    this.stringList = fooBuilder.stringList;
    this.stringSet = fooBuilder.stringSet;
    this.stringArray = fooBuilder.stringArray;
    this.stringMap = fooBuilder.stringMap;
    this.bytes = fooBuilder.bytes;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
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

  public EnumFoo getEnumFoo() {
    return enumFoo;
  }

  public void setEnumFoo(EnumFoo enumFoo) {
    this.enumFoo = enumFoo;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Bar getBar() {
    return bar;
  }

  public List<Bar> getBarList() {
    return barList;
  }

  public Map<String, Bar> getBarMap() {
    return barMap;
  }

  public void setBar(Bar bar) {
    this.bar = bar;
  }

  public EnumBar[] getEnumBarArray() {
    return enumBarArray;
  }

  public void setEnumBarArray(EnumBar[] enumBarArray) {
    this.enumBarArray = enumBarArray;
  }

  public List<String> getStringList() {
    return stringList;
  }

  public String[] getStringArray() {
    return stringArray;
  }

  public Set<String> getStringSet() {
    return stringSet;
  }

  public void setStringList(List<String> stringList) {
    this.stringList = stringList;
  }

  public void setStringArray(String[] stringArray) {
    this.stringArray = stringArray;
  }

  public void setStringSet(Set<String> stringSet) {
    this.stringSet = stringSet;
  }

  public Map<String, String> getStringMap() {
    return stringMap;
  }

  public void setStringMap(Map<String, String> stringMap) {
    this.stringMap = stringMap;
  }

  public byte[] getBytes() {
    return bytes;
  }
  
  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bar == null) ? 0 : bar.hashCode());
    result = prime * result + ((barList == null) ? 0 : barList.hashCode());
    result = prime * result + ((barMap == null) ? 0 : barMap.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + Arrays.hashCode(enumBarArray);
    result = prime * result + ((enumFoo == null) ? 0 : enumFoo.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    result = prime * result + Arrays.hashCode(stringArray);
    result = prime * result + ((stringField == null) ? 0 : stringField.hashCode());
    result = prime * result + ((stringList == null) ? 0 : stringList.hashCode());
    result = prime * result + ((stringMap == null) ? 0 : stringMap.hashCode());
    result = prime * result + ((stringSet == null) ? 0 : stringSet.hashCode());
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
    Foo other = (Foo) obj;
    if (bar == null) {
      if (other.bar != null) {
        return false;
      }
    } else if (!bar.equals(other.bar)) {
      return false;
    }
    if (barList == null) {
      if (other.barList != null) {
        return false;
      }
    } else if (!barList.equals(other.barList)) {
      return false;
    }
    if (barMap == null) {
      if (other.barMap != null) {
        return false;
      }
    } else if (!barMap.equals(other.barMap)) {
      return false;
    }
    if (date == null) {
      if (other.date != null) {
        return false;
      }
    } else if (!date.equals(other.date)) {
      return false;
    }
    if (!Arrays.equals(enumBarArray, other.enumBarArray)) {
      return false;
    }
    if (enumFoo != other.enumFoo) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
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
    if (!Arrays.equals(stringArray, other.stringArray)) {
      return false;
    }
    if (stringField == null) {
      if (other.stringField != null) {
        return false;
      }
    } else if (!stringField.equals(other.stringField)) {
      return false;
    }
    if (stringList == null) {
      if (other.stringList != null) {
        return false;
      }
    } else if (!stringList.equals(other.stringList)) {
      return false;
    }
    if (stringMap == null) {
      if (other.stringMap != null) {
        return false;
      }
    } else if (!stringMap.equals(other.stringMap)) {
      return false;
    }
    if (stringSet == null) {
      if (other.stringSet != null) {
        return false;
      }
    } else if (!stringSet.equals(other.stringSet)) {
      return false;
    }
    return true;
  }

}
