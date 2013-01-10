package org.angolight.bo.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.angolight.bo.Bo;
import org.angolight.bo.impl.BoConfig;
import org.angolight.bo.impl.BoImpl;
import org.angolight.kinetic.Kinetic;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String COMMAND_GROUP = "bo.manager";

	private Bo _peer;

	CommandGroupImpl(ComponentContext context, Bo peer, ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for the BO Manager");
		_peer = peer;
		_config = config;
	}

	// Speed
	// Acceleration
	public final static String USAGE_SETSPEEDACC = "<speed><acc>";

	public final static String[] HELP_SETSPEEDACC = new String[] { "Set speed and acceleration", };

	public int cmdSetspeedacc(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		try {
			double speed = Double.parseDouble(((String) opts.get("speed"))
					.trim());
			double acc = Double.parseDouble(((String) opts.get("acc")).trim());

			Kinetic kinetic = new Kinetic();
			kinetic.setSpeed(speed);
			kinetic.setAcceleration(acc);

			((BoImpl) _peer).onKinetic(kinetic);

			out.println("New speed  : " + speed + " acceleration : " + acc);
		} catch (Exception e) {
			out.println("Erreur de paramétrage de la vitesse : "
					+ ((String) opts.get("speed")).trim());
			out.println(e);
		}

		return 0;
	}

	// VMin
	public final static String USAGE_SETVMIN = "<vminup><vmindown>";

	public final static String[] HELP_SETVMIN = new String[] { "Set VMin", };

	public int cmdSetvmin(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String sVMinUp = ((String) opts.get("vminup")).trim();
		Double vminup = Double.valueOf(sVMinUp);
		String sVMinDown = ((String) opts.get("vmindown")).trim();
		Double vmindown = Double.valueOf(sVMinDown);

		if ((vminup != null) && (vmindown != null)) {
			((BoConfig) _config).setVMin(vminup, vmindown);

			out.println("New VMins : up["
					+ ((BoConfig) _config).getVMinUp() + "] down["
					+ ((BoConfig) _config).getVMinDown() + "]");
		} else
			out.println("Erreur de saisie de VMin !");

		return 0;
	}

	public final static String USAGE_SHOWVMIN = "";

	public final static String[] HELP_SHOWVMIN = new String[] { "Show VMin", };

	public int cmdShowvmin(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current VMins : up["
				+ ((BoConfig) _config).getVMinUp() + "] down["
				+ ((BoConfig) _config).getVMinDown() + "]");
		return 0;
	}

	// VMax
	public final static String USAGE_SETVMAX = "<vmaxup><vmaxdown>";

	public final static String[] HELP_SETVMAX = new String[] { "Set VMax", };

	public int cmdSetvmax(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String sVMaxUp = ((String) opts.get("vmaxup")).trim();
		Double vmaxup = Double.valueOf(sVMaxUp);
		String sVMaxDown = ((String) opts.get("vmaxdown")).trim();
		Double vmaxdown = Double.valueOf(sVMaxDown);

		if ((vmaxup != null) && (vmaxdown != null)) {
			((BoConfig) _config).setVMax(vmaxup, vmaxdown);

			out.println("New VMaxs : up["
					+ ((BoConfig) _config).getVMaxUp() + "] down["
					+ ((BoConfig) _config).getVMaxDown() + "]");
		} else
			out.println("Erreur de saisie de VMax !");

		return 0;
	}

	public final static String USAGE_SHOWVMAX = "";

	public final static String[] HELP_SHOWVMAX = new String[] { "Show VMax", };

	public int cmdShowvmax(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current VMaxs : up["
				+ ((BoConfig) _config).getVMaxUp() + "] down["
				+ ((BoConfig) _config).getVMaxDown() + "]");
		return 0;
	}

	// TriggerPercent
	public final static String USAGE_SETTRIGPERCENT = "<trigpercent>";

	public final static String[] HELP_SETTRIGPERCENT = new String[] { "Set Trigger Percent", };

	public int cmdSettrigpercent(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String sTriggerPercent = ((String) opts.get("trigpercent")).trim();
		Double trigpercent = Double.valueOf(sTriggerPercent);

		if (trigpercent != null) {
			((BoConfig) _config).setTriggerPercent(trigpercent);

			out.println("New Trigger Percent : "
					+ ((BoConfig) _config).getTriggerPercent());
		} else
			out.println("Erreur de saisie de Trigger Percent !");

		return 0;
	}

	public final static String USAGE_SHOWTRIGPERCENT = "";

	public final static String[] HELP_SHOWTRIGPERCENT = new String[] { "Show Trigger Percent", };

	public int cmdShowtrigpercent(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Trigger Percent : "
				+ ((BoConfig) _config).getTriggerPercent());
		return 0;
	}

	public final static String USAGE_SHOWCURVESVERSION = "";

	public final static String[] HELP_SHOWCURVESVERSION = new String[] { "Show Curves version", };

	public int cmdShowcurvesversion(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Curves version : "
				+ ((BoImpl) _peer).getCurvesVersion());
		return 0;
	}

	public final static String USAGE_SETCURVESFILENAME = "<filename>";

	public final static String[] HELP_SETCURVESFILENAME = new String[] { "Set Curves filename", };

	public int cmdSetcurvesfilename(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String filename = ((String) opts.get("filename")).trim();

		if (filename != null) {
			try {
				((BoConfig) _config).setCurvesFileName(filename);

				out.println("New curves filename : "
						+ ((BoConfig) _config).getCurvesFileName());

				_config.updateConfig(true);
			} catch (Exception e) {
				out.println("Erreur : " + e.getMessage());
			}
		} else
			out.println("Erreur de saisie du nom de fichier des courbes !");

		return 0;
	}

}
