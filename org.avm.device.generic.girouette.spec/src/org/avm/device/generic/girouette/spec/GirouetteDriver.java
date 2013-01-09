package org.avm.device.generic.girouette.spec;

import org.apache.log4j.Logger;
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

	private final Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = Girouette.class.getName();

	public static final String MODEL = "org.avm.device.girouette.spec";

	public static final String _spec = "(&" + " ( " + DEVICE_CATEGORY + "="
			+ CATEGORY + ")" + " (" + DeviceConfig.DEVICE_MODEL + "=" + MODEL
			+ ")" + ")";

	private Filter _filter;

	private ServiceTracker _tracker;

	GirouetteService driver;

	public GirouetteDriver() {
		_log.debug("[FLA] : constructeur");
	}

	private Filter getFilter() {
		if (_filter == null) {
			try {
				_filter = _context.getBundleContext().createFilter(_spec);
			} catch (final Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
		return _filter;
	}

	protected void start(final ComponentContext context) {
		_log.debug("[FLA] : start");
		_tracker = new ServiceTracker(context.getBundleContext(),
				GirouetteService.class.getName(), null);
		_tracker.open();
	}

	protected void stop(final ComponentContext context) {
		_log.debug("[FLA] : stop");
		final Object[] services = _tracker.getServices();
		for (int i = 0; i < services.length; i++) {
			final GirouetteService driver = (GirouetteService) services[i];
			driver.close();
		}
		_tracker.close();
	}

	public String attach(final ServiceReference device) throws Exception {
		_log.debug("[FLA] : avant instance girouetteService");
		driver = new GirouetteService(_context, device);
		_log.debug("[FLA] : après instance girouetteService");
		return null;
	}

	public int match(final ServiceReference device) throws Exception {
		_log.debug("[FLA] : match");
		if (getFilter().match(device)) {
			return Device.MATCH_MODEL;
		} else {
			return Device.MATCH_NONE;
		}
	}
}
