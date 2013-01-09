package org.avm.device.generic.io.iocardbus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.generic.io.iocardbus.bundle.Activator;
import org.avm.device.generic.io.iocardbus.bundle.ConfigImpl;
import org.avm.device.io.IOCardInfo;
import org.avm.device.io.iocardbus.IOCardBus;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;

public class IOCardBusImpl implements ConfigurableService, ManageableService,
		IOCardBus {

	private Logger _log = Activator.getDefault().getLogger();

	private ConfigImpl _config;

	private HashMap _cards = new HashMap();

	private BundleContext _context;

	public IOCardBusImpl() {
	}

	public void registerDevice(IOCardInfo card) {

		Class clazz;
		try {
			clazz = Activator.class.getClassLoader().loadClass(
					card.getCategory());
			Device device = (Device) clazz.newInstance();
			Properties properties = new Properties();
			properties.put(org.osgi.framework.Constants.OBJECTCLASS,
					Driver.class.getName());
			properties.put(org.osgi.service.device.Constants.DEVICE_CATEGORY,
					card.getCategory());

			properties.put(IOCardInfo.DEVICE_MODEL, card.getModel());
			properties.put(IOCardInfo.DEVICE_SERIAL, card.getSerial());
			if (card.getManufacturer() != null) {
				properties.put(IOCardInfo.DEVICE_MANUFACTURER, card
						.getManufacturer());
			}

			ServiceRegistration sr = getBundleContext().registerService(
					new String[] { Device.class.getName(), clazz.getName() },
					device, properties);
			_cards.put(card, sr);
			_log.info("card " + card + " inserted.");

		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	public void unregisterDevice(IOCardInfo card) {
		ServiceRegistration sr = (ServiceRegistration) _cards.remove(card);
		sr.unregister();
		_log.info("card " + card + " removed.");
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
	}

	public void start() {
		IOCardInfo[] infos = _config.getIOCCardInfos();
		for (int i = 0; i < infos.length; i++) {
			registerDevice(infos[i]);
		}
	}

	public void stop() {
		for (Iterator iter = _cards.keySet().iterator(); iter.hasNext();) {
			IOCardInfo card = (IOCardInfo) iter.next();
			unregisterDevice(card);
		}
	}

	public BundleContext getBundleContext() {
		return _context;
	}

	public void setBundleContext(BundleContext bundleContext) {
		_context = bundleContext;
	}
}
