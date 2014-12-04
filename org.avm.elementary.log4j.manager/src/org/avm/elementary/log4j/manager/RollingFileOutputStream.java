package org.avm.elementary.log4j.manager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class RollingFileOutputStream extends FileOutputStream {

	private String filename;

	public RollingFileOutputStream(String filename)
			throws FileNotFoundException {
		super(filename,true);
		this.setFilename(filename);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
