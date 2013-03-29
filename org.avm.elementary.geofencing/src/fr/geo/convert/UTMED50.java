package fr.geo.convert;

/**
 * The UTM (Universal Transverse Mercator) projective system in the ED50 frame
 * (Hayford's Ellipsoid).
 */
public class UTMED50 extends UTM {

	/**
	 * reference ellipsoid
	 */
	protected static final Ellipsoid ellipsoid = Ellipsoid.hayford;

	/**
	 * WGS84 => reference ellipsoid translation
	 */
	protected static final double translation[] = { 84.0, 97.0, 117.0 };

	/** Fabrique de classe */
	public static class UTMED50Factory extends UTMFactory {
	}

	/** Referencement de la fabrique de classe */
	static {
		CoordinatesFactory.factories.put(UTMED50.class.getName(),
				new UTMED50Factory());
	}

}
