package org.avm.elementary.jdb;

import java.util.Date;

public interface JDB {
	public void journalize(String category, String message);

	public void sync();

	public int getCheckPeriod();

	public String getScheduledFilename(Date date);

	public String getRootPath();
}
