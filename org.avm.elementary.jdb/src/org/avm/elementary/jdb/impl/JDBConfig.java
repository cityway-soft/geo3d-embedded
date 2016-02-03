package org.avm.elementary.jdb.impl;

public interface JDBConfig {

	String getFilename();

	void setFilename(String filename);

	String getPattern();

	void setPattern(String pattern);

	int getSize();

	void setSize(int size);
	
	void setSaveMode(boolean mode);
	
	boolean isSaveMode ();

}
