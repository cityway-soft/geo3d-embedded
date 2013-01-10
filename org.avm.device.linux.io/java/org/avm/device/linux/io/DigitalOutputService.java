package org.avm.device.linux.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;

import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class DigitalOutputService extends ServiceTracker implements
		DigitalIODriver {

	private static final String DEVICE = "/dev/vsi1";

	private static final int CAPABILITY = 8;

	private Logger _log = Logger.getInstance(this.getClass().getName());

	private DigitalIODevice _device;

	private PropertyChangeSupport _listeners;

	private ServiceRegistration _sr;

	private FileOutputStream _out;

	private FileInputStream _in;

	public DigitalOutputService(BundleContext context, ServiceReference device) {
		super(context, device, null);
		_log.setPriority(Priority.DEBUG);
		_listeners = new PropertyChangeSupport(this);
	}

	public int getCapability() {
		return CAPABILITY;
	}

	public boolean getValue(int index) {
		throw new UnsupportedOperationException();
	}

	public void setValue(int index, boolean value) {
		if (index >= CAPABILITY)
			throw new IndexOutOfBoundsException();

		try {
			byte[] buffer = new byte[2];
			if (_in.read(buffer) != 2) {
				return;
			}
			int dout = ((buffer[1] << 8) | buffer[0]) & 0xffff;			
	
			boolean oldValue = get(dout, index);
			if(value){				
				dout = set(dout, index);				
			}else {
				dout = clear(dout, index);				
			}
			
			buffer[0] =(byte) (dout & 0xff);
			buffer[1] = (byte) ((dout >> 8) & 0xff);
					
			_out.write(buffer);

			if (_log.isDebugEnabled()) {
				_log.debug("[DSU] write 0x" + Integer.toHexString(dout));
			}
			
			_listeners.fireIndexedPropertyChange(null, index, oldValue, value);
			
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}

	public Object addingService(ServiceReference reference) {
		_device = (DigitalIODevice) context.getService(reference);
		_device.setDriver(this);
		initialize();
		_sr = context.registerService(this.getClass().getName(), this,
				new Properties());
		return _device;
	}

	public void removedService(ServiceReference reference, Object service) {
		context.ungetService(reference);
		_sr.unregister();
		dispose();
		_device.setDriver(null);

		_device = null;
	}

	private void initialize() {
		try {
			_in = new FileInputStream(DEVICE);
			_out = new FileOutputStream(DEVICE);
		} catch (FileNotFoundException e) {
			_log.error(e);
		}

	}

	private void dispose() {
		try {
			if (_in != null)
				_in.close();
			if (_out != null)
				_out.close();
		} catch (IOException e) {
			_log.error(e);
		}

	}

	private static final int set(int flag, int bit) {
		int mask = 1 << bit;		
		return flag |= mask; 
	}

	private static final int clear(int flag, int bit) {
		int mask = 1 << bit;		
		return flag &= ~mask;
	}

	private static final boolean get(int flag, int bit) {
		int mask = 1 << bit;
		return ((flag & mask) == mask);
	}

}
