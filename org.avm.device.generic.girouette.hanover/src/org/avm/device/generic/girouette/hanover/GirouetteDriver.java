package org.avm.device.generic.girouette.hanover;

import org.apache.log4j.Logger;
import org.avm.device.generic.girouette.hanover.GirouetteService;
import org.avm.device.girouette.Girouette;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.Device;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;

import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class GirouetteDriver extends AbstractActivator implements Driver,
		Constants {

	private Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = Girouette.class.getName();

	public static final String MODEL = "org.avm.device.girouette.hanover";

	public static final String _spec = "(&" + " ( " + DEVICE_CATEGORY + "="
			+ CATEGORY + ")" + " (" + DeviceConfig.DEVICE_MODEL + "=" + MODEL
			+ ")" + ")";

	private Filter _filter;

	private ServiceTracker _tracker;

	public GirouetteDriver() {
	}

	GirouetteService driver;

	public String attach(ServiceReference device) throws Exception {
		driver = new GirouetteService(_context, device);
		return null;
	}

	public int match(ServiceReference device) throws Exception {
		if (getFilter().match(device))
			return Device.MATCH_MODEL;
		else
			return Device.MATCH_NONE;
	}

	protected void start(ComponentContext context) {
		_tracker = new ServiceTracker(context.getBundleContext(),
				GirouetteService.class.getName(), null);
		_tracker.open();
	}

	protected void stop(ComponentContext context) {
		Object[] services = _tracker.getServices();
		for (int i = 0; i < services.length; i++) {
			GirouetteService driver = (GirouetteService) services[i];
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
