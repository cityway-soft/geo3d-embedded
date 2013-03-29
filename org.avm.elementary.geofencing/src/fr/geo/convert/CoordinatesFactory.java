package fr.geo.convert;

public abstract class CoordinatesFactory {

	/**
	 * Comment for <code>factories</code>
	 */
	public static java.util.Map factories = new java.util.HashMap();

	/**
	 * each coordinate system should be able to create a new coordinate
	 * converted from a reference WGS84 coordinate
	 * 
	 * @param from
	 *            reference WGS84 coordinate
	 */
	public abstract Coordinates create(WGS84 from);

	/**
	 * each coordinate system should be able to create a new coordinate
	 * converted from a reference WGS84 coordinate
	 * 
	 * @param from
	 *            reference WGS84 coordinate
	 */
	public static final Coordinates create(String name, WGS84 from)
			throws ClassNotFoundException {
		if (!factories.containsKey(name)) {
			Class.forName(name);
			if (!factories.containsKey(name))
				throw new ClassNotFoundException(name);
		}
		return ((CoordinatesFactory) factories.get(name)).create(from);
	}
}