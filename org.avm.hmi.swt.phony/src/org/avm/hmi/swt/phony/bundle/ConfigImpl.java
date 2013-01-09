package org.avm.hmi.swt.phony.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.hmi.swt.phony.PhonyConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements PhonyConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}


}
