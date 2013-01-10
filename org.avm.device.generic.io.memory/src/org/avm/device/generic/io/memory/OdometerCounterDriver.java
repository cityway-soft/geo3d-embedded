package org.avm.device.generic.io.memory;

import org.avm.device.io.CounterDevice;
import org.avm.device.io.DigitalIODevice;
import org.avm.elementary.common.AbstractActivator;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class OdometerCounterDriver extends AbstractActivator implements Driver,
		Constants {

	public static final String CATEGORY = CounterDevice.class.getName();

	public static final String MANUFACTURER = "mercur.fr";

	public static final String MODEL = "org.avm.device.io.memory.CounterDevice";

	public static final String SERIAL = "a1bb7bb5-bde8-4dad-9750-2542e5d67550";

	public static final String FILTER = "(&" + " " + " ( " + DEVICE_CATEGORY
			+ "=" + CATEGORY + ")" + " ( " + DEVICE_SERIAL + "=" + SERIAL + ")"
			+ ")";

	private Filter _filter;

	private ServiceTracker _tracker;

	public String attach(ServiceReference device) throws Exception {
		OdometerCounterService driver = new OdometerCounterService(_context,
				device);
		return null;
	}

	public int match(ServiceReference device) throws Exception {
		if (getFilter().match(device))
			return DigitalIODevice.MATCH_SERIAL;
		else
			return Device.MATCH_NONE;
	}

	protected void start(ComponentContext context) {
		_tracker = new ServiceTracker(context.getBundleContext(),
				OdometerCounterService.class.getName(), null);
		_tracker.open();
	}

	protected void stop(ComponentContext context) {
		Object[] services = _tracker.getServices();
		for (int i = 0; i < services.length; i++) {
			OdometerCounterService driver = (OdometerCounterService) services[i];
			driver.close();
		}
		_tracker.close();
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

}
