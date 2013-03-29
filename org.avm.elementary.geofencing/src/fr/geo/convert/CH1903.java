package fr.geo.convert;

/* http://www.swisstopo.ch   /  http://topo.epfl.ch/transcoco.php
 * (484000, 74000) -> 5�56'47.5, 45�48'27.8 / 5�56'46.79, 45�48'27.5
 * (550000, 100000) -> 6�47'33.6, 46�02'58.8 / 6�47'33.6, 46�02'58.82
 * (600000, 150000) -> 7�26'19.1, 46�30'04.7 / 7�26'19.08, 46�30'04.72
 * 5�70', 45�50' -> (501159, 76546) / (501158.76, 76546.18)
 * 6�50', 46�25' -> (553463, 140770) / (553462.88, 140770.46)
 * 7�25', 46�30' -> (598314, 149855) / (598313.65, 149854.51)
 */

/**
 * The Swiss projective system.
 */
public class CH1903 extends Coordinates {
	/**
	 * east coordinate in Swiss projection (in meters)
	 */
	protected double x;

	/**
	 * north coordinate in Swiss projection (in meters)
	 */
	protected double y;

	/**
	 * altitude (in meters)
	 */
	protected double z;

	public static final double a = 6377397.155;

	public static final double b = 6356078.962822;

	public static final double phi0 = 46.9524055 * Math.PI / 180.0;

	public static final double lambda0 = 7.4395833 * Math.PI / 180.0;

	public static final double E2 = (a * a - b * b) / (a * a);

	public static final double E = Math.sqrt(E2);

	public static final double R = a * Math.sqrt(1.0 - E2)
			/ (1.0 - E2 * Math.sin(phi0) * Math.sin(phi0));

	public static final double alpha = Math.sqrt(1.0 + E2 / (1.0 - E2)
			* Math.pow(Math.cos(phi0), 4.0));

	public static final double b0 = Math.asin(Math.sin(phi0) / alpha);

	public static final double K = Math.log(Math.tan(Math.PI / 4.0 + b0 / 2.0))
			- alpha * Math.log(Math.tan(Math.PI / 4.0 + phi0 / 2.0)) + alpha
			* E / 2.0
			* Math.log((1.0 + E * Math.sin(phi0)) / (1.0 - E * Math.sin(phi0)));

	/**
	 * initializes a default Swiss coordinate: (0, 0, 0)
	 */
	public CH1903() {
		x = 484000.0;
		y = 74000.0;
		z = 0;
	}

	/**
	 * initializes a Swiss coordinate
	 * 
	 * @param x
	 *            Swiss east coordinate in meters
	 * @param y
	 *            Swiss north coordinate in meters
	 * @param x
	 *            Swiss altitude in meters
	 */
	public CH1903(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		check();
	}

	/**
	 * print an error message if the coordinates fall out of the swiss zone
	 */
	void check() {
		if (x < 484000.0 || x > 834000.0 || y < 74000.0 || y > 296000.0) {
			System.err
					.println("out of Swiss zone (484000 <= east <= 834000, 74000 <= nord <= 296000)");
		}
	}

	/**
	 * returns this coordinate as a string
	 * 
	 * @return string formated as "Swiss east north altitude"
	 */
	public String toString() {
		return " " + Coordinates.lengthToString(x) + " "
				+ Coordinates.lengthToString(y) + " "
				+ Coordinates.altitudeToString(z);
	}

	/**
	 * creates a new WGS84 coordinates object initialized at the same location
	 * than this Swiss coordinate.
	 * 
	 * @return WGS84 coordinates object
	 */
	public WGS84 toWGS84() {

		double y = (this.x - 600000.0) / 1000000.0;
		double y2 = y * y;
		double y3 = y2 * y;
		double x = (this.y - 200000.0) / 1000000.0;
		double x2 = x * x;
		double x3 = x2 * x;
		double lambda = 2.6779094 + 4.728982 * y + 0.791484 * y * x + 0.1306
				* y * x2 - 0.0436 * y3;
		double phi = 16.9023892 + 3.238272 * x - 0.270978 * y2 - 0.002528 * x2
				- 0.0447 * y2 * x - 0.014 * x3;

		return new WGS84(lambda * 100.0 * Math.PI / 6480.0, phi * 100.0
				* Math.PI / 6480.0, z);
	}

	/** Fabrique de classe */
	public static class CH1903Factory extends CoordinatesFactory {
		/**
		 * creates a new Swiss coordinate object initialized at the same
		 * location than the input WGS84 coordinate
		 * 
		 * @param from
		 *            WGS84 coordinate to translate
		 * @return Swiss coordinate object
		 */
		public Coordinates create(WGS84 from) {

			double phi = (from.getLatitude() * 180.0 / Math.PI) * 3600.0;
			double lambda = (from.getLongitude() * 180.0 / Math.PI) * 3600.0;

			phi = (phi - 169028.66) / 10000.0;
			lambda = (lambda - 26782.5) / 10000.0;
			double phi2 = phi * phi;
			double phi3 = phi2 * phi;
			double lambda2 = lambda * lambda;
			double lambda3 = lambda2 * lambda;
			double y = 600072.37 + 211455.93 * lambda - 10938.51 * lambda * phi
					- 0.36 * lambda * phi2 - 44.54 * lambda3;
			double x = 200147.07 + 308807.95 * phi + 3745.25 * lambda2 + 76.63
					* phi2 - 194.56 * lambda2 * phi + 119.79 * phi3;

			return new CH1903(y, x, from.getEllipsoidalElevation());
		}
	}

	/** Referencement de la fabrique de classe */
	static {
		CoordinatesFactory.factories.put(CH1903.class.getName(),
				new CH1903Factory());
	}
}
