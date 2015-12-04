package org.avm.device.vtc1010.watchdog.bundle;

import org.avm.device.vtc1010.common.api.Vtc1010APC;
import org.avm.device.vtc1010.watchdog.Watchdog;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	private static BundleContext _context;
	
	private Watchdog _peer;
	
	private ServiceTracker st;
	
	

	public Activator() {
		_peer = new Watchdog();
	}

	public void start(BundleContext context) throws Exception {
		_context = context;
		st =new ServiceTracker(context, Vtc1010APC.class.getName(), this);
		st.open();
		//_peer.start();
	}

	public void stop(BundleContext context) throws Exception {
		st.close();
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

	
	public Object addingService(ServiceReference sr) {
		Object tmp = _context.getService(sr);
		if (tmp instanceof Vtc1010APC){
			_peer.setVtc1010apc((Vtc1010APC)tmp);
			_peer.start();
		}
		return tmp;
	}

	public void modifiedService(ServiceReference sr, Object arg1) {
		
	}

	public void removedService(ServiceReference sr, Object arg1) {
		Object tmp = _context.getService(sr);
		if (tmp instanceof Vtc1010APC){
			_peer.setVtc1010apc(null);
			_peer.stop();
		}
	}
}
