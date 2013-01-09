package org.avm.business.comptage.bundle;

import org.avm.business.comptage.Comptage;
import org.avm.elementary.common.AbstractCommandGroup;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String COMMAND_GROUP = "comptage-manager";

	private Comptage _peer;

	CommandGroupImpl(ComponentContext context, Comptage peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for comptage manager.");
		_peer = peer;
	}
}
