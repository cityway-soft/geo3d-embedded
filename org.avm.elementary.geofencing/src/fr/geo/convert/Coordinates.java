package fr.geo.convert;

/**
 * The abstract base class for all coordinate systems. A Coordinate object
 * represents a point in a given coordinate frame.
 * <p>
 * It is possible to convert each coordinate into a WGS84 frame (
 * <code>toWGS84</code> method) or from a WGS84 frame (
 * <code>create(WGS84)</code> method). This central format allow conversion from
 * any system to any other system.
 * </p>
 * <p>
 * A coordinate may also be exported as a string (<code>toString</code> method)
 * or read from an array of strings (<code>create(String[])</code> method) which
 * is obtained by splitting the toString output.
 * </p>
 * <p>
 * Finally, the coordinate system may be displayed and edited in a graphic
 * interface using the <code>editor</code> method
 * </p>
 */
public abstract class Coordinates {

	public static final int METER = 0;

	public static final int KILOMETER = 1;

	public static final int RADIAN = 0;

	public static final int DEGRE = 1;

	public static final int DEGMN = 2;

	public static final int DEGMNSEC = 3;

	protected static int lengthUnit = METER;

	protected static int angleUnit = RADIAN;

	public static int lengthUnit() {
		return lengthUnit;
	}

	public static int angleUnit() {
		return angleUnit;
	}

	public static void setLengthUnit(int unit) {
		if (unit == KILOMETER)
			lengthUnit = KILOMETER;
		else
			lengthUnit = METER;
	}

	public static void setAngleUnit(int unit) {
		angleUnit = unit;
		if (unit < 0 || unit > DEGMNSEC)
			unit = RADIAN;
	}

	public static String lengthToString(double length) {
		if (lengthUnit == KILOMETER)
			return String.valueOf(Math.round(length) / 1000.0);
		return String.valueOf(Math.round(length));
	}

	public static String altitudeToString(double length) {
		return String.valueOf(Math.round(length));
	}

	public static String angleToString(double angle) {
		if (angleUnit == RADIAN)
			return String.valueOf(angle);
		double v = angle * 180.0 / Math.PI;
		if (angleUnit == DEGRE)
			return String.valueOf(v);
		String res;
		if (v < 0) {
			res = "-";
			v = -v;
		} else
			res = "";
		int d = (int) Math.floor(v);
		res += String.valueOf(d);
		res += '�';
		v -= d;
		v *= 60.0;
		if (angleUnit == DEGMN) {
			res += String.valueOf(Math.round(v * 100000.0) / 100000.0);
			res += '\'';
			return res;
		}
		d = (int) Math.floor(v);
		res += String.valueOf(d);
		res += '\'';
		v -= d;
		v *= 60.0;
		res += String.valueOf(Math.round(v * 1000.0) / 1000.0);
		res += '\"';
		return res;
	}

	/**
	 * each coordinate system should be able to output a string representing
	 * this point in this frame
	 */
	public abstract String toString();

	/**
	 * each coordinate system should be able to create a new WGS84 coordinate
	 * which represent the same point
	 */
	public abstract WGS84 toWGS84();

	public Coordinates convert(String clazz) throws Exception {
		return CoordinatesFactory.create(clazz, toWGS84());

	}

}