package org.avm.elementary.media.test.bundle;

import java.util.Dictionary;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.media.test.MediaTestConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements MediaTestConfig {

		
	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		//result.put(SMSCENTER_TAG, DEFAULT_SMSCENTER);
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}




}
