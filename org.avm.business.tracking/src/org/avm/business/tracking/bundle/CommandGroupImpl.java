package org.avm.business.tracking.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.business.tracking.Tracking;
import org.avm.business.tracking.TrackingImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "business.tracking";

	private Tracking _peer;

	CommandGroupImpl(ComponentContext context, Tracking peer, ConfigImpl config) {
		super(context, config, "tracking",
				"Configuration commands for the tracking.");
		_peer = peer;
	}

	// set frequency
	public final static String USAGE_SETFREQUENCY = "<freq>";

	public final static String[] HELP_SETFREQUENCY = new String[] { "permet de definir les frequences d'emission des localisations", };

	public int cmdSetfrequency(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		String sFrequency = ((String) opts.get("freq"));

		if (sFrequency != null) {
			((ConfigImpl) _config).setFrequency(Integer
					.parseInt(sFrequency));
		}


		_config.updateConfig(false);

		out.println(((ConfigImpl) _config).getFrequency());

		return 0;
	}


	
	// localize
	public final static String USAGE_LOCALIZE = "";

	public final static String[] HELP_LOCALIZE = new String[] { "transmet la position courante", };

	public int cmdLocalize(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		((TrackingImpl) _peer).localize();

		return 0;
	}
}
