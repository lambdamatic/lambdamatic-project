/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.sample;

import org.bson.types.ObjectId;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.types.geospatial.Point;

/**
 * @author Xavier Coulon
 *
 */
@Document(collection="foos")
public class Foo {

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
	private Point location;
	
	public Foo() {
		
	}

	public Foo(final String stringField) {
		this.stringField = stringField;
	}

	public Foo(final String stringField, final int primitiveIntField, final EnumFoo fooType) {
		this.stringField = stringField;
		this.primitiveIntField = primitiveIntField;
		this.enumFoo = fooType; 
	}

	public Foo(final ObjectId id, final String stringField, final int primitiveIntField, final EnumFoo fooType) {
		this.id = id;
		this.stringField = stringField;
		this.primitiveIntField = primitiveIntField;
		this.enumFoo = fooType; 
	}

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(ObjectId id) {
		this.id = id;
	}

	/**
	 * @return the stringField
	 */
	public String getStringField() {
		return stringField;
	}

	/**
	 * @param stringField the stringField to set
	 */
	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	/**
	 * @return the primitiveByteField
	 */
	public byte getPrimitiveByteField() {
		return primitiveByteField;
	}

	/**
	 * @param primitiveByteField the primitiveByteField to set
	 */
	public void setPrimitiveByteField(byte primitiveByteField) {
		this.primitiveByteField = primitiveByteField;
	}

	/**
	 * @return the primitiveShortField
	 */
	public short getPrimitiveShortField() {
		return primitiveShortField;
	}

	/**
	 * @param primitiveShortField the primitiveShortField to set
	 */
	public void setPrimitiveShortField(short primitiveShortField) {
		this.primitiveShortField = primitiveShortField;
	}

	/**
	 * @return the primitiveIntField
	 */
	public int getPrimitiveIntField() {
		return primitiveIntField;
	}

	/**
	 * @param primitiveIntField the primitiveIntField to set
	 */
	public void setPrimitiveIntField(int primitiveIntField) {
		this.primitiveIntField = primitiveIntField;
	}

	/**
	 * @return the primitiveLongField
	 */
	public long getPrimitiveLongField() {
		return primitiveLongField;
	}

	/**
	 * @param primitiveLongField the primitiveLongField to set
	 */
	public void setPrimitiveLongField(long primitiveLongField) {
		this.primitiveLongField = primitiveLongField;
	}

	/**
	 * @return the primitiveFloatField
	 */
	public float getPrimitiveFloatField() {
		return primitiveFloatField;
	}

	/**
	 * @param primitiveFloatField the primitiveFloatField to set
	 */
	public void setPrimitiveFloatField(float primitiveFloatField) {
		this.primitiveFloatField = primitiveFloatField;
	}

	/**
	 * @return the primitiveDoubleField
	 */
	public double getPrimitiveDoubleField() {
		return primitiveDoubleField;
	}

	/**
	 * @param primitiveDoubleField the primitiveDoubleField to set
	 */
	public void setPrimitiveDoubleField(double primitiveDoubleField) {
		this.primitiveDoubleField = primitiveDoubleField;
	}

	/**
	 * @return the primitiveBooleanField
	 */
	public boolean isPrimitiveBooleanField() {
		return primitiveBooleanField;
	}

	/**
	 * @param primitiveBooleanField the primitiveBooleanField to set
	 */
	public void setPrimitiveBooleanField(boolean primitiveBooleanField) {
		this.primitiveBooleanField = primitiveBooleanField;
	}

	/**
	 * @return the primitiveCharField
	 */
	public char getPrimitiveCharField() {
		return primitiveCharField;
	}

	/**
	 * @param primitiveCharField the primitiveCharField to set
	 */
	public void setPrimitiveCharField(char primitiveCharField) {
		this.primitiveCharField = primitiveCharField;
	}

	/**
	 * @return the enumFoo
	 */
	public EnumFoo getEnumFoo() {
		return enumFoo;
	}

	/**
	 * @param enumFoo the enumFoo to set
	 */
	public void setEnumFoo(EnumFoo enumFoo) {
		this.enumFoo = enumFoo;
	}

	/**
	 * @return the location
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Point location) {
		this.location = location;
	}
	
	
	
}