package org.avm.device.nomad.io;

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

public class AnalogInputDriver implements Driver, Constants {

	private Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = AnalogIODevice.class.getName();

	public static final String MANUFACTURER = "kerlink.fr";

	public static final String MODEL = "org.avm.device.nomad.io.AnalogInput";

	public static final String SERIAL = "1904d68b-fe58-4bc0-aece-04b83d0cf5ad";

	public static final String FILTER = "(&" + " ( "
			+ org.osgi.framework.Constants.OBJECTCLASS + "="
			+ AnalogIODevice.class.getName() + ")" + " ( " + DEVICE_CATEGORY
			+ "=" + CATEGORY + ")" + " (" + IOCardInfo.DEVICE_SERIAL + "="
			+ SERIAL + ")" + ")";

	private Filter _filter;

	private ComponentContext _context;

	private Vector _drivers = new Vector();

	private AnalogInputServiceTracker _tracker;

	public AnalogInputDriver() {
	}

	public String attach(ServiceReference device) throws Exception {
		AnalogInputService driver = new AnalogInputService(_context
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
		_tracker = new AnalogInputServiceTracker(context
				.getBundleContext());
		_tracker.open();
	}

	protected void deactivate(ComponentContext context) {
		_log.info("Component deactivated");
		for (Enumeration iter = _drivers.elements(); iter.hasMoreElements();) {
			AnalogInputService driver = (AnalogInputService) iter.nextElement();
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

	class AnalogInputServiceTracker extends ServiceTracker {

		public AnalogInputServiceTracker(BundleContext context) {
			super(context, AnalogInputService.class.getName(), null);
		}

		public Object addingService(ServiceReference reference) {
			AnalogInputService driver = (AnalogInputService) super
					.addingService(reference);
			_drivers.add(driver);
			return driver;
		}

		public void removedService(ServiceReference reference, Object service) {
			if (service instanceof AnalogInputService) {
				AnalogInputService driver = (AnalogInputService) service;
				driver.close();
				_drivers.remove(driver);
			}

			super.removedService(reference, service);
		}

	}
}
