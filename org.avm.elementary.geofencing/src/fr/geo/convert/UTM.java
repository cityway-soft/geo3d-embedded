package fr.geo.convert;

/**
 * The UTM (Universal Transverse Mercator) projective system in the GRS84 frame
 * ().
 */
public class UTM extends Coordinates {
	/**
	 * Mercator zone (from 1 to 60). France is covered by zones 30 to 32.
	 */
	private int zone;

	/**
	 * east coordinate in UTM frame (in meters)
	 */
	private double x;

	/**
	 * north coordinate in UTM frame (in meters)
	 */
	private double y;

	/**
	 * altitude (in meters)
	 */
	private double z;

	/**
	 * whether north or south hemisphere
	 */
	private boolean north;

	/**
	 * reference ellipsoid
	 */
	protected static final Ellipsoid ellipsoid = Ellipsoid.GRS80;

	/**
	 * WGS84 => reference ellipsoid translation
	 */
	protected static final double translation[] = { 0.0, 0.0, 0.0 };

	/**
	 * initializes a default Lambert UTM coordinate: (0, 0, 0) zone 30, north
	 */
	public UTM() {
		north = true;
		zone = 30;
		x = y = z = 0;
	}

	/**
	 * initializes a UTM coordinate
	 * 
	 * @param zone
	 *            UTM zone (from 1 to 60)
	 * @param x
	 *            UTM east coordinate in meters
	 * @param y
	 *            UTM north coordinate in meters
	 * @param x
	 *            UTM altitude in meters
	 * @param north
	 *            true if in north hemisphere, false in south hemisphere
	 */
	public UTM(int zone, double x, double y, double z, boolean north) {
		this.zone = zone;
		this.x = x;
		this.y = y;
		this.z = z;
		this.north = north;
	}

	/**
	 * returns this coordinate as a string
	 * 
	 * @return string formated as "UTM <1-60><N|S>east north altitude"
	 */
	public String toString() {
		String res = " " + String.valueOf(zone) + " ";
		if (north)
			res += "N ";
		else
			res += "S ";
		return res + Coordinates.lengthToString(x) + " "
				+ Coordinates.lengthToString(y) + " "
				+ Coordinates.altitudeToString(z);
	}

	/**
	 * creates a new WGS84 coordinates object initialized at the same location
	 * than this UTM coordinate.
	 * 
	 * @return WGS84 coordinates object
	 */
	public WGS84 toWGS84() {

		/*
		 * UTM projection => reference ellipsoid geographic
		 */
		MercatorProjection proj = new MercatorProjection(x, y, zone, north);
		Geographic geo = new Geographic(proj, ellipsoid.a, ellipsoid.e, z);

		// reference ellipsoid geographic => reference ellipsoid cartesian
		Cartesian utm = new Cartesian(geo, ellipsoid);

		// reference ellipsoid => WGS84 ellipsoide similarity
		utm.translate(-translation[0], -translation[1], -translation[2]);

		// WGS84 cartesian => WGS84 geographic
		Geographic wgs = new Geographic(utm, Ellipsoid.GRS80);

		return new WGS84(wgs.lg(), wgs.lt(), /* wgs.h() */z);
	}

	/** Fabrique de classe */
	public static class UTMFactory extends CoordinatesFactory {

		/**
		 * creates a new UTM coordinate object initialized at the same location
		 * than the input WGS84 coordinate
		 * 
		 * @param from
		 *            WGS84 coordinate to translate
		 * @return UTM coordinate object
		 */
		public Coordinates create(WGS84 from) {

			// WGS84 geographic => WGS84 cartesian
			Cartesian wgs = new Cartesian(from.getLongitude(),
					from.getLatitude(), from.getEllipsoidalElevation(),
					Ellipsoid.GRS80);
			// WGS84 => reference ellipsoid similarity
			wgs.translate(translation[0], translation[1], translation[2]);

			// reference ellipsoid cartesian => reference ellipsoid geographic
			Geographic ref = new Geographic(wgs, ellipsoid);

			// reference ellipsoid geographic => UTM projection
			MercatorProjection proj = new MercatorProjection(ref, ellipsoid.a,
					ellipsoid.e);

			return new UTM(proj.zone(), proj.east(), proj.north(), /* ref.h() */
			from.getEllipsoidalElevation(), proj.isNorth());
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CoordinatesFactory.factories.put(UTM.class.getName(), new UTMFactory());
	}
}
