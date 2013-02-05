package org.avm.device.generic.afficheur.aesys;

import org.apache.log4j.Logger;
import org.avm.device.afficheur.Afficheur;
import org.avm.elementary.common.AbstractActivator;
import org.avm.elementary.common.Device;
import org.avm.elementary.common.DeviceConfig;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class AfficheurDriver extends AbstractActivator implements Driver,
		Constants {

	private final Logger _log = Logger.getInstance(this.getClass());

	public static final String CATEGORY = Afficheur.class.getName();

	public static final String MODEL = "org.avm.device.afficheur.aesys";

	public static final String _spec = "(&" + " ( " + DEVICE_CATEGORY + "="
			+ CATEGORY + ")" + " (" + DeviceConfig.DEVICE_MODEL + "=" + MODEL
			+ ")" + ")";

	private Filter _filter;

	private ServiceTracker _tracker;

	AfficheurService driver;

	public AfficheurDriver() {
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
		_tracker = new ServiceTracker(context.getBundleContext(),
				AfficheurService.class.getName(), null);
		_tracker.open();
	}

	protected void stop(final ComponentContext context) {
		final Object[] services = _tracker.getServices();
		for (int i = 0; i < services.length; i++) {
			final AfficheurService driver = (AfficheurService) services[i];
			driver.close();
		}
		_tracker.close();
	}

	public String attach(final ServiceReference device) throws Exception {
		driver = new AfficheurService(_context, device);
		return null;
	}

	public int match(final ServiceReference device) throws Exception {
		if (getFilter().match(device)) {
			return Device.MATCH_MODEL;
		} else {
			return Device.MATCH_NONE;
		}
	}
}
