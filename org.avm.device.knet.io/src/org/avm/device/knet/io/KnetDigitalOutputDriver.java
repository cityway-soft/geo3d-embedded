package org.avm.device.knet.io;

import org.apache.log4j.Logger;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.knet.sensor.Sensor;
import org.avm.elementary.common.AbstractActivator;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;

public class KnetDigitalOutputDriver extends AbstractActivator implements
		Driver, Constants {

	private Logger _log = Logger.getInstance(this.getClass());

	public static final String MANUFACTURER = "mercur.fr";

	public static final String CATEGORY = DigitalIODevice.class.getName();

	public static final String MODEL = "org.avm.device.io.knet.KnetDigitalIO";

	public static final String SERIAL = "895b1871-cb40-4fcb-9dab-10e394d12552";

	public static final String _spec = "(&" + " " + " ( " + DEVICE_CATEGORY
			+ "=" + CATEGORY + ")" + " ( " + DEVICE_SERIAL + "=" + SERIAL + ")"
			+ ")";

	private Filter _filter;

	private ServiceTracker _tracker;

	/** singleton * */
	private KnetDigitalOutputService _outputService;

	public KnetDigitalOutputDriver() {
	}

	public String attach(ServiceReference device) throws Exception {
		_outputService = new KnetDigitalOutputService(_context, device);
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
				KnetDigitalOutputService.class.getName(), null);
		_tracker.open();
	}

	protected void stop(ComponentContext context) {
		Object[] services = _tracker.getServices();
		for (int i = 0; i < services.length; i++) {
			KnetDigitalOutputService driver = (KnetDigitalOutputService) services[i];
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

	public void setSensor(Sensor sensor) {
		// System.out.println(">>>>>>>>>> KnetDigitalOutputDriver::setSensor");
		_outputService.setSensor(sensor);
	}

	public void unsetSensor(Sensor sensor) {
		// System.out.println(">>>>>>>>>>
		// KnetDigitalOutputDriver::unsetSensor");
		_outputService.setSensor(null);
	}

}
