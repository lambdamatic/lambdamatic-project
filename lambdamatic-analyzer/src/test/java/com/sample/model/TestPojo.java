package com.sample.model;

import java.util.Date;

public class TestPojo {
	
	public String field = "bar";

	public String stringValue = "foo";

	public Date dateValue = new Date();

	public int intValue = 0;
	
	public Long longValue = new Long("1");

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String name) {
		this.stringValue = name;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public int getIntValue() {
		return intValue;
	}
	
	public Long getLongValue() {
		return longValue;
	}

}

