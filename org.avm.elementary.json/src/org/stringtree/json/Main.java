package org.stringtree.json;

import org.json.JSONObject;
import org.json.XML;

public class Main {

	public Main() {
		super();

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Test test = new Test();
		JSONWriter writer = new JSONWriter();
		String text = writer.write(test);
		System.out.println(text);
		// JSONObject o = new JSONObject(text);
		// System.out.println(o);
		// System.out.println(XML.toString(test));

	}

}
