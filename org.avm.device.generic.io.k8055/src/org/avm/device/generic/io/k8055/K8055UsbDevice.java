package org.avm.device.generic.io.k8055;

import javax.usb.UsbDevice;

import org.avm.elementary.common.PropertyChangeListener;

public interface K8055UsbDevice {

	public UsbDevice getUsbDevice();

	public abstract void open();

	public abstract void close();

	public abstract byte readAnalogChannel(int index);

	public abstract void writeAnalogChannel(int index, byte value);

	public abstract boolean readDigitalChannel(int index);

	public abstract void writeDigitalChannel(int index, boolean value);

	public abstract short readCounter(int index);

	public abstract void resetCounter(int index);

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);

}