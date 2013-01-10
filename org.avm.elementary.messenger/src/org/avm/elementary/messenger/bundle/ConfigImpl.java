package org.avm.elementary.messenger.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.messenger.impl.MessengerConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements MessengerConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

}
