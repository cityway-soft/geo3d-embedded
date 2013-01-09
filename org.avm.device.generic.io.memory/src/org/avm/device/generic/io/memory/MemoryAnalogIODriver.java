package org.avm.device.generic.io.memory;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.avm.device.io.AnalogIODevice;
import org.avm.device.io.IOCardInfo;
import org.avm.elementary.common.AbstractActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class MemoryAnalogIODriver extends AbstractActivator implements Driver,
		Constants {

	public static final String MANUFACTURER = "mercur.fr";

	private Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = AnalogIODevice.class.getName();

	public static final String MODEL = "org.avm.device.io.memory.MemoryAnalogIO";

	public static final String SERIAL = "89f595bf-a2c7-42e7-8178-11836f19a3c1";

	public static final String FILTER = "(&" + " ( "
			+ org.osgi.framework.Constants.OBJECTCLASS + "="
			+ AnalogIODevice.class.getName() + ")" + " ( " + DEVICE_CATEGORY
			+ "=" + CATEGORY + ")" + " (" + IOCardInfo.DEVICE_MODEL + "="
			+ MODEL + ")" + ")";

	private Filter _filter;

	private ServiceTracker _tracker;

	public MemoryAnalogIODriver() {
	}

	public String attach(ServiceReference device) throws Exception {
		MemoryAnalogIOService driver = new MemoryAnalogIOService(_context
				.getBundleContext(), device);
		driver.open();
		return null;
	}

	public int match(ServiceReference device) throws Exception {

		if (getFilter().match(device))
			return AnalogIODevice.MATCH_MODEL;
		else
			return Device.MATCH_NONE;
	}

	protected void start(ComponentContext context) {
		_tracker = new ServiceTracker(context.getBundleContext(),
				MemoryAnalogIOService.class.getName(), null);
		_tracker.open();
	}

	protected void stop(ComponentContext context) {
		Object[] services = _tracker.getServices();
		for (int i = 0; i < services.length; i++) {
			MemoryAnalogIOService driver = (MemoryAnalogIOService) services[i];
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
