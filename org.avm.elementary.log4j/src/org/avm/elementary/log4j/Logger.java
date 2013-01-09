package org.avm.elementary.log4j;

public interface Logger {

	public void enableSyslog(String bindAddress, String hostAdress);

	public void disableSyslog();
}
