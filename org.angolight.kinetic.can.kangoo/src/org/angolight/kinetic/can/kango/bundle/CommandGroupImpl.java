package org.angolight.kinetic.can.kango.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.angolight.kinetic.can.kangoo.KineticConfig;
import org.angolight.kinetic.can.kangoo.KineticServiceImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "kinetic";

	private KineticServiceImpl _peer;

	CommandGroupImpl(ComponentContext context, KineticServiceImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for kinetic.");
		_peer = (KineticServiceImpl) peer;

	}

	// can service pid
	public final static String USAGE_SETCANSERVICEPID = "<pid>";

	public final static String[] HELP_SETCANSERVICEPID = new String[] { "Set can service pid", };

	public int cmdSetcanservicepid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String pid = (String) opts.get("pid");
		((KineticConfig) _config).setCanServicePid(pid);
		_config.updateConfig();

		out.println("Current can service pid : "
				+ ((KineticConfig) _config).getCanServicePid());
		return 0;
	}

	public final static String USAGE_SHOWCANSERVICEPID = "";

	public final static String[] HELP_SHOWCANSERVICEPID = new String[] { "Show current can service pid", };

	public int cmdShowcanservicepid(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current can service pid : "
				+ ((KineticConfig) _config).getCanServicePid());
		return 0;
	}

	// acquisition period
	public final static String USAGE_SETACQUISITIONPERIOD = "<period>";

	public final static String[] HELP_SETACQUISITIONPERIOD = new String[] { "Set acquisition period", };

	public int cmdSetacquisitionperiod(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double period = Double
				.parseDouble(((String) opts.get("period")).trim());
		((KineticConfig) _config).setAcquisitionPeriod(period);
		_config.updateConfig();

		out.println("Current acquisition period : "
				+ ((KineticConfig) _config).getAcquisitionPeriod());
		return 0;
	}

	public final static String USAGE_SHOWACQUISITIONPERIOD = "";

	public final static String[] HELP_SHOWACQUISITIONPERIOD = new String[] { "Show current acquisition period", };

	public int cmdShowacquisitionperiod(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current acquisition period : "
				+ ((KineticConfig) _config).getAcquisitionPeriod());
		return 0;
	}

	// notification period
	public final static String USAGE_SETNOTIFICATIONPERIOD = "<period>";

	public final static String[] HELP_SETNOTIFICATIONPERIOD = new String[] { "Set notification period", };

	public int cmdSetnotificationperiod(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		double period = Double
				.parseDouble(((String) opts.get("period")).trim());
		((KineticConfig) _config).setNotificationPeriod(period);
		_config.updateConfig();

		out.println("Current notification period : "
				+ ((KineticConfig) _config).getNotificationPeriod());
		return 0;
	}

	public final static String USAGE_SHOWNOTIFICATIONPERIOD = "";

	public final static String[] HELP_SHOWNOTIFICATIONPERIOD = new String[] { "Show current notification period", };

	public int cmdShownotificationperiod(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current notification period : "
				+ ((KineticConfig) _config).getNotificationPeriod());
		return 0;
	}

	// median-filter-length
	public final static String USAGE_SETMEDIANFILTERLENGTH = "<length>";

	public final static String[] HELP_SETMEDIANFILTERLENGTH = new String[] { "Set minimum speed down", };

	public int cmdSetmedianfilterlength(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		int length = Integer.parseInt(((String) opts.get("length")).trim());
		((KineticConfig) _config).setMedianFilterLength(length);
		_config.updateConfig();

		out.println("Current minimum speed down : "
				+ ((KineticConfig) _config).getMedianFilterLength());
		return 0;
	}

	public final static String USAGE_SHOWMEDIANFILTERLENGTH = "";

	public final static String[] HELP_SHOWMEDIANFILTERLENGTH = new String[] { "Show current minimum speed down", };

	public int cmdShowmedianfilterlength(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current minimum speed down : "
				+ ((KineticConfig) _config).getMedianFilterLength());
		return 0;
	}

}
