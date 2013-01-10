package org.avm.business.vocal;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.avm.business.vocal.bundle.Activator;
import org.avm.elementary.common.AbstractManager;
import org.avm.elementary.common.Config;
import org.osgi.framework.BundleContext;

public class VocalManager extends AbstractManager {
	protected VocalConfig _config;

	public VocalManager(BundleContext context, VocalConfig config)
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
			if (text.endsWith("/")) {
				url = new URL("file://" + text);
			} else {
				url = new URL("file://" + text + "/");
			}
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
		int idx = pid.lastIndexOf(".");
		pid = pid.substring(0, idx) + ".data";
		return pid;
	}
}
