package org.avm.hmi.swt.phony.bundle;

import org.avm.elementary.common.AbstractCommandGroup;
import org.avm.hmi.swt.phony.Phony;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "swt.phony";
	private Phony _peer;

	public CommandGroupImpl(ComponentContext context, Phony peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for IHM phony.");
		_peer = peer;
	}




}
