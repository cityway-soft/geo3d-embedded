package org.angolight.recorder.avm.bundle;

import org.angolight.recorder.Recorder;
import org.angolight.recorder.avm.RecorderService;
import org.avm.elementary.common.AbstractCommandGroup;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "avm.recorder";

	private RecorderService _peer;

	CommandGroupImpl(ComponentContext context, Recorder peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for recorder.");
		_peer = (RecorderService) peer;

	}

}
