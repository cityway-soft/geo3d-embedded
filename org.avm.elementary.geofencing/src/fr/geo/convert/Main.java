package fr.geo.convert;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		LambertIIe from = new LambertIIe(187124.0, 2411810.0, 0);
		Coordinates to = from.convert(Lambert93.class.getName());
		System.out.println(from);
		System.out.println(to);
		Coordinates result = to.convert(LambertIIe.class.getName());
		System.out.println(result);

	}

}
