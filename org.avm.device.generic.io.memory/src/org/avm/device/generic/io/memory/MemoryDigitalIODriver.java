package org.avm.device.generic.io.memory;

import org.apache.log4j.Logger;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.IOCardInfo;
import org.avm.elementary.common.AbstractActivator;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class MemoryDigitalIODriver extends AbstractActivator implements Driver,
		Constants {

	private Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = DigitalIODevice.class.getName();

	public static final String MANUFACTURER = "mercur.fr";

	public static final String MODEL = "org.avm.device.io.memory.MemoryDigitalIO";

	public static final String SERIAL = "3b2a8026-a90c-430e-b002-7109f6c73b4f";

	public static final String _spec = "(&" + " ( " + DEVICE_CATEGORY + "="
			+ CATEGORY + ")" + " (" + IOCardInfo.DEVICE_MODEL + "=" + MODEL
			+ ")" + ")";

	private Filter _filter;

	private ServiceTracker _tracker;

	public MemoryDigitalIODriver() {
	}

	public String attach(ServiceReference device) throws Exception {
		MemoryDigitalIOService driver = new MemoryDigitalIOService(_context,
				device);
		return null;
	}

	public int match(ServiceReference device) throws Exception {

		if (getFilter().match(device))
			return DigitalIODevice.MATCH_MODEL;
		else
			return Device.MATCH_NONE;
	}

	protected void start(ComponentContext context) {
		_tracker = new ServiceTracker(context.getBundleContext(),
				MemoryDigitalIOService.class.getName(), null);
		_tracker.open();
	}

	protected void stop(ComponentContext context) {
		Object[] services = _tracker.getServices();
		for (int i = 0; i < services.length; i++) {
			MemoryDigitalIOService driver = (MemoryDigitalIOService) services[i];
			driver.close();
		}
		_tracker.close();
	}

	private Filter getFilter() {
		if (_filter == null) {
			try {
				_filter = _context.getBundleContext().createFilter(_spec);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
		return _filter;
	}
}
