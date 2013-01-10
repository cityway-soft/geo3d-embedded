package org.avm.device.generic.io.k8055;

import java.util.List;
import java.util.Properties;

import javax.usb.UsbConst;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbPipe;
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;
import javax.usb.util.UsbUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.io.AnalogIODevice;
import org.avm.device.io.AnalogIODriver;
import org.avm.device.io.CounterDevice;
import org.avm.device.io.CounterDriver;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;
import org.avm.device.io.IOCardInfo;
import org.avm.elementary.common.PropertyChangeEvent;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;

public class K8055 implements K8055UsbDevice {

	private Logger _log = Logger.getInstance(this.getClass());

	private UsbDevice _usbDevice;

	private UsbInterface _usbInterface;

	private UsbPipe _usbPipeIn;

	private UsbPipe _usbPipeOut;

	private byte[] _in;

	private byte[] _out;

	private InputTask _reader;

	private PropertyChangeSupport _listeners;

	private boolean _closed = true;

	public K8055(UsbDevice usbDevice) {
		_usbDevice = usbDevice;
		_log.setPriority(Priority.DEBUG);
		_listeners = new PropertyChangeSupport(this);
		open();
	}

	public UsbDevice getUsbDevice() {
		return _usbDevice;
	}

	public synchronized void open() {
		_usbInterface = _usbDevice.getActiveUsbConfiguration().getUsbInterface(
				(byte) 0);

		// interface
		try {
			_usbInterface.claim(new UsbInterfacePolicy() {
				public boolean forceClaim(UsbInterface usbInterface) {
					return true;
				}
			});
		} catch (Exception e) {
			close();
			throw new RuntimeException("Error : " + e.getMessage());
		}

		// endpoint
		List usbEndpoints = _usbInterface.getUsbEndpoints();

		UsbEndpoint usbEndpoint = null;

		UsbEndpoint usbEndpointDirectionIn = null;
		UsbEndpoint usbEndpointDirectionOut = null;

		for (int i = 0; i < usbEndpoints.size(); i++) {
			usbEndpoint = (UsbEndpoint) usbEndpoints.get(i);
			if (usbEndpoint.getType() != UsbConst.ENDPOINT_TYPE_INTERRUPT)
				continue;
			switch (usbEndpoint.getDirection()) {
			case UsbConst.ENDPOINT_DIRECTION_IN:
				usbEndpointDirectionIn = usbEndpoint;
				break;
			case UsbConst.ENDPOINT_DIRECTION_OUT:
				usbEndpointDirectionOut = usbEndpoint;
				break;
			}
		}

		if (usbEndpointDirectionIn == null || usbEndpointDirectionOut == null) {
			close();
			throw new RuntimeException(
					"This interface does not have the required interrupt endpoint.");
		}

		// pipes
		_usbPipeIn = usbEndpointDirectionIn.getUsbPipe();
		_usbPipeOut = usbEndpointDirectionOut.getUsbPipe();

		try {
			_usbPipeIn.open();
			_in = new byte[UsbUtil.unsignedInt(_usbPipeIn.getUsbEndpoint()
					.getUsbEndpointDescriptor().wMaxPacketSize())];

			_usbPipeOut.open();
			_out = new byte[UsbUtil.unsignedInt(_usbPipeOut.getUsbEndpoint()
					.getUsbEndpointDescriptor().wMaxPacketSize())];
		} catch (UsbException e) {
			close();
			throw new RuntimeException("Error : " + e.getMessage());
		}

		_reader = new InputTask(_usbPipeIn, _in);
		new Thread(_reader).start();

		_closed = false;

	}

	public synchronized void close() {

		_closed = true;

		if (_reader != null) {
			_reader.stop();
			_reader = null;
		}

		try {
			if (_usbPipeIn != null && _usbPipeIn.isOpen()) {
				_usbPipeIn.close();
				_usbPipeIn = null;
			}
		} catch (UsbException e) {
			_usbPipeIn = null;
			_log.error(e.getMessage());
		}

		try {
			if (_usbPipeOut != null && _usbPipeOut.isOpen()) {
				_usbPipeOut.close();
				_usbPipeOut = null;
			}
		} catch (UsbException e) {
			_usbPipeOut = null;
			_log.error(e.getMessage());
		}

		try {
			if (_usbInterface != null) {
				_usbInterface.release();
				_usbInterface = null;
			}
		} catch (UsbException e) {
			_log.error(e.getMessage());
		}
	}

