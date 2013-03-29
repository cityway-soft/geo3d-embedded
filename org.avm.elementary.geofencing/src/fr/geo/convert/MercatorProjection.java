package fr.geo.convert;

/**
 * Mercator transverse projection of a point.
 */
class MercatorProjection extends Projection {
	/**
	 * false east in meters (constant)
	 */
	protected static final double Xs = 500000.0;

	/**
	 * false north in meters (0 in northern hemisphere, 10000000 in southern
	 * hemisphere)
	 */
	protected double Ys;

	/**
	 * origin meridian longitude
	 */
	protected double lg0;

	/**
	 * UTM zone (from 1 to 60)
	 */
	protected int zone;

	/**
	 * initializes new projection coordinates (in north hemisphere)
	 * 
	 * @param east
	 *            east from origin in meters
	 * @param north
	 *            north from origin in meters
	 * @param zone
	 *            zone number (from 1 to 60)
	 * @param isNorth
	 *            true in north hemisphere, false in south hemisphere
	 */
	public MercatorProjection(double east, double north, int zone,
			boolean isNorth) {
		super(east, north);
		Ys = isNorth ? 0.0 : 10000000.0;
		if (zone > 60)
			zone = 60;
		else if (zone < 1)
			zone = 1;
		this.zone = zone;
		double r6d = Math.PI / 30.0;
		lg0 = r6d * (zone - 0.5) - Math.PI;
	}

	/**
	 * initalizes from geographic coordinates
	 * 
	 * @param coord
	 *            geographic coordinates triplet
	 * @param a
	 *            reference ellipsoid long axis
	 * @param e
	 *            reference ellipsoid excentricity
	 */
	public MercatorProjection(Geographic coord, double a, double e) {
		double n = 0.9996 * a;
		Ys = (coord.lt() >= 0.0) ? 0.0 : 10000000.0;
		double r6d = Math.PI / 30.0;
		zone = (int) Math.floor((coord.lg() + Math.PI) / r6d) + 1;
		lg0 = r6d * (zone - 0.5) - Math.PI;
		double e2 = e * e;
		double e4 = e2 * e2;
		double e6 = e4 * e2;
		double e8 = e4 * e4;
		double C[] = {
				1.0 - e2 / 4.0 - 3.0 * e4 / 64.0 - 5.0 * e6 / 256.0 - 175.0
						* e8 / 16384.0,
				e2 / 8.0 - e4 / 96.0 - 9.0 * e6 / 1024.0 - 901.0 * e8
						/ 184320.0,
				13.0 * e4 / 768.0 + 17.0 * e6 / 5120.0 - 311.0 * e8 / 737280.0,
				61.0 * e6 / 15360.0 + 899.0 * e8 / 430080.0,
				49561.0 * e8 / 41287680.0 };
		double s = e * Math.sin(coord.lt());
		double l = Math.log(Math.tan(Math.PI / 4.0 + coord.lt() / 2.0)
				* Math.pow((1.0 - s) / (1.0 + s), e / 2.0));
		double phi = Math.asin(Math.sin(coord.lg() - lg0)
				/ ((Math.exp(l) + Math.exp(-l)) / 2.0));
		double ls = Math.log(Math.tan(Math.PI / 4.0 + phi / 2.0));
		double lambda = Math.atan(((Math.exp(l) - Math.exp(-l)) / 2.0)
				/ Math.cos(coord.lg() - lg0));

		north = C[0] * lambda;
		east = C[0] * ls;
		for (int k = 1; k < 5; k++) {
			double r = 2.0 * k * lambda;
			double m = 2.0 * k * ls;
			double em = Math.exp(m);
			double en = Math.exp(-m);
			double sr = Math.sin(r) / 2.0 * (em + en);
			double sm = Math.cos(r) / 2.0 * (em - en);
			north += C[k] * sr;
			east += C[k] * sm;
		}
		east *= n;
		east += Xs;
		north *= n;
		north += Ys;
	}

	/**
	 * returns false east
	 */
	public double Xs() {
		return Xs;
	}

	/**
	 * returns false north
	 */
	public double Ys() {
		return Ys;
	}

	/**
	 * returns origin meridian longitude
	 */
	public double lg0() {
		return lg0;
	}

	/**
	 * returns UTM zone (from 1 to 60)
	 */
	public int zone() {
		return zone;
	}

	/**
	 * returns true if the coordinate is in north hemisphere, false in south
	 * hemisphere.
	 */
	public boolean isNorth() {
		return Ys == 0.0;
	}
}