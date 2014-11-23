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
import org.lambdamatic.mongodb.annotations.DocumentId;
import org.lambdamatic.mongodb.annotations.DocumentField;

/**
 * @author Xavier Coulon
 *
 */
@Document(collection="users")
public class User {

	@DocumentId
	private ObjectId id;
	
	@DocumentField
	private String userName;

	@DocumentField
	private String firstName;
	
	@DocumentField
	private String lastName;

	public User() {
		super();
	}
	
	/**
	 * Constructor with predefined ObjectId
	 * @param userName
	 * @param firstName
	 * @param lastName
	 */
	public User(final ObjectId id, final String userName, final String firstName, final String lastName) {
		super();
		this.id = id;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	/**
	 * Constructor without predefined ObjectId
	 * @param userName
	 * @param firstName
	 * @param lastName
	 */
	public User(final String userName, final String firstName, final String lastName) {
		super();
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
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
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
	
}

