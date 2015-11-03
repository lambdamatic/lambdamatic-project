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

/**
 * Foo sample class.
 */
@Document(collection = "foo")
public class Foo {

  @DocumentId
  private ObjectId id;

  @DocumentField(name = "stringField_")
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
  private Byte byteField;

  @DocumentField
  private Short shortField;

  @DocumentField
  private Integer integerField;

  @DocumentField
  private Long longField;

  @DocumentField
  private Float floatField;

  @DocumentField
  private Double doubleField;

  @DocumentField
  private Boolean booleanField;

  @DocumentField
  private Character characterField;

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

  private Bar[] barArray;

  private List<String> stringList;

  private Set<String> stringSet;

  private String[] stringArray;

  private Map<String, String> stringMap;
  
  @DocumentField(name = "bytes")
  private byte[] bytes;

  @DocumentField
  private List<byte[]> bytesList;
  
}
