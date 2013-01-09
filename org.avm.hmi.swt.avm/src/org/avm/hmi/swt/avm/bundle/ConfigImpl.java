package org.avm.hmi.swt.avm.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.hmi.swt.avm.AvmIhmConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements AvmIhmConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public void setPeriode(int value) {
		String v = Integer.toString(value);
		_config.put(PERIODE, v);
	}

	public int getPeriode() {
		String v = (String) _config.get(PERIODE);

		return Integer.parseInt(v);
	}

}
