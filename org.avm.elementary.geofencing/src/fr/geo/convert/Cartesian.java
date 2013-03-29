package fr.geo.convert;

/**
 * Cartesian coordinates of a point: (X, Y, Z) triplet expressed in metters. The
 * origin is the earth mass center. The X and Y axis lie in the equatorial
 * plane. The Z axis points to the north.
 */
class Cartesian {
	/**
	 * coordinate along the X axis
	 */
	private double X;

	/**
	 * coordinate along the Y axis
	 */
	private double Y;

	/**
	 * coordinate along the Z axis
	 */
	private double Z;

	/**
	 * initializes from a coordinates triplet
	 */
	public Cartesian(double X, double Y, double Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}

	/**
	 * initalizes from geographic coordinates
	 * 
	 * @param lg
	 *            longitude in radian
	 * @param lt
	 *            latitude in radian
	 * @param h
	 *            ellispoidal elevation in meters
	 * @param ell
	 *            reference ellipsoid
	 */
	public Cartesian(double lg, double lt, double h, Ellipsoid ell) {
		double N = ell.a
				/ (Math.sqrt(1.0 - ell.e2 * Math.sin(lt) * Math.sin(lt)));
		X = (N + h) * Math.cos(lt) * Math.cos(lg);
		Y = (N + h) * Math.cos(lt) * Math.sin(lg);
		Z = (N * (1.0 - ell.e2) + h) * Math.sin(lt);
	}

	/**
	 * initalizes from geographic coordinates
	 * 
	 * @param coord
	 *            geographic coordinates triplet
	 * @param ell
	 *            reference ellipsoid
	 */
	public Cartesian(Geographic coord, Ellipsoid ell) {
		this(coord.lg(), coord.lt(), coord.h(), ell);
	}

	/**
	 * returns 1st coordinate
	 */
	public double X() {
		return X;
	}

	/**
	 * returns 2nd coordinate
	 */
	public double Y() {
		return Y;
	}

	/**
	 * returns 3rd coordinate
	 */
	public double Z() {
		return Z;
	}

	/**
	 * translate the coordinate origin by given translation
	 * 
	 * @param tx
	 *            translation along X axis
	 * @param ty
	 *            translation along Y axis
	 * @param tz
	 *            translation along Z axis
	 */
	public void translate(double tx, double ty, double tz) {
		X += tx;
		Y += ty;
		Z += tz;
	}

	/**
	 * transform the coordinate system by given affine transformation
	 * 
	 * @param tx
	 *            translation along X axis
	 * @param ty
	 *            translation along Y axis
	 * @param tz
	 *            translation along Z axis
	 * @param delta
	 *            scale factor
	 * @param ex
	 *            rotation in X
	 * @param ey
	 *            rotation in Y
	 * @param ez
	 *            rotation in Z
	 */
	public void translate(double tx, double ty, double tz, double delta,
			double ex, double ey, double ez) {
		X += tx + delta * X + ez * Y - ey * Z;
		Y += ty + delta * Y - ez * X + ex * Z;
		Z += tz + delta * Z + ey * X - ex * Y;
	}
}
