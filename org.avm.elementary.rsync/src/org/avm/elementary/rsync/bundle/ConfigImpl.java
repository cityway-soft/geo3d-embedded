package org.avm.elementary.rsync.bundle;

import java.util.Dictionary;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.rsync.impl.RSyncConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements RSyncConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

}
