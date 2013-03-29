package fr.geo.convert;

/**
 * Conic projection of a point.
 */

class ConicProjection extends Projection {
	/**
	 * initializes new projection coordinates
	 * 
	 * @param east
	 *            east from origin in meters
	 * @param north
	 *            north from origin in meters
	 */
	public ConicProjection(double east, double north) {
		super(east, north);
	}

	/**
	 * initalizes from geographic coordinates
	 * 
	 * @param coord
	 *            geographic coordinates triplet
	 * @param Xs
	 *            false east (coordinate system origin) in meters
	 * @param Ys
	 *            false north (coordinate system origin) in meters
	 * @param c
	 *            projection constant
	 * @param n
	 *            projection exponent
	 * @param e
	 *            reference ellipsoid excentricity
	 * @param lg0
	 *            longitude of origin wrt to the Greenwich meridian (in radian)
	 */
	public ConicProjection(Geographic coord, double Xs, double Ys, double c,
			double n, double e, double lg0) {
		double eslt = e * Math.sin(coord.lt());
		double l = Math.log(Math.tan(Math.PI / 4.0 + coord.lt() / 2.0)
				* Math.pow((1.0 - eslt) / (1.0 + eslt), e / 2.0));
		east = Xs + c * Math.exp(-n * l) * Math.sin(n * (coord.lg() - lg0));
		north = Ys - c * Math.exp(-n * l) * Math.cos(n * (coord.lg() - lg0));
	}
}
