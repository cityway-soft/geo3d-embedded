package org.avm.device.road.watchdog.bundle;

import org.avm.device.road.watchdog.Watchdog;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

public class Activator implements BundleActivator {

	private static BundleContext _context;
	
	private Watchdog _peer;

	public Activator() {
		_peer = new Watchdog();
	}

	public void start(BundleContext context) throws Exception {
		_context = context;
		_peer.start();
	}

	public void stop(BundleContext context) throws Exception {
		_peer.stop();
		_context = null;
	}

	public static StartLevel getStartLevelService() {
		StartLevel result = null;
		if (_context != null) {
			ServiceReference sr = _context.getServiceReference(StartLevel.class
					.getName());
			result = (StartLevel) _context.getService(sr);
		}
		return result;
	}
}
