package com.sample;

import org.lambdamatic.mongodb.annotations.Document;

@Document(collection="baz")
public class Baz {

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
