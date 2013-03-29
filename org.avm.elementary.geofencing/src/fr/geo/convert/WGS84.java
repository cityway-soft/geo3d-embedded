package fr.geo.convert;

/**
 * The WGS84 international system is a geographic coordinate system that is used
 * by GPS and coverts the whole earth. It is used as a central system for all
 * coordinate conversions.
 */
public class WGS84 extends Coordinates {

	/**
	 * longitude (en radian)
	 */
	private double longitude;

	/**
	 * latitude (en radian)
	 */
	private double latitude;

	/**
	 * ellipsoidal elevation (en meters)
	 */
	private double ellipsoidalElevation;

	/**
	 * initializes a new WGS84 coordinate at (0, 0, 0)
	 */
	public WGS84() {
		this.latitude = 0.0;
		this.longitude = 0.0;
		this.ellipsoidalElevation = 0.0;
	}

	/**
	 * initializes a new WGS84 coordinate
	 * 
	 * @param longitude
	 *            longitude in radian
	 * @param latitude
	 *            latitude in radian
	 * @param h
	 *            ellipsoidal elevation in meters
	 */
	public WGS84(double longitude, double latitude, double ellipsoidalElevation) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.ellipsoidalElevation = ellipsoidalElevation;
	}

	/**
	 * returns longitude angle in radian
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * returns latitude angle in radian
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * returns ellipsoidal elevation in meters
	 */
	public double getEllipsoidalElevation() {
		return ellipsoidalElevation;
	}

	/**
	 * returns this coordinate as a string
	 * 
	 * @return string formated as "WGS84 longitude latitude elevation"
	 */
	public String toString() {
		return " " + Coordinates.angleToString(longitude) + " "
				+ Coordinates.angleToString(latitude) + " "
				+ Coordinates.altitudeToString(ellipsoidalElevation);
	}

	/**
	 * returns a copy of this WGS84 coordinate
	 * 
	 * @return new WGS84 coordinate identical to <code>this</code>
	 */
	public WGS84 toWGS84() {
		return new WGS84(longitude, latitude, ellipsoidalElevation);
	}

	/** Fabrique de classe */
	public static class WGS84Factory extends CoordinatesFactory {
		/**
		 * @see fr.geo.convert.CoordinatesFactory#create(fr.geo.convert.WGS84)
		 */
		public Coordinates create(WGS84 from) {
			return new WGS84(from.getLongitude(), from.getLatitude(),
					from.getEllipsoidalElevation());
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CoordinatesFactory.factories.put(WGS84.class.getName(),
				new WGS84Factory());
	}
}
