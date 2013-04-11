
package org.avm.device.generic.girouette.aesys;

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

public class GirouetteDriver extends AbstractActivator implements Driver, Constants {
	
	private final Logger       log     = Logger.getInstance(this.getClass());
	
	public static final String CATEGORY = Girouette.class.getName();
	
	public static final String MODEL    = "org.avm.device.girouette.aesys";
	
	public static final String spec    = "(&" + " ( " + Constants.DEVICE_CATEGORY + "=" + GirouetteDriver.CATEGORY + ")" + " (" + DeviceConfig.DEVICE_MODEL + "=" + GirouetteDriver.MODEL + ")" + ")";
	
	private Filter             filter;
	
	private ServiceTracker     tracker;
	
	GirouetteService           driver;
	
	public GirouetteDriver() {
	
	}
	
	public String attach(final ServiceReference device) throws Exception {
	
		this.driver = new GirouetteService(this._context, device);
		return null;
	}
	
	public int match(final ServiceReference device) throws Exception {
	
		if (this.getFilter().match(device)) {
			return Device.MATCH_MODEL;
		}
		else {
			return org.osgi.service.device.Device.MATCH_NONE;
		}
	}
	
	protected void start(final ComponentContext context) {
	
		this.tracker = new ServiceTracker(context.getBundleContext(), GirouetteService.class.getName(), null);
		this.tracker.open();
	}
	
	protected void stop(final ComponentContext context) {
	
		final Object[] services = this.tracker.getServices();
		for (int i = 0; i < services.length; i++) {
			final GirouetteService driver = (GirouetteService) services[i];
			driver.close();
		}
		this.tracker.close();
	}
	
	private Filter getFilter() {
	
		if (this.filter == null) {
			try {
				this.filter = this._context.getBundleContext().createFilter(GirouetteDriver.spec);
			}
			catch (final Exception e) {
				this.log.error(e.getMessage(), e);
			}
		}
		return this.filter;
	}
}
