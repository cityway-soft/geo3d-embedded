package org.avm.elementary.management.core;

import java.io.IOException;

/*
 * Created on 1 sept. 2005
 * Copyright (c) Mercur
 */

public class BundleProperties {
	private String _version;

	private String _name;

	private String _nameOptions = null;

	private int _startlevel;
	
	private boolean _enable=true;

	public static final int NOT_SET = 0;

	public BundleProperties() {
	}

	public void loadProperties(String filename) throws IOException {
		ManifestReader reader = new ManifestReader(filename);
		_name = reader.getProperty("Bundle-SymbolicName");
		if (_name != null) {
			int idx = _name.indexOf(";");
			if (idx != -1) {
				_name = _name.substring(0, idx);
			}

		}
		_version = reader.getProperty("Bundle-Version");

		_startlevel = NOT_SET;
		
		_enable = true;
	}



	public String getName() {
		return _name;
	}

	public String getCompleteName() {
		return _name;
	}

	public int getStartlevel() {
		return _startlevel;
	}

	public void setStartlevel(int startlevel) {
		_startlevel = startlevel;
	}

	public String getVersion() {
		return _version;
	}
	
	public boolean isEnable() {
		return _enable;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setNameOptions(String opts) {
		_nameOptions = opts;
	}

	public String getNameOptions() {
		return _nameOptions;
	}

	public void setVersion(String version) {
		_version = version;
	}
	
	public void setEnable(boolean b) {
		_enable = b;
	}

	public String toString() {
		String tag = "";
		if (_nameOptions != null) {
			tag = "#" + _nameOptions;
		}
		return (_startlevel + ";" + _name + tag + ";" + _version + (_enable?"":";disabled"));
	}
}
