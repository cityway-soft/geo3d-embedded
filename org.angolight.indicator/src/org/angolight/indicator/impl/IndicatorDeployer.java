package org.angolight.indicator.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.angolight.indicator.bundle.Activator;
import org.avm.elementary.common.AbstractManager;
import org.avm.elementary.common.Config;
import org.osgi.framework.BundleContext;

public class IndicatorDeployer extends AbstractManager {
	protected IndicatorConfig _config;

	public IndicatorDeployer(BundleContext context, IndicatorConfig config)
			throws Exception {
		super(context, Activator.getDefault().getLogger());
		_config = config;
	}

	protected URL getUrlRepository() {
		URL url = null;
		try {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat
					.format(_config.getFilename(), arguments);
			url = new URL("file://" + text);
		} catch (MalformedURLException e) {
			_log.error(e.getMessage());
		}
		return url;
	}

	protected void updateUrlRepository(URL url, String version) {
		_config.setFilename(url.getPath());
		((Config) _config).updateConfig(_started);
	}

	protected String getDeployerPID() {
		String pid = Activator.getDefault().getPid();
		String result = pid.substring(0, pid.lastIndexOf('.'));
		return result + ".data";
	}
}
