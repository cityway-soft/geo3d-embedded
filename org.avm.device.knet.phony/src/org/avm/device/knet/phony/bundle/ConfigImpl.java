package org.avm.device.knet.phony.bundle;

import java.util.Dictionary;

import org.avm.device.knet.phony.PhonyConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements PhonyConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		// result.put(TAG_CONTACT_LIST, DEFAULT_CONTACT_LIST);
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public String getContactList() {
		return (String) _config.get(TAG_CONTACT_LIST);
	}

	public void setContactList(String list) {
		_config.put(TAG_CONTACT_LIST, list);
	}

}
