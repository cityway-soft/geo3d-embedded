package org.avm.elementary.rsync.impl;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.rsync.RSync;
import org.metastatic.rsync.v2.Client;

public class RSyncImpl implements ConfigurableService, ManageableService, RSync {

	private Logger _log = Logger.getInstance("org.metastatic.rsync");

	private RSyncConfig _config;

	public RSyncImpl() {

	}

	public void configure(Config config) {
		_config = (RSyncConfig) config;
	}

	public void start() {
		String value = System.getProperty("user.dir");
	}

	public void stop() {
		_log.removeAllAppenders();
	}

	public void rsync(String[] args) {
		Client.rsync(args);
	}

}