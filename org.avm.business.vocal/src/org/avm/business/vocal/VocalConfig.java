package org.avm.business.vocal;

import java.util.Properties;

public interface VocalConfig {

	String getFileName();

	public void setFileName(String path);
	
	public String[] getLanguages();

	public long getSleepBeforePlay();

	public void setSleepBeforePlay(long time);
	
	void addProperty(String name, Properties properties);

	void removeProperty(String name);

	Properties getProperty(String key);
}
