package org.avm.device.knet.sensor.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.knet.sensor.Sensor;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String LOGGER = "org.avm.device.sensor";
	public static final String COMMAND_GROUP = "knet.sensor";
	private ConfigImpl _config;
	private Sensor _peer;

	protected CommandGroupImpl(ComponentContext context, Sensor peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Commandes de configuration pour Sensor");
		_peer = peer;
		_config = (ConfigImpl) config;
	}

	/*
	 * A titre d'exemple : pour voir et modifier l'ID porte avant (voir
	 * Interface Sensor).
	 * 
	 */
	public final static String USAGE_SHOWIDPORTEAV = "";
	public final static String[] HELP_SHOWIDPORTEAV = new String[] { "Presente l'ID pour la porte avant" };

	public int cmdShowidporteav(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Id de la porte Avant : " + _config.getIdPorteAv());
		return 0;
	}

	/*
	 * La valeur entre < et > doit �tre la m�me que l'argument de opts.get(...)
	 */
	public final static String USAGE_SETIDPORTEAV = "<idAv>";
	public final static String[] HELP_SETIDPORTEAV = new String[] { "Modifie l'ID pour la porte avant" };

	public int cmdSetidporteav(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String id = ((String) opts.get("idAv")).trim();
		_config.setIdPorteAv(id);
		_config.updateConfig();
		out.println("Modification de l'Id de la porte Avant : "
				+ _config.getIdPorteAv());
		return 0;
	}

}
