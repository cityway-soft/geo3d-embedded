package org.avm.elementary.database;

public interface DatabaseInjector {
	public void setDatabase(Database database);

	public void unsetDatabase(Database database);
}