	public synchronized byte readAnalogChannel(int index) {
		byte result = 0;

		if (_closed)
			throw new RuntimeException("Device closed.");

		switch (index) {

		case 0:
			result = _in[2];
			break;
		case 1:
			result = _in[3];
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
		return result;
	}

	public synchronized void writeAnalogChannel(int index, byte value) {

		if (_closed)
			throw new RuntimeException("Device closed.");

		switch (index) {

		case 0:
			_out[2] = value;
			break;
		case 1:
			_out[3] = value;
			break;
		default:
			throw new IndexOutOfBoundsException();
		}

		write();

	}

	public synchronized boolean readDigitalChannel(int index) {

		final int[] indexes = { 4, 5, 0, 6, 7 };

		if (_closed)
			throw new RuntimeException("Device closed.");

		if (index >= indexes.length)
			throw new IndexOutOfBoundsException();

		byte value = _in[0];

		return (((value >> indexes[index]) & 0x01) == 1);

	}

	public synchronized void writeDigitalChannel(int index, boolean value) {

		if (_closed)
			throw new RuntimeException("Device closed.");

		if (index > 7 || index < 0)
			throw new IndexOutOfBoundsException();

		int offset = 1;

		byte mask = (byte) (1 << index);
		if (value) {
			_out[offset] = (byte) ((_out[offset] | mask) & 0xff);
		} else {
			_out[offset] = (byte) ((_out[offset] & ~mask) & 0xff);
		}

		write();
	}

	public synchronized short readCounter(int index) {
		short result = 0;

		if (_closed)
			throw new RuntimeException("Device closed.");

		switch (index) {

		case 0:
			result = UsbUtil.toShort(_in[5], _in[4]);
			break;
		case 1:
			result = UsbUtil.toShort(_in[7], _in[6]);
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
		return result;
	}

	public synchronized void resetCounter(int index) {
		throw new UnsupportedOperationException();
	}

	private int write() {
		int lenght = 0;
		_out[0] = 0x05;
		try {
			lenght = _usbPipeOut.syncSubmit(_out);
		} catch (UsbException e) {
			throw new RuntimeException(e.getMessage());
		}
		return lenght;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_log.info("[DSU] addPropertyChangeListener !" + listener);
		_listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}

	void firePropertyChange(byte oldValue, byte newValue) {
		final int[] indexes = { 4, 5, 0, 6, 7 };

		for (int i = 0; i < indexes.length; i++) {
			boolean old = ((oldValue >> indexes[i]) & 0x01) == 1;
			boolean value = ((newValue >> indexes[i]) & 0x01) == 1;
			if (old != value) {
				_log.info("[DSU] fireIndexedPropertyChange !");
				_listeners.fireIndexedPropertyChange(null, i, old, value);
			}
		}
	}

	class InputTask implements Runnable {

		private Logger _log = Logger.getInstance(this.getClass());

		public boolean _running = true;

		public UsbPipe _usbPipe = null;

		private byte[] _in;

		public InputTask() {
			super();
			// TODO Auto-generated constructor stub
		}

		public InputTask(UsbPipe pipe, byte[] in) {
			_usbPipe = pipe;
			_in = in;
		}

		public void run() {

			int length = 0;

			_log.info("reader starting ");

			while (_running) {

				try {
					byte tmp = _in[0];
					length = _usbPipe.syncSubmit(_in);
					if (tmp != _in[0])
						firePropertyChange(tmp, _in[0]);
					sleep(100);
				} catch (UsbException e) {
					if (_running) {
						_log.error("Unable to submit data buffer to device : "
								+ e.getMessage());
						break;
					}
				}

				if (_running) {
					StringBuffer sb = new StringBuffer();
					sb
							.append("Got " + length
									+ " bytes of data from device : ");
					for (int i = 0; i < length; i++)
						sb.append(" 0x" + UsbUtil.toHexString(_in[i]));
					// _log.info(sb.toString());
				}

			}
			_log.info("reader stopped ");
		}

		public void stop() {
			_running = false;
			try {
				_usbPipe.abortAllSubmissions();
			} catch (Exception e) {
			}

		}

		private void sleep(long millis) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				throw new RuntimeException("Error : " + e.getMessage());
			}
		}
	}

}
