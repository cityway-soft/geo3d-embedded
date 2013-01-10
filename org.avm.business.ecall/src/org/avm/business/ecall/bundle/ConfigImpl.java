package org.avm.business.ecall.bundle;

import org.avm.business.ecall.EcallServiceConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl  extends AbstractConfig implements EcallServiceConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
		// TODO Auto-generated constructor stub
	}

}
