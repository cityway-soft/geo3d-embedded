package org.avm.device.knet.gps.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.gps.Gps;
import org.avm.device.knet.gps.GpsConfig;
import org.avm.device.knet.gps.GpsImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.position.Position;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String LOGGER = "org.avm.device.knet.gps";
	public static final String COMMAND_GROUP = "knet.gps";
	private ConfigImpl _config;
	private Gps _peer;

	protected CommandGroupImpl(ComponentContext context, Gps peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Commandes de configuration pour GPS");
		_peer = peer;
		_config = (ConfigImpl) config;
	}

	// Delay
	public final static String USAGE_SETDELAY = "<delay>";
	public final static String[] HELP_SETDELAY = new String[] { "Set delay (s)", };

	public int cmdSetdelay(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Double delay = new Double(((String) opts.get("delay")).trim());
		((GpsConfig) _config).setDelay(delay);
		_config.updateConfig();

		out.println("Current delay : " + ((GpsConfig) _config).getDelay());
		return 0;
	}

	public final static String USAGE_SHOWDELAY = "";
	public final static String[] HELP_SHOWDELAY = new String[] { "Show delay (s)", };

	public int cmdShowdelay(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current delay : " + ((GpsConfig) _config).getDelay());
		return 0;
	}

	// Correct
	public final static String USAGE_SETCORRECT = "<correct>";
	public final static String[] HELP_SETCORRECT = new String[] { "Set correct true/false", };

	public int cmdSetcorrect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Boolean correct = new Boolean(((String) opts.get("correct")).trim());
		((GpsConfig) _config).setCorrect(correct);
		_config.updateConfig();

		out.println("Current correct  : " + _config.getCorrect());
		return 0;
	}

	public final static String USAGE_SHOWCORRECT = "";
	public final static String[] HELP_SHOWCORRECT = new String[] { "Show current  correct true/false", };

	public int cmdShowcorrect(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Current correct  : " + _config.getCorrect());
		return 0;
	}

	/*
	 * Methode supprimer ou mettre les log de position GPS (1 toutes les
	 * secondes)
	 */
	public final static String USAGE_SETDEBUGON = "<level>";
	public final static String[] HELP_SETDEBUGON = new String[] { "1 : Pour log tres verbeux" };

	public int cmdSetdebugon(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String level = ((String) opts.get("level")).trim();
		if (level.equals("1")) {
			((GpsImpl) _peer).setDebugOn();
			out.println("Log verbeux");
		} else {
			((GpsImpl) _peer).unsetDebugOn();
			out.println("Log moins verbeux");
		}
		return 0;
	}

	public final static String USAGE_GETCURRENTPOS = "";
	public final static String[] HELP_GETCURRENTPOS = new String[] { "Retourne la position courante." };

	public int cmdGetcurrentpos(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		Position p = _peer.getCurrentPosition();
		double x = p.getLongitude().getValue();
		double y = p.getLatitude().getValue();
		x = x * 180 / Math.PI;
		y = y * 180 / Math.PI;
		out.println("Position courante : Longitude=" + x + "�, Latitude=" + y
				+ "�");
		return 0;
	}

}
