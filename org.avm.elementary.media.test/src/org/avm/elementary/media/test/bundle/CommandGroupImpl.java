package org.avm.elementary.media.test.bundle;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.elementary.media.test.MediaTest;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "test";

	private MediaTest _peer;

	public CommandGroupImpl(ComponentContext context, MediaTest peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP, "Configuration commands for sms.");
		_peer = peer;
	}


}
