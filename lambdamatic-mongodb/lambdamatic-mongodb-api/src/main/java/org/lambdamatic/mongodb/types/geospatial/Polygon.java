/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb.types.geospatial;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * <p>
 * Polygons consist of an array of GeoJSON LinearRing coordinate arrays. These LinearRings are
 * closed LineStrings. Closed LineStrings have at least four coordinate pairs and specify the same
 * position as the first and last coordinates.
 * </p>
 * 
 * <p>
 * The line that joins two points on a curved surface may or may not contain the same set of
 * co-ordinates that joins those two points on a flat surface. The line that joins two points on a
 * curved surface will be a geodesic. Carefully check points to avoid errors with shared edges, as
 * well as overlaps and other types of intersections.
 * </p>
 * 
 * @author Xavier Coulon
 */
public class Polygon {

  final Ring[] rings;

  /**
   * Polygon with a single ring.
   * 
   * <p>
   * For Polygons with a single ring, the ring cannot self-intersect.
   * </p>
   * <p>
   * Note: If the last point does not correspond to the first point, an extra point will be added to
   * close the ring.
   * </p>
   * 
   * @param points the points making the single {@link Ring} of this {@link Polygon}
   * 
   */
  public Polygon(final Location... points) {
    this.rings = new Ring[] {new Ring(points)};
  }

  /**
   * Polygon with a single ring.
   * 
   * <p>
   * For Polygons with a single ring, the ring cannot self-intersect.
   * </p>
   * <p>
   * Note: If the last point does not correspond to the first point, an extra point will be added to
   * close the ring.
   * </p>
   * 
   * @param points the points making the single {@link Ring} of this {@link Polygon}
   * 
   */
  public Polygon(final List<Location> points) {
    this.rings = new Ring[] {new Ring(points.toArray(new Location[points.size()]))};
  }

  /**
   * <p>
   * Polygon with a single ring.
   * </p>
   * 
   * <p>
   * For Polygons with a single ring, the ring cannot self-intersect.
   * </p>
   * <p>
   * Note: If the last point does not correspond to the first point, an extra point will be added to
   * close the ring.
   * </p>
   * 
   * @param ring the single {@link Ring} of this {@link Polygon}
   */
  public Polygon(final Ring ring) {
    this.rings = new Ring[] {ring};
  }

  /**
   * Polygons with Multiple Rings
   * 
   * For Polygons with multiple rings:
   * <ul>
   * <li>The first described ring must be the exterior ring.</li>
   * <li>The exterior ring cannot self-intersect.</li>
   * <li>Any interior ring must be entirely contained by the outer ring.</li>
   * <li>Interior rings cannot intersect or overlap each other. Interior rings cannot share an edge.
   * </li>
   * </ul>
   * 
   * @param rings the polygon rings
   */
  public Polygon(final Ring... rings) {
    this.rings = rings;
  }

  /**
   * @return the array of {@link Ring} of this {@link Polygon}.
   */
  public Ring[] getRings() {
    return this.rings;
  }

  @Override
  public String toString() {
    if (this.rings == null || this.rings.length == 0) {
      return "<empty polygon>";
    }
    final String content =
        Stream.of(this.rings).map(r -> r.toString()).collect(Collectors.joining(","));
    if (this.rings.length == 1) {
      return content;
    }
    return "[" + content + "]";
  }

  /**
   * A {@link Polygon} {@link Ring}.
   * 
   * @author Xavier Coulon
   *
   */
  public class Ring {

    private final Location[] points;

    /**
     * A single ring of a {@link Polygon}.
     * <p>
     * Note: If the last point does not correspond to the first point, an extra point will be added
     * to close the ring. Queries using unclosed ring fail with error #27287
     * </p>
     * 
     * @param points the points making this ring.
     */
    public Ring(final Location... points) {
      if (points.length > 2 && points[0].equals(points[points.length - 1])) {
        // valid ring, let's just use the given points
        this.points = points;
      } else {
        // add the first point to close the ring
        this.points = new Location[points.length + 1];
        System.arraycopy(points, 0, this.points, 0, points.length);
        this.points[this.points.length - 1] =
            new Location(this.points[0].getLatitude(), this.points[0].getLongitude());
      }
    }

    /**
     * @return the array of {@link Location} that compose this {@link Polygon}. The last entry in
     *         the array is a copy of the first entry, so that the polygon is a closed shape.
     */
    public Location[] getPoints() {
      return this.points;
    }

    @Override
    public String toString() {
      if (Polygon.this.rings == null || Polygon.this.rings.length == 0) {
        return "<empty ring>";
      }
      return "[" + Stream.of(this.points).map(r -> r.toString()).collect(Collectors.joining(","))
          + "]";
    }

  }
}
