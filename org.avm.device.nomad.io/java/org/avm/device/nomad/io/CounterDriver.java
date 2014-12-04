package org.avm.device.nomad.io;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.avm.device.io.CounterDevice;
import org.avm.device.io.DigitalIODevice;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class CounterDriver implements Driver, Constants {

	public static final String CATEGORY = CounterDevice.class.getName();

	public static final String MANUFACTURER = "kerlink.fr";

	public static final String MODEL = "org.avm.device.nomad.io.Counter";

	public static final String SERIAL = "4f522f1a-933f-4517-bb27-6b63de5dd442";

	public static final String FILTER = "(&" + " " + " ( " + DEVICE_CATEGORY
			+ "=" + CATEGORY + ")" + " ( " + DEVICE_SERIAL + "=" + SERIAL + ")"
			+ ")";

	private Logger _log = Logger.getInstance("org.avm.device.io");

	private Filter _filter;

	private ComponentContext _context;

	private Vector _drivers = new Vector();

	private CounterServiceTracker _tracker;

	public CounterDriver() {
	}

	public String attach(ServiceReference device) throws Exception {
		CounterService driver = new CounterService(_context.getBundleContext(),
				device);
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
		_tracker = new CounterServiceTracker(context.getBundleContext());
		_tracker.open();

	}

	protected void deactivate(ComponentContext context) {
		_log.info("Component deactivated");
		for (Enumeration iter = _drivers.elements(); iter.hasMoreElements();) {
			CounterService driver = (CounterService) iter.nextElement();
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

	class CounterServiceTracker extends ServiceTracker {

		public CounterServiceTracker(BundleContext context) {
			super(context, CounterService.class.getName(), null);
		}

		public Object addingService(ServiceReference reference) {
			CounterService driver = (CounterService) super
					.addingService(reference);
			_drivers.add(driver);
			return driver;
		}

		public void removedService(ServiceReference reference, Object service) {
			if (service instanceof CounterService) {
				CounterService driver = (CounterService) service;
				driver.close();
				_drivers.remove(driver);
			}

			super.removedService(reference, service);
		}

	}

}
