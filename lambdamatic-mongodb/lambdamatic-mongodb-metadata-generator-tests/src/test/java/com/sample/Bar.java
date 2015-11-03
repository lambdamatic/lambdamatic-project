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

/**
 * Bar sample class.
 *
 */
@EmbeddedDocument
public class Bar {

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

}
