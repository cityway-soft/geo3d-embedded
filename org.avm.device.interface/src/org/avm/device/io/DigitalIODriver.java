package org.avm.device.io;

import org.avm.elementary.common.PropertyChangeListener;

public interface DigitalIODriver {

	public int getCapability();

	public boolean getValue(int index);

	public void setValue(int index, boolean value);

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);

}
