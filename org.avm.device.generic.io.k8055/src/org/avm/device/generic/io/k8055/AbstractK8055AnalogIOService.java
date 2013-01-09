package org.avm.device.generic.io.k8055;

import java.util.Properties;

import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;
import javax.usb.util.UsbUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.io.AnalogIODevice;
import org.avm.device.io.AnalogIODriver;
import org.avm.device.io.DigitalIODevice;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class AbstractK8055AnalogIOService implements AnalogIODriver,
		Driver, Constants, UsbDeviceListener, ServiceTrackerCustomizer {

	protected Logger _log = Logger.getInstance(this.getClass());

	protected K8055UsbDevice _k8055;

	protected Filter _filter;

	protected BundleContext _context;

	protected ServiceRegistration _registration;

	protected ServiceTracker _tracker;

	protected AnalogIODevice _device;

	// Constructor
	public AbstractK8055AnalogIOService(BundleContext context,
			K8055UsbDevice k8055) {
		_context = context;
		_k8055 = k8055;
		_log.setPriority(Priority.DEBUG);
		_k8055.getUsbDevice().addUsbDeviceListener(this);

		String[] clazzes = { Driver.class.getName() };
		Properties properties = new Properties();
		properties.put(DRIVER_ID, this.getClass().getName()
				+ "."
				+ UsbUtil.toHexString(_k8055.getUsbDevice()
						.getUsbDeviceDescriptor().idProduct()));
		_registration = _context.registerService(clazzes, this, properties);
	}

	protected abstract String[] getSerial();

	// Usb Device Event
	public void dataEventOccurred(UsbDeviceDataEvent event) {
		_log.info("dataEventOccurred: " + event);
	}

	public void errorEventOccurred(UsbDeviceErrorEvent event) {
		_log.info("errorEventOccurred: " + event);
	}

	public void usbDeviceDetached(UsbDeviceEvent event) {
		_log.info("usbDeviceDetached: " + event);

		if (_device != null)
			_device.setDriver(null);

		_log.info("Unregister: " + _registration);
		if (_registration != null) {
			_registration.unregister();
			_registration = null;
		}

		_k8055.getUsbDevice().removeUsbDeviceListener(this);

	}

	// Device Event
	public Object addingService(ServiceReference reference) {
		_device = (AnalogIODevice) _context.getService(reference);
		_log.info("device attached: " + _device);
		_device.setDriver(this);
		return _device;
	}

	public void modifiedService(ServiceReference reference, Object service) {

	}

	public void removedService(ServiceReference reference, Object service) {
		_context.ungetService(reference);
		_device.setDriver(null);
		_log.info("device deattached: " + _device);
		_device = null;
	}

	// Driver Interface
	public String attach(ServiceReference device) throws Exception {
		_tracker = new ServiceTracker(_context, device, this);
		_tracker.open();
		return null;
	}

	public int match(ServiceReference device) throws Exception {

		final String filter = "(&" + "("
				+ org.osgi.framework.Constants.OBJECTCLASS + "="
				+ AnalogIODevice.class.getName() + ")" + "(" + DEVICE_CATEGORY
				+ "=" + AnalogIODevice.class.getName() + ")" + "(|" + "("
				+ DEVICE_SERIAL + "=" + getSerial()[0] + ")" + "("
				+ DEVICE_SERIAL + "=" + getSerial()[1] + ")" + "("
				+ DEVICE_SERIAL + "=" + getSerial()[2] + ")" + "("
				+ DEVICE_SERIAL + "=" + getSerial()[3] + ")" + ")" + ")";

		if (getFilter(filter).match(device))
			return DigitalIODevice.MATCH_MODEL;
		else
			return Device.MATCH_NONE;
	}

	protected Filter getFilter(String filter) {
		if (_filter == null) {
			try {
				_filter = _context.createFilter(filter);
				_log.info("Filter: " + _filter.toString());
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
		return _filter;
	}

}
