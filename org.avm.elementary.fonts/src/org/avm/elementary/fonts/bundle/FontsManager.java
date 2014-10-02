package org.avm.elementary.fonts.bundle;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.avm.elementary.common.AbstractManager;
import org.avm.elementary.common.Config;
import org.avm.elementary.fonts.FontsConfig;
import org.osgi.framework.BundleContext;

public class FontsManager extends AbstractManager{

	private FontsConfig config;
	
	
	public FontsManager(BundleContext context, FontsConfig config) throws Exception {
		super(context, Activator.getDefault().getLogger());
		this.config = config;
	}

	protected String getDeployerPID() {
		String pid = Activator.getDefault().getPid();
		final int idx = pid.lastIndexOf(".");
		pid = pid.substring(0, idx) + ".data";
		return pid;
	}

	protected URL getUrlRepository() {
		URL url = null;
		try {
			final Object[] arguments = { System.getProperty("org.avm.home") };
			final String filename = config.getFontsPath();
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
	
	protected void updateUrlRepository(URL url, String version) {
		config.setFontsPath(url.getPath());
		((Config) config).updateConfig(_started);
		//_config.loadProperties(null);
	}
	



}
