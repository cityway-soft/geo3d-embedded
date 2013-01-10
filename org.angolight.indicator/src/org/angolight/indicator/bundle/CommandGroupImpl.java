package org.angolight.indicator.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.angolight.indicator.Indicator;
import org.angolight.indicator.Measure;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class CommandGroupImpl extends AbstractCommandGroup {

	public static final String COMMAND_GROUP = "indicator";

	private IndicatorService _peer;

	CommandGroupImpl(ComponentContext context, Indicator peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for indicator.");
		_peer = (IndicatorService) peer;

	}

	// can service pid
	public final static String USAGE_SETCANSERVICEPID = "<pid>";

	public final static String[] HELP_SETCANSERVICEPID = new String[] { "Set can service pid", };

	public int cmdSetcanservicepid(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String pid = (String) opts.get("pid");
		((IndicatorConfig) _config).setCanServicePid(pid);
		_config.updateConfig();

		out.println("Current can service pid : "
				+ ((IndicatorConfig) _config).getCanServicePid());
		return 0;
	}

	public final static String USAGE_SHOWCANSERVICEPID = "";

	public final static String[] HELP_SHOWCANSERVICEPID = new String[] { "Show current can service pid", };

	public int cmdShowcanservicepid(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current can service pid : "
				+ ((IndicatorConfig) _config).getCanServicePid());
		return 0;
	}

	// filename
	public final static String USAGE_SETCURVESFILENAME = "<path>";

	public final static String[] HELP_SETCURVESFILENAME = new String[] { "Set curves filename", };

	public int cmdSetcurvesfilename(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String filename = ((String) opts.get("path")).trim();
		((IndicatorConfig) _config).setFilename(filename);
		_config.updateConfig();

		out.println("Current curves filename : "
				+ ((IndicatorConfig) _config).getFilename());
		return 0;
	}

	public final static String USAGE_SHOWCURVESFILENAME = "";

	public final static String[] HELP_SHOWCURVESFILENAME = new String[] { "Show current curves filename", };

	public int cmdShowcurvesfilename(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Current curves filename : "
				+ ((IndicatorConfig) _config).getFilename());
		return 0;
	}

	// Add
	public final static String USAGE_ADD = "-n #name# -v #value#";

	public final static String[] HELP_ADD = new String[] { "Add property", };

	public int cmdAdd(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String key = ((String) opts.get("-n")).trim();
		String value = ((String) opts.get("-v")).trim();
		Properties p = ((IndicatorConfig) _config).getProperties();
		p.put(key, value);
		((IndicatorConfig) _config).setProperties(p);
		return 0;
	}

	// Remove
	public final static String USAGE_REMOVE = "-n #name#";

	public final static String[] HELP_REMOVE = new String[] { "Remove property", };

	public int cmdRemove(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String key = ((String) opts.get("-n")).trim();
		Properties p = ((IndicatorConfig) _config).getProperties();
		p.remove(key);
		((IndicatorConfig) _config).setProperties(p);
		return 0;
	}

	// List
	public final static String USAGE_LIST = "";

	public final static String[] HELP_LIST = new String[] { "List all variables", };

	public int cmdList(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Properties p = ((IndicatorConfig) _config).getProperties();
		out.println(p);
		return 0;
	}

	// SIMU STATE
	public final static String USAGE_SIMULATESTATE = "<statename> <statevalue>";

	public final static String[] HELP_SIMULATESTATE = new String[] { "Simulate states : longstop-engineon-state , freewheel-state ", };

	public int cmdSimulatestate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String statename = ((String) opts.get("statename")).trim();
		String statevalue = ((String) opts.get("statevalue")).trim();

		((IndicatorService) _peer).publish(new State(Integer
				.parseInt(statevalue), statename));

		return 0;
	}

	// EVALUATE
	public final static String USAGE_EVALUATE = "";

	public final static String[] HELP_EVALUATE = new String[] { "Evaluate maps ", };

	public int cmdEvaluate(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Map map = ((IndicatorService) _peer).evaluate();

		for (Iterator iterator = map.values().iterator(); iterator.hasNext();) {
			Measure measure = (Measure) iterator.next();
			out.println(measure.getName() + "=" + measure.getValue());
		}

		return 0;
	}

}
