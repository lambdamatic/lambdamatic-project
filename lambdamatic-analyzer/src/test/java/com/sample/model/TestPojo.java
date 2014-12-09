package com.sample.model;

import java.util.Date;

public class TestPojo {
	
	public int primitiveIntValue = 42;
	
	public String field = "bar";

	public String stringValue = "foo";

	public Date dateValue = new Date();

	public boolean getPrimitiveBooleanValue() {
		return true;
	}
	
	public Boolean getBooleanValue() {
		return Boolean.TRUE;
	}

	public byte getPrimitiveByteValue() {
		return (byte)0;
	}
	
	public Byte getByteValue() {
		return new Byte("0");
	}
	
	public short getPrimitiveShortValue() {
		return (short)0;
	}
	
	public Short getShortValue() {
		return new Short((short)0);
	}
	
	public int getPrimitiveIntValue() {
		return 0;
	}
	
	public Integer getIntegerValue() {
		return new Integer(0);
	}
	
	public long getPrimitiveLongValue() {
		return 1l;
	}
	
	public Long getLongValue() {
		return new Long(1l);
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

	
}


