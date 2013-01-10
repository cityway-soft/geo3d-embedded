package org.avm.business.tracking.bundle;

import org.avm.business.tracking.Tracking;
import org.avm.elementary.common.AbstractCommandGroup;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	private Tracking _peer;

	CommandGroupImpl(ComponentContext context, Tracking peer, ConfigImpl config) {
		super(context, config, "tracking",
				"Configuration commands for the tracking.");
		_peer = peer;
	}

}
