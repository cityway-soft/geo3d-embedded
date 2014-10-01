package org.avm.elementary.management.core;

import java.io.IOException;

import org.avm.elementary.management.core.utils.ManifestReader;

/*
 * Created on 1 sept. 2005
 * Copyright (c) Mercur
 */

public class BundleProperties {
	private String version;

	private String symbolicName;
	
	private String nameOptions = null;

	private int startlevel;

	private String pack = null;

	private String path;

	public static final int NOT_SET = 0;

	public BundleProperties() {
	}

	public void loadProperties(String filename) throws IOException {
		ManifestReader reader = new ManifestReader(filename);
		symbolicName = reader.getProperty("Bundle-SymbolicName");
		if (symbolicName != null) {
			int idx = symbolicName.indexOf(";");
			if (idx != -1) {
				symbolicName = symbolicName.substring(0, idx);
			}
			
			idx = symbolicName.lastIndexOf("/");
			if (idx != -1){
				symbolicName = symbolicName.substring(idx+1);
			}
		}
		version = reader.getProperty("Bundle-Version");

		startlevel = NOT_SET;

		pack = reader.getProperty("TAB-Pack");
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
	}

	public int getStartlevel() {
		return startlevel;
	}
	
//	public String getPath(){
//		String path =  symbolicName;
//		if (_relativePath != null){
//			path = _relativePath+"/"+symbolicName; 
//		}
//			
//		return path;
//	}
//	
//	public String getRelativePath(){	
//		return _relativePath;
//	}
//	
//	public void setRelativePath(String r){
//		_relativePath = r;
//	}

	public void setStartlevel(int startlevel) {
		this.startlevel = startlevel;
	}

	public String getVersion() {
		return version;
	}

	public void setSymbolicName(String name) {
		symbolicName = name;
	}

	public void setNameOptions(String opts) {
		nameOptions = opts;
	}

	public String getNameOptions() {
		return nameOptions;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String toString() {
		String tag = "";
		if (nameOptions != null) {
			tag = "#" + nameOptions;
		}
		return (startlevel + ";" + symbolicName + tag + ";" + version + (pack == null ? ""
				: (";" + pack)));
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String _pack) {
		this.pack = _pack;
	}
}