package org.avm.device.girouette.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.girouette.Girouette;
import org.avm.device.girouette.GirouetteDevice;
import org.avm.device.girouette.GirouetteProtocol;
import org.avm.elementary.common.AbstractDeviceCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractDeviceCommandGroup {
	public static final String COMMAND_GROUP = "girouette";

	private GirouetteDevice _peer;

	public CommandGroupImpl(ComponentContext context, GirouetteDevice peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP);
		_peer = (GirouetteDevice) peer;
	}

	// code girouette
	public final static String USAGE_DESTINATION = "<code>";

	public final static String[] HELP_DESTINATION = new String[] { "Envoi un code girouette (destination)", };

	public int cmdDestination(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String code = ((String) opts.get("code")).trim();

		Girouette girouette = (Girouette) _context.locateService("girouette");
		if (girouette != null) {
			girouette.destination(code);
		}
		return 0;
	}

	// status girouette
	public final static String USAGE_STATUS = "";

	public final static String[] HELP_STATUS = new String[] { "Verifie qu'une girouette est bien connectee", };

	public int cmdStatus(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		try {
			int status = ((GirouetteDevice) _peer).checkStatus();
			switch (status) {
			case GirouetteProtocol.STATUS_NOT_AVAILABLE:
				out.println("Pas de statut disponible.");
				break;
			case GirouetteProtocol.STATUS_OK:
				out.println("OK");
				break;
			case GirouetteProtocol.STATUS_ERR_NO_RESPONSE:
				out.println("Err: Pas de reponse");
				break;
			case GirouetteProtocol.STATUS_ERR_INCORRECT_REPONSE:
				out.println("Err: Reponse, mais incorrecte");
				break;
			case GirouetteProtocol.STATUS_ERR_CANNOT_OPEN_PORT:
				out.println("Err: Aucune girouette");
				break;
			default:
				out.println("Err: inconnue !??");
				break;
			}

		} catch (Exception e) {
			out.println("Error:" + e.getMessage());
			e.printStackTrace();
		}

		return 0;
	}
}
