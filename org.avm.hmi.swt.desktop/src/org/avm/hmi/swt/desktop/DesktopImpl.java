package org.avm.hmi.swt.desktop;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;

public class DesktopImpl extends DesktopIhm implements Desktop,
		ManageableService, ConsumerService, ConfigurableService {

	private Logger _log;


	public DesktopImpl() {
		_log = Logger.getInstance(DesktopImpl.class);

	}
	
	public void setMessageBox(String title, String string, int type) {
		setMessageBox(title, string, type, null);
	}


	public void start() {
		open();
		

	}

	public void stop() {
		close();
	}

	


	public void notify(Object o) {

	}

	



}
