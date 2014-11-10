/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.metadata;

import org.lambdamatic.mongodb.metadata.context.GeoNearContext;
import org.lambdamatic.mongodb.metadata.context.GeoWithinContext;
import org.lambdamatic.mongodb.metadata.context.NearContext;

/**
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface LocationField {

	/**
	 * <p>
	 * Specifies a point for which a geospatial query returns the documents from nearest to farthest.
	 * </p>
	 * <p>
	 * Considerations:
	 * </p>
	 * <ul>
	 * <li>{@link #near(double, double, double)} queries that use a 2d index return a limit of 100 documents.</li>
	 * <li>You cannot combine the {@link #near(double, double, double)}, which requires a special geospatial index, with a query operator or
	 * command that uses a different type of special index. For example you cannot combine {@link #near(double, double, double)} with the
	 * <code>$text</code> query.</li>
	 * <li>If using a 2d index for {@link #near(double, double, double)}, specifying a batch size (i.e. batchSize()) in conjunction with
	 * {@link #near(double, double, double)} queries that use a 2d index is undefined. See {@link https
	 * ://jira.mongodb.org/browse/SERVER-5236?_ga=1.98925021.293086428.1412867581} for more information.</li>
	 * <li>For sharded collections, queries using {@link #near(double, double, double)} are not supported. You can instead use either the geoNear command or the $geoNear
	 * aggregation stage.</li>
	 * <li>geoNear always returns the documents sorted by distance. Any other sort order requires to sort the documents in memory, which can
	 * be inefficient. To return results in a different sort order, use the $geoWithin operator and the sort() method.</li>
	 * </ul>
	 * 
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public NearContext near(double longitude, double latitude);

	public GeoNearContext geoNear(double longitude, double latitude);

	public GeoWithinContext geoWithin(double longitude, double latitude);

}

