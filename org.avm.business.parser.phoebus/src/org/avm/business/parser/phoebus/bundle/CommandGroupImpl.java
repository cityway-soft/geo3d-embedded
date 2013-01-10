package org.avm.business.parser.phoebus.bundle;

import org.avm.business.parser.phoebus.ParserImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "protocol_phoebus";

	private ParserImpl _peer;

	CommandGroupImpl(ComponentContext context, ParserImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the protocol phoebus.");
		_peer = peer;
	}

}
