package org.avm.elementary.directory.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.avm.elementary.common.AbstractManager;
import org.avm.elementary.common.Config;
import org.avm.elementary.directory.bundle.Activator;
import org.osgi.framework.BundleContext;

public class DirectoryManager extends AbstractManager {

	protected DirectoryConfig _config;

	public DirectoryManager(final BundleContext context,
			final DirectoryConfig config) throws Exception {
		super(context, Activator.getDefault().getLogger());
		_config = config;
		_log.debug("Constructor of directory manager");
	}

	protected URL getUrlRepository() {
		URL url = null;
		try {
			final Object[] arguments = { System.getProperty("org.avm.home") };
			final String filename = _config.getFileName();
			if (filename != null) {
				final String text = MessageFormat.format(filename, arguments);
				if (text != null) {
					if (text.endsWith("/")) {
						url = new URL("file://" + text);
					} else {
						url = new URL("file://" + text + "/");
					}
				}
			}
		} catch (final MalformedURLException e) {
			_log.error(e.getMessage());
		}
		return url;
	}

	protected void updateUrlRepository(final URL url, final String version) {
		_config.setFileName(url.getPath());
		((Config) _config).updateConfig(_started);
		_config.loadProperties(null);
	}

	protected String getDeployerPID() {
		String pid = Activator.getDefault().getPid();
		final int idx = pid.lastIndexOf(".");
		pid = pid.substring(0, idx) + ".data";
		return pid;
	}

}
