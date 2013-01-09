package org.avm.device.usbmass.bundle;

import java.util.Dictionary;

import org.avm.device.usbmass.UsbMassConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements UsbMassConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();

		return result;
	}

	public void setMountPoint(String mount) {
		_config.put(TAG_MOUNT_POINT, mount);
	}

	public String getMountPoint() {
		return (String)_config.get(TAG_MOUNT_POINT);
	}

	public String getPollFrequency() {
		return  (String)_config.get(TAG_POLL_FREQUENCY);
	}

	public void setPollFrequency(String frequency) {
		_config.put(TAG_POLL_FREQUENCY, frequency);
	}

	
}
