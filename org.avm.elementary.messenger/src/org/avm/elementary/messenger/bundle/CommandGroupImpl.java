package org.avm.elementary.messenger.bundle;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.messenger.impl.MessengerImpl;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "messenger";

	private MessengerImpl _peer;

	CommandGroupImpl(ComponentContext context, MessengerImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the gps.");
		_peer = peer;
	}

}
