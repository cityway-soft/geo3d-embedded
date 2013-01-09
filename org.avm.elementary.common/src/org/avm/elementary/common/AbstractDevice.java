package org.avm.elementary.common;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Driver;

public abstract class AbstractDevice implements ConfigurableService,
		ManageableService, Device {

	private Logger _log = Logger.getInstance(this.getClass());

	private ComponentContext _context;

	private ServiceRegistration _sr;

	private DeviceConfig _config;

	public void configure(Config config) {
		_config = (DeviceConfig) config;
	}

	public void start() {
		registerDevice();
	}

	public void stop() {
		unregisterDevice();
	}

	protected void registerDevice() {
		Class clazz;
		try {

			Properties properties = new Properties();
			properties.put(org.osgi.framework.Constants.OBJECTCLASS,
					Driver.class.getName());
			properties.put(org.osgi.service.device.Constants.DEVICE_CATEGORY,
					_config.getCategory());

			properties.put(DeviceConfig.DEVICE_MODEL, _config.getModel());
			properties.put(DeviceConfig.DEVICE_SERIAL, _config.getSerial());
			properties.put(DeviceConfig.DEVICE_MANUFACTURER, _config
					.getManufacturer());

			Properties p = _config.getParameters();
			for (Enumeration iterator = p.keys(); iterator.hasMoreElements();) {
				String name = (String) iterator.nextElement();
				properties.put(name, p.getProperty(name));
			}

			_sr = _context.getBundleContext().registerService(
					new String[] { Device.class.getName(), }, this, properties);

		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

	}

	protected void unregisterDevice() {
		if (_sr != null) {
			_sr.unregister();
			_sr = null;
		}

	}

	public void noDriverFound() {
		_log.info("Driver " + _config.getCategory() + " not found");

	}

	public ComponentContext getContext() {
		return _context;
	}

	public void setContext(ComponentContext context) {
		_context = context;
	}

	public DeviceConfig getConfig() {
		return _config;
	}

}
