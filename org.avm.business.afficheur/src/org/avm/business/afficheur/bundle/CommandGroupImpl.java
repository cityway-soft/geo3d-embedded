package org.avm.business.afficheur.bundle;

import org.avm.business.afficheur.Afficheur;
import org.avm.elementary.common.AbstractCommandGroup;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String COMMAND_GROUP = "afficheur-manager";

	private Afficheur _peer;

	CommandGroupImpl(ComponentContext context, Afficheur peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for afficheur manager.");
		_peer = peer;
	}
}
