/**
 * 
 */
package org.lambdamatic.mongodb.types.geospatial;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * <p>
 * Polygons consist of an array of GeoJSON LinearRing coordinate arrays. These
 * LinearRings are closed LineStrings. Closed LineStrings have at least four
 * coordinate pairs and specify the same position as the first and last
 * coordinates.
 * </p>
 * 
 * <p>
 * The line that joins two points on a curved surface may or may not contain the
 * same set of co-ordinates that joins those two points on a flat surface. The
 * line that joins two points on a curved surface will be a geodesic. Carefully
 * check points to avoid errors with shared edges, as well as overlaps and other
 * types of intersections.
 * </p>
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 */
public class Polygon {

	private final Ring[] rings;

	/**
	 * Polygon with a single ring.
	 * 
	 * <p>
	 * For Polygons with a single ring, the ring cannot self-intersect.
	 * </p>
	 * <p>
	 * Note: If the last point does not correspond to the first point, an extra
	 * point will be added to close the ring.
	 * </p>
	 * 
	 * @param points
	 *            the points making the single {@link Ring} of this
	 *            {@link Polygon}
	 * 
	 */
	public Polygon(final Location... points) {
		this.rings = new Ring[] { new Ring(points) };
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
	 * Note: If the last point does not correspond to the first point, an extra
	 * point will be added to close the ring.
	 * </p>
	 * 
	 * @param ring
	 *            the single {@link Ring} of this {@link Polygon}
	 */
	public Polygon(final Ring ring) {
		this.rings = new Ring[] { ring };
	}

	/**
	 * Polygons with Multiple Rings
	 * 
	 * For Polygons with multiple rings:
	 * <ul>
	 * <li>The first described ring must be the exterior ring.</li>
	 * <li>The exterior ring cannot self-intersect.</li>
	 * <li>Any interior ring must be entirely contained by the outer ring.</li>
	 * <li>Interior rings cannot intersect or overlap each other. Interior rings
	 * cannot share an edge.</li>
	 * 
	 * @param rings
	 */
	public Polygon(final Ring... rings) {
		this.rings = rings;
	}
	
	/**
	 * @return the array of {@link Ring} of this {@link Polygon}.
	 */
	public Ring[] getRings() {
		return rings;
	}
	
	@Override
	public String toString() {
		if(rings == null || rings.length == 0) {
			return "<empty polygon>";
		} 
		final String content = Arrays.asList(rings).stream().map(r -> r.toString()).collect(Collectors.joining(","));
		if(rings.length == 1) {
			return content;
		}
		return "[" + content + "]";
	}

	/**
	 * A {@link Polygon} {@link Ring}.
	 * 
	 * @author Xavier Coulon <xcoulon@redhat.com>
	 *
	 */
	public class Ring {

		private final Location[] points;

		/**
		 * A single ring of a {@link Polygon}.
		 * <p>
		 * Note: If the last point does not correspond to the first point, an
		 * extra point will be added to close the ring. Queries using unclosed ring fail with error #27287 
		 * </p>
		 * 
		 * @param points
		 *            the points making this ring.
		 */
		public Ring(final Location... points) {
			// valid ring, let's just use the given points
			if(points.length > 2 && points[0].equals(points[points.length - 1])) {
				this.points = points;
			}
			// add the first point to close the ring
			else {
				this.points = new Location[points.length + 1];
				System.arraycopy(points, 0, this.points, 0, points.length);
				this.points[this.points.length - 1] = new Location(this.points[0].getLatitude(), this.points[0].getLongitude());
			}
		}
		
		public Location[] getPoints() {
			return points;
		}
		
		@Override
		public String toString() {
			if(rings == null || rings.length == 0) {
				return "<empty ring>";
			}
			return "[" + Arrays.asList(points).stream().map(r -> r.toString()).collect(Collectors.joining(",")) + "]";
		}

	}
}
