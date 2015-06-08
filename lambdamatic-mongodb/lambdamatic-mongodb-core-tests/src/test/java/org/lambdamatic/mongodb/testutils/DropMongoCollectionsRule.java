/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.testutils;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
@Deprecated
public class DropMongoCollectionsRule implements MethodRule {

	private final MongoCollection<?> collection;

	public DropMongoCollectionsRule(final MongoClient mongoClient, final String databaseName, final String collectionName) {
		this.collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				// clean the collection
				collection.drop();
				base.evaluate();
			}
		};
	}

}
