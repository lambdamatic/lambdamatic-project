/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.sample;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentField;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.TransientField;
import org.lambdamatic.mongodb.types.geospatial.Location;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
@Document(collection="foos")
@SuppressWarnings("unused")
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
		private EnumBar[] enumBarArray;
		private Set<String> stringSet;

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
		
		public FooBuilder withEnumBarArray(final EnumBar... values) {
			this.enumBarArray = values;
			return this;
		}
		
		public FooBuilder withStringSet(final String... values) {
			this.stringSet = new TreeSet<>(Arrays.asList(values));
			return this;
		}
		
		public Foo build() {
			return new Foo(this);
		}

	}
	
	@DocumentId 
	private ObjectId id;
	
	@DocumentField(name="stringField_")
	private String stringField;
	
	@TransientField
	private String transientStringField;
	
	private byte primitiveByteField;
	
	private Byte byteField;
	
	@DocumentField
	private short primitiveShortField;
	
	@DocumentField
	private Short shortField;
	
	@DocumentField
	private int primitiveIntField;

	@DocumentField
	private Integer integerField;
	
	@DocumentField
	private long primitiveLongField;

	@DocumentField
	private Long longField;
	
	@DocumentField
	private float primitiveFloatField;

	@DocumentField
	private Float floatField;
	
	@DocumentField
	private double primitiveDoubleField;
	
	@DocumentField
	private Double doubleField;
	
	@DocumentField
	private boolean primitiveBooleanField;
	
	@DocumentField
	private Boolean booleanField;
	
	@DocumentField
	private char primitiveCharField;
	
	@DocumentField
	private Character characterField;
	
	@DocumentField
	private EnumFoo enumFoo;
	
	@DocumentField
	private Location location;
	
	@DocumentField
	private List<Bar> barList;

	private Bar bar;
	
	@DocumentField
	private Date date;
	
	private EnumBar[] enumBarArray;

	private Set<String> stringSet;

	public Foo() {
		
	}

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
		this.enumBarArray = fooBuilder.enumBarArray;
		this.stringSet = fooBuilder.stringSet;
	}

}