package fr.geo.convert;

/**
 * Base class for projected coordinates of a point: (East, North) pair expressed
 * in meters.
 */
class Projection {

	/**
	 * X coordinate (east from origin) in meters
	 */
	protected double east;

	/**
	 * Y coordinate (north from origin) in meters
	 */
	protected double north;

	/**
	 * Creates point (0, 0)
	 */
	public Projection() {
		east = 0.0;
		north = 0.0;
	}

	/**
	 * initializes new projection coordinates
	 * 
	 * @param east
	 *            east from origin in meters
	 * @param north
	 *            north from origin in meters
	 */
	public Projection(double east, double north) {
		this.east = east;
		this.north = north;
	}

	/**
	 * returns east coordinate (in meters)
	 */
	public double east() {
		return east;
	}

	/**
	 * returns north coordinate (in meters)
	 */
	public double north() {
		return north;
	}
}