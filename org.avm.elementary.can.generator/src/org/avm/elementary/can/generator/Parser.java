package org.avm.elementary.can.generator;

import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

public class Parser {

	private Logger log = Logger.getLogger(Parser.class);

	public CAN parse(InputSource spec, URL rules) {
		CAN o = null;
		try {

			Digester digester = DigesterLoader.createDigester(rules);
			o = (CAN) digester.parse(spec);
			log.info(Catalog.getInstance());
			log.info(o.toString());

		} catch (Exception e) {
			log.error(e);
		}
		return o;
	}
}
