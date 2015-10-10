/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.metadata;

import java.util.List;

import org.lambdamatic.mongodb.FilterExpression;
import org.lambdamatic.mongodb.types.geospatial.Location;
import org.lambdamatic.mongodb.types.geospatial.Polygon;

/**
 * API for geo-search operations related to {@link Location}.
 * 
 * @author Xavier Coulon
 *
 * @see <a href="http://docs.mongodb.org/manual/core/2dsphere">2dsphere Indexes</a>
 */
public interface LocationField extends QueryField<Location> {

  /**
   * Selects documents with geospatial data that exists entirely within a specified shape. When
   * determining inclusion, MongoDB considers the border of a shape to be part of the shape, subject
   * to the precision of floating point numbers.
   *
   * {@link #geoWithin(Polygon)} does not require a geospatial index. However, a geospatial index
   * will improve query performance. Both 2dsphere and 2d geospatial indexes support $geoWithin.
   * 
   * @param polygon a single ring or a multiple rings polygon
   * @return a boolean so that this method can be used in a {@link FilterExpression}
   */
  // FIXME: add support for http://docs.mongodb.org/manual/core/2dsphere/#multipoint et al.
  @MongoOperation(MongoOperator.GEO_WITHIN)
  public boolean geoWithin(final Polygon polygon);

  /**
   * Selects documents with geospatial data that exists entirely within a specified shape. When
   * determining inclusion, MongoDB considers the border of a shape to be part of the shape, subject
   * to the precision of floating point numbers.
   *
   * {@link #geoWithin(Polygon)} does not require a geospatial index. However, a geospatial index
   * will improve query performance. Both 2dsphere and 2d geospatial indexes support $geoWithin.
   * 
   * @param points the points defining the single ring of the polygon
   * @return a boolean so that this method can be used in a {@link FilterExpression}
   */
  // FIXME: add support for http://docs.mongodb.org/manual/core/2dsphere/#multipoint et al.
  @MongoOperation(MongoOperator.GEO_WITHIN)
  public boolean geoWithin(final Location[] points);

  /**
   * Selects documents with geospatial data that exists entirely within a specified shape. When
   * determining inclusion, MongoDB considers the border of a shape to be part of the shape, subject
   * to the precision of floating point numbers.
   *
   * {@link #geoWithin(Polygon)} does not require a geospatial index. However, a geospatial index
   * will improve query performance. Both 2dsphere and 2d geospatial indexes support $geoWithin.
   * 
   * @param points the points defining the single ring of the polygon
   * @return a boolean so that this method can be used in a {@link FilterExpression}
   */
  // FIXME: add support for http://docs.mongodb.org/manual/core/2dsphere/#multipoint et al.
  @MongoOperation(MongoOperator.GEO_WITHIN)
  public boolean geoWithin(final List<Location> points);

}
