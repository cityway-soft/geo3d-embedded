package org.avm.business.billettique.atoumod.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.avm.business.billettique.atoumod.Billettique;
import org.avm.business.billettique.atoumod.BillettiqueImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String COMMAND_GROUP = "billettique";

	private ConfigImpl _config;

	private Billettique _peer;

	CommandGroupImpl(ComponentContext context, Billettique peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for 'billettique'.");
		_peer = peer;
		_config = config;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		return 0;
	}



}
