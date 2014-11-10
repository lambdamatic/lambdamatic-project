package com.sample.model;

import java.util.Date;

public class TestPojo {
	
	public String field = "bar";

	private String stringValue = "foo";

	private Date dateValue = new Date();

	private int intValue = 0;
	
	private Long longValue = new Long("1");

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

