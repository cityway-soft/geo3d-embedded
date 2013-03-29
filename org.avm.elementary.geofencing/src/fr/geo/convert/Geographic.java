package fr.geo.convert;

/**
 * Geographic coordinates of a point: (longitude, latitude, ellipoidal
 * elevation) triplet. Angles are expressed in radians, elevation in meters. The
 * longitude is the angle between the Greenwich meridian and the plane in which
 * lies the point. The latitude is the angle between the equatorial plane and
 * the normal to the ellipsoid that goes through the point. The ellipsoidal
 * elevation is measure on the normal to the ellipsoid and differs from the
 * altitude up to several tens of meters.
 */
class Geographic {
	/**
	 * precision in iterative schema
	 */
	public static final double epsilon = 1e-11;

	/**
	 * longitude (in radian)
	 */
	private double lg;

	/**
	 * latitude (in radian)
	 */
	private double lt;

	/**
	 * ellipsoidal elevation (in meters)
	 */
	private double h;

	/**
	 * initializes from a coordinates triplet
	 * 
	 * @param lg
	 *            longitude in radian
	 * @param lt
	 *            latitude in radian
	 * @param h
	 *            ellipsoidal elevation in meters
	 */
	public Geographic(double lg, double lt, double h) {
		this.lg = lg;
		this.lt = lt;
		this.h = h;
	}

	/**
	 * initalizes from cartesian coordinates
	 * 
	 * @param X
	 *            1st coordinate in meters
	 * @param Y
	 *            2nd coordinate in meters
	 * @param Z
	 *            3rd coordinate in meters
	 * @param ell
	 *            reference ellipsoid
	 */
	public Geographic(double X, double Y, double Z, Ellipsoid ell) {
		double norm = Math.sqrt(X * X + Y * Y);
		lg = 2.0 * Math.atan(Y / (X + norm));
		lt = Math.atan(Z
				/ (norm * (1.0 - (ell.a * ell.e2 / Math.sqrt(X * X + Y * Y + Z
						* Z)))));
		double delta = 1.0;
		while (delta > epsilon) {
			double s2 = Math.sin(lt);
			s2 *= s2;
			double l = Math.atan((Z / norm)
					/ (1.0 - (ell.a * ell.e2 * Math.cos(lt) / (norm * Math
							.sqrt(1.0 - ell.e2 * s2)))));
			delta = Math.abs(l - lt);
			lt = l;
		}
		double s2 = Math.sin(lt);
		s2 *= s2;
		h = norm / Math.cos(lt) - ell.a / Math.sqrt(1.0 - ell.e2 * s2);
	}

	/**
	 * initalizes from cartesian coordinates
	 * 
	 * @param coord
	 *            cartesian coordinates triplet
	 * @param ell
	 *            reference ellipsoid
	 */
	public Geographic(Cartesian coord, Ellipsoid ell) {
		this(coord.X(), coord.Y(), coord.Z(), ell);
	}

	/**
	 * initalizes from projected coordinates (conic projection)
	 * 
	 * @param coord
	 *            projected coordinates pair
	 * @param Xs
	 *            false east (coordinate system origin) in meters
	 * @param Ys
	 *            false north (coordinate system origin) in meters
	 * @param c
	 *            projection constant
	 * @param n
	 *            projection exponent
	 * @param lg0
	 *            longitude of origin wrt to the Greenwich meridian (in radian)
	 * @param e
	 *            reference ellipsoid excentricity
	 * @param z
	 *            altitude in meters
	 */
	public Geographic(ConicProjection coord, double Xs, double Ys, double c,
			double n, double e, double lg0, double z) {
		double dx = coord.east() - Xs;
		double dy = Ys - coord.north();
		double R = Math.sqrt(dx * dx + dy * dy);
		double gamma = Math.atan(dx / dy);
		double l = -1.0 / n * Math.log(Math.abs(R / c));
		l = Math.exp(l);
		lg = lg0 + gamma / n;
		lt = 2.0 * Math.atan(l) - Math.PI / 2.0;
		double delta = 1.0;
		while (delta > epsilon) {
			double eslt = e * Math.sin(lt);
			double nlt = 2.0
					* Math.atan(Math.pow((1.0 + eslt) / (1.0 - eslt), e / 2.0)
							* l) - Math.PI / 2.0;
			delta = Math.abs(nlt - lt);
			lt = nlt;
		}
		h = z;
	}

	/**
	 * initalizes from projected coordinates (Mercator transverse projection)
	 * 
	 * @param coord
	 *            projected coordinates pair
	 * @param e
	 *            reference ellipsoid excentricity
	 * @param a
	 *            reference ellipsoid long axis
	 * @param z
	 *            altitude in meters
	 */
	public Geographic(MercatorProjection coord, double a, double e, double z) {
		double n = 0.9996 * a;
		double e2 = e * e;
		double e4 = e2 * e2;
		double e6 = e4 * e2;
		double e8 = e4 * e4;
		double C[] = {
				1.0 - e2 / 4.0 - 3.0 * e4 / 64.0 - 5.0 * e6 / 256.0 - 175.0
						* e8 / 16384.0,
				e2 / 8.0 + e4 / 48.0 + 7.0 * e6 / 2048.0 + e8 / 61440.0,
				e4 / 768.0 + 3.0 * e6 / 1280.0 + 559.0 * e8 / 368640.0,
				17.0 * e6 / 30720.0 + 283.0 * e8 / 430080.0,
				4397.0 * e8 / 41287680.0 };
		double l = (coord.north() - coord.Ys()) / (n * C[0]);
		double ls = (coord.east() - coord.Xs()) / (n * C[0]);
		double l0 = l;
		double ls0 = ls;
		for (int k = 1; k < 5; k++) {
			double r = 2.0 * k * l0;
			double m = 2.0 * k * ls0;
			double em = Math.exp(m);
			double en = Math.exp(-m);
			double sr = Math.sin(r) / 2.0 * (em + en);
			double sm = Math.cos(r) / 2.0 * (em - en);
			l -= C[k] * sr;
			ls -= C[k] * sm;
		}
		lg = coord.lg0()
				+ Math.atan(((Math.exp(ls) - Math.exp(-ls)) / 2.0)
						/ Math.cos(l));
		double phi = Math.asin(Math.sin(l)
				/ ((Math.exp(ls) + Math.exp(-ls)) / 2.0));
		l = Math.log(Math.tan(Math.PI / 4.0 + phi / 2.0));
		lt = 2.0 * Math.atan(Math.exp(l)) - Math.PI / 2.0;
		double lt0;
		do {
			lt0 = lt;
			double s = e * Math.sin(lt);
			lt = 2.0
					* Math.atan(Math.pow((1.0 + s) / (1.0 - s), e / 2.0)
							* Math.exp(l)) - Math.PI / 2.0;
		} while (Math.abs(lt - lt0) >= epsilon);
		h = z;
	}

	/**
	 * returns longitude in radian
	 */
	public double lg() {
		return lg;
	}

	/**
	 * returns latitude in radian
	 */
	public double lt() {
		return lt;
	}

	/**
	 * returns ellipsoidal elevation in meters
	 */
	public double h() {
		return h;
	}
}
