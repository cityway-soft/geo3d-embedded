package org.avm.device.fm6000.io;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.avm.device.io.AnalogIODevice;
import org.avm.device.io.IOCardInfo;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class FM6000AnalogInputDriver implements Driver, Constants {

	private Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = AnalogIODevice.class.getName();

	public static final String MANUFACTURER = "b2i.fr";

	public static final String MODEL = "org.avm.device.io.fm6000.FM6000AnalogInput";

	public static final String SERIAL = "ff125003-8113-414c-acce-2506269a59a2";

	public static final String FILTER = "(&" + " ( "
			+ org.osgi.framework.Constants.OBJECTCLASS + "="
			+ AnalogIODevice.class.getName() + ")" + " ( " + DEVICE_CATEGORY
			+ "=" + CATEGORY + ")" + " (" + IOCardInfo.DEVICE_SERIAL + "="
			+ SERIAL + ")" + ")";

	private Filter _filter;

	private ComponentContext _context;

	private Vector _drivers = new Vector();

	private FM6000AnalogInputServiceTracker _tracker;

	public FM6000AnalogInputDriver() {
	}

	public String attach(ServiceReference device) throws Exception {
		FM6000AnalogInputService driver = new FM6000AnalogInputService(_context
				.getBundleContext(), device);
		driver.open();
		return null;
	}

	public int match(ServiceReference device) throws Exception {

		if (getFilter().match(device))
			return AnalogIODevice.MATCH_SERIAL;
		else
			return Device.MATCH_NONE;
	}

	protected void activate(ComponentContext context) {
		_log.info("Components activated");
		_context = context;
		_tracker = new FM6000AnalogInputServiceTracker(context
				.getBundleContext());
		_tracker.open();
	}

	protected void deactivate(ComponentContext context) {
		_log.info("Component deactivated");
		for (Enumeration iter = _drivers.elements(); iter.hasMoreElements();) {
			FM6000AnalogInputService driver = (FM6000AnalogInputService) iter
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

	class FM6000AnalogInputServiceTracker extends ServiceTracker {

		public FM6000AnalogInputServiceTracker(BundleContext context) {
			super(context, FM6000AnalogInputService.class.getName(), null);
		}

		public Object addingService(ServiceReference reference) {
			FM6000AnalogInputService driver = (FM6000AnalogInputService) super
					.addingService(reference);
			_drivers.add(driver);
			return driver;
		}

		public void removedService(ServiceReference reference, Object service) {
			if (service instanceof FM6000AnalogInputService) {
				FM6000AnalogInputService driver = (FM6000AnalogInputService) service;
				driver.close();
				_drivers.remove(driver);
			}

			super.removedService(reference, service);
		}

	}
}
