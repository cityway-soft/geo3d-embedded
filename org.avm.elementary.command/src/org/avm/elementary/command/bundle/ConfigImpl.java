package org.avm.elementary.command.bundle;

import java.util.Dictionary;

import org.avm.elementary.command.impl.CommandChainConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements CommandChainConfig {

	public static String COMMAND_TAG = "command";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		return result;
	}

	public String getCommand() {
		return (String) _config.get(COMMAND_TAG);
	}

	public void setCommand(String command) {
		_config.put(COMMAND_TAG, command);
	}

}
