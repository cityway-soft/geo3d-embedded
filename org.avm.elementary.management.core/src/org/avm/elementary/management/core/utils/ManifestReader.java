package org.avm.elementary.management.core.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/*
 * Created on 1 sept. 2005
 * Copyright (c) Mercur
 */

public class ManifestReader {
	Manifest _manifest;

	Properties _properties;

	public ManifestReader(String filename) throws IOException {
		try {
			JarFile file = new JarFile(filename);
			_manifest = file.getManifest();
			_properties = new Properties();
			buildProperties();
		} catch (IOException e) {
			System.out.println("Error on  " + filename);
			throw e;
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void buildProperties() {
		Set set = _manifest.getMainAttributes().keySet();
		Iterator iter = set.iterator();
		while (iter.hasNext()) {
			Attributes.Name key = (Attributes.Name) iter.next();
			String value = _manifest.getMainAttributes().getValue(key);
			_properties.put(key.toString(), value);
		}
	}

	public String getProperty(String name) {
		String value = _properties.getProperty(name);
		return value;
	}

	public Properties getProperties() {
		return (Properties) _properties.clone();
	}

	public static void main(String[] args) throws IOException {
		try {
			ManifestReader reader = new ManifestReader(args[0]);
			String filename = args[0];
			String property = null;
			if (args.length > 1) {
				property = args[1];
			}

			if (property == null) {
				Properties props = reader.getProperties();
				System.out.println(props);

			} else {
				String value = reader.getProperty(property);
				System.out.println(value);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Usage <jar filename> [manifest property name]");
		}
	}
}
