package org.avm.device.nomad.io;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.IOCardInfo;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class DigitalInputDriver implements Driver, Constants {

	private Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = DigitalIODevice.class.getName();

	public static final String MANUFACTURER = "kerlink.fr";

	public static final String MODEL = "org.avm.device.io.nomad.DigitalInput";

	public static final String SERIAL = "ccea6a6d-bbfe-4179-a0a2-ab940611c772";

	public static final String FILTER = "(&" + " ( "
			+ org.osgi.framework.Constants.OBJECTCLASS + "="
			+ DigitalIODevice.class.getName() + ")" + " ( " + DEVICE_CATEGORY
			+ "=" + CATEGORY + ")" + " (" + IOCardInfo.DEVICE_SERIAL + "="
			+ SERIAL + ")" + ")";

	private Filter _filter;

	private ComponentContext _context;

	private Vector _drivers = new Vector();

	private DigitalInputServiceTracker _tracker;

	public DigitalInputDriver() {
	
	}

	public String attach(ServiceReference device) throws Exception {
		DigitalInputService driver = new DigitalInputService(_context
				.getBundleContext(), device);
		driver.open();
		return null;
	}

	public int match(ServiceReference device) throws Exception {
		_log.info("[DSU] DigitalInputDriver.match()");
		if (getFilter().match(device))
			return DigitalIODevice.MATCH_SERIAL;
		else
			return Device.MATCH_NONE;
	}

	protected void activate(ComponentContext context) {
		_log.info("Components activated");
		_context = context;
		_tracker = new DigitalInputServiceTracker(context
				.getBundleContext());
		_tracker.open();

	}

	protected void deactivate(ComponentContext context) {
		_log.info("Component deactivated");
		for (Enumeration iter = _drivers.elements(); iter.hasMoreElements();) {
			DigitalInputService driver = (DigitalInputService) iter
					.nextElement();
			driver.close();
			_drivers.remove(driver);
		}
		_tracker.close();
		_context = null;
	}

	private Filter getFilter() {
		if (_filter == null) {
			try {
				_filter = _context.getBundleContext().createFilter(FILTER);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
		return _filter;
	}

	class DigitalInputServiceTracker extends ServiceTracker {

		public DigitalInputServiceTracker(BundleContext context) {
			super(context, DigitalInputService.class.getName(), null);
		}

		public Object addingService(ServiceReference reference) {
			DigitalInputService driver = (DigitalInputService) super
					.addingService(reference);
			_drivers.add(driver);
			return driver;
		}

		public void removedService(ServiceReference reference, Object service) {
			if (service instanceof DigitalInputService) {
				DigitalInputService driver = (DigitalInputService) service;
				driver.close();
				_drivers.remove(driver);
			}

			super.removedService(reference, service);
		}

	}
}
