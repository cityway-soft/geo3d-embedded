package org.avm.device.generic.io.k8055;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbHostManager;
import javax.usb.UsbServices;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;
import javax.usb.util.UsbUtil;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Driver;

public class Activator implements UsbServicesListener {

	private static final short ID_VENDOR = 0x10cf;

	private static final short[] ID_PRODUCT = { 0x5500, 0x5501, 0x5502, 0x5503 };

	private Logger _log = Logger.getInstance(this.getClass());

	private ComponentContext _context;

	private HashMap _k8055s = new HashMap();

	private UsbServices _services;

	protected void activate(ComponentContext context) {
		_log.info("Components activated");
		_context = context;

		try {
			_services = UsbHostManager.getUsbServices();
		} catch (Exception e) {
			_log.error("Error : " + e.getMessage());
		}
		_services.addUsbServicesListener(this);
	}

	protected void deactivate(ComponentContext context) {
		_log.info("Component deactivated");

		for (Iterator iter = _k8055s.entrySet().iterator(); iter.hasNext();) {
			Short idProduct = (Short) iter.next();
			K8055UsbDevice k8055 = (K8055UsbDevice) _k8055s.remove(idProduct);
			k8055.close();
		}
		_services.removeUsbServicesListener(this);
		_services = null;
	}

	public void usbDeviceAttached(UsbServicesEvent event) {
		UsbDevice usbDevice = event.getUsbDevice();
		UsbDeviceDescriptor usbDeviceDescriptor = usbDevice
				.getUsbDeviceDescriptor();
		short idVendor = usbDeviceDescriptor.idVendor();
		short idProduct = usbDeviceDescriptor.idProduct();
		if (idVendor == ID_VENDOR) {
			for (int i = 0; i < ID_PRODUCT.length; i++) {
				if (idProduct == ID_PRODUCT[i]) {
					System.out.println("Device Attached 0x"
							+ UsbUtil.toHexString(idVendor) + " Product ID 0x"
							+ UsbUtil.toHexString(idProduct));
					if (!_k8055s.containsKey(new Short(idProduct))) {
						K8055UsbDevice k8055 = new K8055(usbDevice);

						new K8055DigitalInputService(_context
								.getBundleContext(), k8055);

						new K8055DigitalOutputService(_context
								.getBundleContext(), k8055);

						new K8055AnalogInputService(
								_context.getBundleContext(), k8055);

						new K8055AnalogOutputService(_context
								.getBundleContext(), k8055);

						new K8055CounterService(_context.getBundleContext(),
								k8055);

						_k8055s.put(new Short(idProduct), k8055);
					}
				}

			}
		}
	}

	public void usbDeviceDetached(UsbServicesEvent event) {
		UsbDevice usbDevice = event.getUsbDevice();
		UsbDeviceDescriptor usbDeviceDescriptor = usbDevice
				.getUsbDeviceDescriptor();
		short idVendor = usbDeviceDescriptor.idVendor();
		short idProduct = usbDeviceDescriptor.idProduct();
		if (idVendor == ID_VENDOR) {
			for (int i = 0; i < ID_PRODUCT.length; i++) {
				if (idProduct == ID_PRODUCT[i]) {
					System.out.println("Device Detached 0x"
							+ UsbUtil.toHexString(idVendor) + " Product ID 0x"
							+ UsbUtil.toHexString(idProduct));
					if (_k8055s.containsKey(new Short(idProduct))) {
						K8055UsbDevice k8055 = (K8055UsbDevice) _k8055s
								.remove(new Short(idProduct));
						k8055.close();
					}
				}

			}
		}

	}

}
