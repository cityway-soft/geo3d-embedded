package org.avm.elementary.geofencing.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.avm.elementary.common.AbstractManager;
import org.avm.elementary.common.Config;
import org.avm.elementary.geofencing.bundle.Activator;
import org.osgi.framework.BundleContext;

public class GeoFencingManager extends AbstractManager {
	protected GeoFencingConfig _config;

	public GeoFencingManager(BundleContext context, GeoFencingConfig config)
			throws Exception {
		super(context, Activator.getDefault().getLogger());
		_config = config;
	}

	protected URL getUrlRepository() {
		URL url = null;
		try {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat
					.format(_config.getFileName(), arguments);
			url = new URL("file://" + text);
		} catch (MalformedURLException e) {
			_log.error(e.getMessage());
		}
		return url;
	}

	protected void updateUrlRepository(URL url, String version) {
		_config.setFileName(url.getPath());
		((Config) _config).updateConfig(_started);
	}

	protected String getDeployerPID() {
		String pid = Activator.getDefault().getPid();
		String result = pid.substring(0, pid.lastIndexOf('.'));
		return result + ".data";
	}
}
