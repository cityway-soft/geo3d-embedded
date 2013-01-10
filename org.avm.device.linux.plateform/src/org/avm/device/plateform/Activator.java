package org.avm.device.plateform;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

public class Activator implements BundleActivator {

	private static final Plugin _plugin = new Implementation();
	private static BundleContext _context;

	public Activator() {
	}

	public static Plugin getPlugin() {
		return _plugin;
	}

	public void start(BundleContext context) throws Exception {
		_context = context;
	}

	public void stop(BundleContext context) throws Exception {
		_context = null;
	}

	public static class Implementation implements Plugin {

		private Version _version;

		public Version getVersion() {
			if (_version == null) {
				if (_context != null) {
					Bundle bundle = _context.getBundle();
					Dictionary map = bundle.getHeaders();
					String text = (String) map.get(Constants.BUNDLE_VERSION);
					_version = Version.parseVersion(text);
				}
			}
			return _version;
		}
	}
}