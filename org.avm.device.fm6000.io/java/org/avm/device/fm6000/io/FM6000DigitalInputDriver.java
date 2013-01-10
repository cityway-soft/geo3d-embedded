package org.avm.device.fm6000.io;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
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

public class FM6000DigitalInputDriver implements Driver, Constants {

	private Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = DigitalIODevice.class.getName();

	public static final String MANUFACTURER = "b2i.fr";

	public static final String MODEL = "org.avm.device.io.fm6000.FM6000DigitalInput";

	public static final String SERIAL = "5a00051d-1a44-4693-a2cc-0a326211b614";

	public static final String FILTER = "(&" + " ( "
			+ org.osgi.framework.Constants.OBJECTCLASS + "="
			+ DigitalIODevice.class.getName() + ")" + " ( " + DEVICE_CATEGORY
			+ "=" + CATEGORY + ")" + " (" + IOCardInfo.DEVICE_SERIAL + "="
			+ SERIAL + ")" + ")";

	private Filter _filter;

	private ComponentContext _context;

	private Vector _drivers = new Vector();

	private FM6000DigitalInputServiceTracker _tracker;

	public FM6000DigitalInputDriver() {
	}

	public String attach(ServiceReference device) throws Exception {
		FM6000DigitalInputService driver = new FM6000DigitalInputService(
				_context.getBundleContext(), device);
		driver.open();
		return null;
	}

	public int match(ServiceReference device) throws Exception {
		if (getFilter().match(device))
			return DigitalIODevice.MATCH_SERIAL;
		else
			return Device.MATCH_NONE;
	}

	protected void activate(ComponentContext context) {
		_log.info("Components activated");
		_context = context;
		_tracker = new FM6000DigitalInputServiceTracker(context
				.getBundleContext());
		_tracker.open();

	}

	protected void deactivate(ComponentContext context) {
		_log.info("Component deactivated");
		for (Enumeration iter = _drivers.elements(); iter.hasMoreElements();) {
			FM6000DigitalInputService driver = (FM6000DigitalInputService) iter
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

	class FM6000DigitalInputServiceTracker extends ServiceTracker {

		public FM6000DigitalInputServiceTracker(BundleContext context) {
			super(context, FM6000DigitalInputService.class.getName(), null);
		}

		public Object addingService(ServiceReference reference) {
			FM6000DigitalInputService driver = (FM6000DigitalInputService) super
					.addingService(reference);
			_drivers.add(driver);
			return driver;
		}

		public void removedService(ServiceReference reference, Object service) {
			if (service instanceof FM6000DigitalInputService) {
				FM6000DigitalInputService driver = (FM6000DigitalInputService) service;
				driver.close();
				_drivers.remove(driver);
			}

			super.removedService(reference, service);
		}

	}
}
