/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sample.User;


/**
 * Testing the MongoDB Lambda-based Fluent API
 * 
 * @author Xavier Coulon
 *
 */
@Ignore
@RunWith(Arquillian.class)
public class MongoFluentAPIIntegrationTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoFluentAPIIntegrationTest.class);
	
	@Inject
	private DataStore datastore;
	
	@Deployment
    public static WebArchive createDeployment() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class)
            .addPackages(true, User.class.getPackage())
            .addPackages(true, DataStore.class.getPackage())
            .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml").resolve("org.mongodb:mongo-java-driver", "org.slf4j:slf4j-api", "org.assertj:assertj-core").withTransitivity().asFile())
            .addAsResource("config.json")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        LOGGER.debug(archive.toString(true));
		return archive;
    }
	
	@Before
	public void preChecks() {
		assertThat(datastore).isNotNull();
	}

	@Test
	public void shouldQueryContent() {
	}

}

