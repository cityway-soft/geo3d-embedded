package org.avm.elementary.database.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.avm.elementary.common.AbstractManager;
import org.avm.elementary.common.Config;
import org.avm.elementary.database.bundle.Activator;
import org.osgi.framework.BundleContext;

public class DatabaseManager extends AbstractManager {

	protected DatabaseConfig _config;

	public DatabaseManager(BundleContext context, DatabaseConfig config)
			throws Exception {
		super(context, Activator.getDefault().getLogger());
		_config = config;
	}

	protected URL getUrlRepository() {
		URL url = null;
		try {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat.format(_config.getUrlConnection(),
					arguments);
			url = new URL("file://" + text.substring(text.lastIndexOf(':') + 1));
		} catch (MalformedURLException e) {
			_log.error(e.getMessage());
		}

		return url;
	}

	protected void updateUrlRepository(URL url, String version) {
		_config.setUrlConnection("jdbc:hsqldb:file:" + url.getPath());
		_config.setVersion(version);
		((Config) _config).updateConfig(_started);
	}

	protected String getDeployerPID() {
		String pid = Activator.getDefault().getPid();
		String result = pid.substring(0, pid.lastIndexOf('.'));
		return result + ".data";
	}

}
