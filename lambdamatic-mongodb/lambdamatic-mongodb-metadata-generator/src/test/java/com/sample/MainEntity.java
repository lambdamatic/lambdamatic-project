/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.sample;

import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.DocumentField;

/**
 * @author Xavier Coulon
 *
 */
@Document(collection="mainCollection")
public class MainEntity {

	@DocumentId 
	private String id;
	
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
	
	
}

