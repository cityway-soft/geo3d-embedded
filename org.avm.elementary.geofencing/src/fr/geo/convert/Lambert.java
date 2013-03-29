package fr.geo.convert;

/**
 * The French Lambert projective system in the NTF frame.
 * <p>
 * To minimize projection errors, the Lambert system is divided in 4 zones
 * covering metropolitan France: I in north of France (latitude 53.5 gr to 57.0
 * gr), II in middle of France (latitude 50.5 gr to 53.5 gr), III in south of
 * France (latitude 47.0 gr to 50.5 gr) and IV in Corsica (latitude 45.9 gr to
 * 47.8 gr).
 * </p>
 * <p>
 * The reference for geographic coordinates is Clarke's Ellipsoid.
 * </p>
 */
public class Lambert extends Coordinates {

	/**
	 * Lambert I, II, III, and IV projection exponents
	 */
	public static final double n[] = { 0.7604059656, 0.7289686274,
			0.6959127966, 0.6712679322 };

	/**
	 * Lambert I, II, III, and IV projection constants
	 */
	public static final double c[] = { 11603796.98, 11745793.39, 11947992.52,
			12136281.99 };

	/**
	 * Lambert I, II, III, and IV false east
	 */
	public static final double Xs[] = { 600000.0, 600000.0, 600000.0, 234.358 };

	/**
	 * Lambert I, II, III, and IV false north
	 */
	public static final double Ys[] = { 5657616.674, 6199695.768, 6791905.085,
			7239161.542 };

	/**
	 * Lambert I, II, III, and IV longitudinal offset to Greenwich meridian
	 */
	public static final double lg0 = 0.04079234433198; // 2�20'14.025"

	/**
	 * Lambert zone (should be 1 to 4, 5 means LambertIIe and 6 means
	 * Lambert93).
	 */
	private int zone;

	/**
	 * east coordinate in Lambert frame (in meters)
	 */
	protected double x;

	/**
	 * north coordinate in Lambert frame (in meters)
	 */
	protected double y;

	/**
	 * altitude (in meters)
	 */
	protected double z;

	/**
	 * initializes a default Lambert coordinate: (0, 0, 0) zone 1
	 */
	public Lambert() {
		zone = 1;
		x = y = z = 0;
	}

	/**
	 * initializes a Lambert coordinate
	 * 
	 * @param zone
	 *            Lambert zone (from 1 to 4)
	 * @param x
	 *            Lambert east coordinate in meters
	 * @param y
	 *            Lambert north coordinate in meters
	 * @param z
	 *            Lambert altitude in meters
	 */
	public Lambert(int zone, double x, double y, double z) {
		this.zone = zone;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Lambert east coordinate in meters
	 * 
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * Lambert north coordinate in meters
	 * 
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * Lambert altitude in meters
	 * 
	 * @return
	 */
	public double getZ() {
		return z;
	}

	/**
	 * returns this coordinate as a string
	 * 
	 * @return string formated as "Lambert [1-4] east north altitude"
	 */
	public String toString() {

		String res = new String();
		if (zone <= 4)
			res += " " + String.valueOf(zone);
		return res + " " + Coordinates.lengthToString(x) + " "
				+ Coordinates.lengthToString(y) + " "
				+ Coordinates.altitudeToString(z);
	}

	/**
	 * creates a new WGS84 coordinates object initialized at the same location
	 * than this Lambert coordinate.
	 * 
	 * @return WGS84 coordinates object
	 */
	public WGS84 toWGS84() {
		int zone = this.zone - 1;

		/*
		 * Lambert projection => Lambert geographic
		 */
		ConicProjection proj = new ConicProjection(x, y);
		Geographic geo = new Geographic(proj, Lambert.Xs[zone],
				Lambert.Ys[zone], Lambert.c[zone], Lambert.n[zone],
				Ellipsoid.clarke.e, Lambert.lg0, z);

		// Lambert geographic => Lambert cartesian
		Cartesian lambert = new Cartesian(geo, Ellipsoid.clarke);

		// Lambert => WGS84 ellipsoide similarity
		lambert.translate(-168.0, -60.0, 320.0);

		// WGS84 cartesian => WGS84 geographic
		Geographic wgs = new Geographic(lambert, Ellipsoid.GRS80);

		return new WGS84(wgs.lg(), wgs.lt(), /* wgs.h() */z);
	}

	/** Fabrique de classe */
	public static class LambertFactory extends CoordinatesFactory {

		/**
		 * creates a new Lambert coordinate object initialized at the same
		 * location than the input WGS84 coordinate
		 * 
		 * @param from
		 *            WGS84 coordinate to translate
		 * @return Lambert coordinate object
		 */
		public Coordinates create(WGS84 from) {

			// WGS84 geographic => WGS84 cartesian
			Cartesian wgs = new Cartesian(from.getLongitude(),
					from.getLatitude(), from.getEllipsoidalElevation(),
					Ellipsoid.GRS80);
			// WGS84 => Lambert ellipsoide similarity
			wgs.translate(168.0, 60.0, -320.0);

			// Lambert cartesian => Lambert geographic
			Geographic lambert = new Geographic(wgs, Ellipsoid.clarke);

			int zone = 0;
			// check longitude and latitude
			double lt = lambert.lt();
			double lg = lambert.lg();
			if (lt > 1.0471975511965976 || lt < 0.7173303225696694
					|| lg > 0.1605702911834783 || lg < -0.0770853752964162) {
				System.err.println("out of Lambert zone");
				return new Lambert();
			}
			// zone I
			if (lt > 53.5 * Math.PI / 200.0)
				zone = 0;
			// zone II
			else if (lt > 50.5 * Math.PI / 200.0)
				zone = 1;
			// zone III
			else if (lt > 47.0 * Math.PI / 200.0)
				zone = 2;
			// zone IV (warning: zone IV extends from 47.8 to 45.9 degres of
			// latitude, there is an overlap with zone III that
			// cannot be solved automatically)
			else if (lt > 45.9 * Math.PI / 200.0)
				zone = 3;

			// Lambert geographic => Lambert projection
			ConicProjection proj = new ConicProjection(lambert,
					Lambert.Xs[zone], Lambert.Ys[zone], Lambert.c[zone],
					Lambert.n[zone], Ellipsoid.clarke.e, Lambert.lg0);

			zone++;
			return new Lambert(zone, proj.east(), proj.north(),
			/* lambert.h() */from.getEllipsoidalElevation());
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CoordinatesFactory.factories.put(Lambert.class.getName(),
				new LambertFactory());
	}
}
