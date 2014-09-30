package org.avm.elementary.management.core;

import java.io.IOException;

import org.avm.elementary.management.core.utils.ManifestReader;

/*
 * Created on 1 sept. 2005
 * Copyright (c) Mercur
 */

public class BundleProperties {
	private String _version;

	private String _name;
	
	private String _relativePath="";

	private String _nameOptions = null;

	private int _startlevel;

	private String _pack = null;

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
			
			idx = _name.lastIndexOf("/");
			if (idx != -1){
				_relativePath = _name.substring(0, idx);
				_name = _name.substring(idx+1);
			}
		}
		_version = reader.getProperty("Bundle-Version");

		_startlevel = NOT_SET;

		_pack = reader.getProperty("TAB-Pack");
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
	
	public String getPath(){
		String path =  _name;
		if (_relativePath != null){
			path = _relativePath+"/"+_name; 
		}
			
		return path;
	}
	
	public String getRelativePath(){	
		return _relativePath;
	}
	
	public void setRelativePath(String r){
		_relativePath = r;
	}

	public void setStartlevel(int startlevel) {
		_startlevel = startlevel;
	}

	public String getVersion() {
		return _version;
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

	public String toString() {
		String tag = "";
		if (_nameOptions != null) {
			tag = "#" + _nameOptions;
		}
		return (_startlevel + ";" + _name + tag + ";" + _version + (_pack == null ? ""
				: (";" + _pack)));
	}

	public String getPack() {
		return _pack;
	}

	public void setPack(String _pack) {
		this._pack = _pack;
	}
}