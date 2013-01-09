package org.avm.elementary.directory.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.microedition.io.ConnectionNotFoundException;



public class CMDataXLMSerializer {

	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
	private static final String CM_HEADER = "<cm_data version=\"0.1\">\n";
	private static final String CONFIG_HEADER = "\t<configuration pid=\"org.avm.elementary.directory.Directory\">\n";
	private static final String CM_FOOTER = "</cm_data>\n";
	private static final String CONFIG_FOOTER = "\t</configuration>\n";

	private static void prepareHeader(final StringBuffer buffer) {
		buffer.append(XML_HEADER);
		buffer.append(CM_HEADER);
		buffer.append(CONFIG_HEADER);
	}

	private static void prepareFooter(final StringBuffer buffer) {
		buffer.append(CONFIG_FOOTER);
		buffer.append(CM_FOOTER);
	}

	public static boolean save(final String filename,
			final Properties properties) {
		final StringBuffer buffer = new StringBuffer();
		prepareHeader(buffer);
		final Enumeration en = properties.keys();
		while (en.hasMoreElements()) {
			final String key = (String) en.nextElement();
			buffer.append("\t\t<property name=\"");
			buffer.append(key);
			buffer.append("\">\n");
			buffer.append("\t\t\t<value type=\"String\">\n");
			buffer.append(properties.get(key));
			buffer.append("\t\t\t</value>\n");
			buffer.append("\n\t\t</property>\n");
		}
		prepareFooter(buffer);
		try {
			final FileOutputStream fos = new FileOutputStream(filename, false);
			fos.write(buffer.toString().getBytes());
			fos.flush();
		} catch (final ConnectionNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
