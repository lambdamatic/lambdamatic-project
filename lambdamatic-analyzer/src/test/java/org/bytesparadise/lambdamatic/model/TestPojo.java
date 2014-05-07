package org.bytesparadise.lambdamatic.model;

import java.util.Date;

/**
 * Created by xcoulon on 12/16/13.
 */
public class TestPojo {

	private String stringValue = "foo";

	private Date dateValue = new Date();

	private int intValue = 0;

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

}
