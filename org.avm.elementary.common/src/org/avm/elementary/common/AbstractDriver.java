package org.avm.elementary.common;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractDriver extends ServiceTracker {

	protected Logger _log = Logger.getInstance(this.getClass());

	protected Device _device;

	protected ComponentContext _context;

	protected ServiceRegistration _sr;

	public AbstractDriver(ComponentContext context, ServiceReference device) {
		super(context.getBundleContext(), device, null);
		_context = context;
		open();
	}

	public Object addingService(ServiceReference reference) {
		Object o = super.addingService(reference);
		if (o instanceof Device) {
			_device = (Device) o;
			DeviceConfig config = _device.getConfig();
			_log.info("Driver " + config.getCategory() + " started");
			start(config);
			String[] clazzes = { this.getClass().getName(),
					config.getCategory() };
			Properties properties = new Properties();
			properties.setProperty(org.osgi.framework.Constants.SERVICE_PID,
					config.getName());
			_sr = context.registerService(clazzes, this, properties);
		}

		return o;
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		if (service instanceof Device) {
			DeviceConfig config = _device.getConfig();
			_log.info("Driver " + config.getCategory() + " stopped");
			stop();
			if (_sr != null) {
				_sr.unregister();
				_sr = null;
			}
			_device = null;
		}
	}

	protected abstract void start(DeviceConfig config);

	protected abstract void stop();
}
