package org.avm.device.afficheur.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.afficheur.Afficheur;
import org.avm.device.afficheur.AfficheurDevice;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.elementary.common.AbstractDeviceCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractDeviceCommandGroup {

	private AfficheurDevice _peer;

	public CommandGroupImpl(ComponentContext context, AfficheurDevice peer,
			ConfigImpl config) {
		super(context, config, "afficheur");
		_peer = (AfficheurDevice) peer;
	}

	// print
	public final static String USAGE_PRINT = "<message>";

	public final static String[] HELP_PRINT = new String[] { "Print message", };

	public int cmdPrint(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String message = ((String) opts.get("message")).trim();

		Afficheur afficheur = (Afficheur) _context.locateService("afficheur");
		if (afficheur != null) {
			afficheur.print(message);
		}
		return 0;
	}

	// status Afficheur
	public final static String USAGE_STATUS = "";

	public final static String[] HELP_STATUS = new String[] { "Verifie qu'un Afficheur est bien connecte", };

	public int cmdStatus(Dictionary opts, Reader in, PrintWriter out,
			Session session) {

		try {
			int status = ((AfficheurDevice) _peer).checkStatus();
			switch (status) {
			case AfficheurProtocol.STATUS_NOT_AVAILABLE:
				out.println("Pas de statut disponible.");
				break;
			case AfficheurProtocol.STATUS_OK:
				out.println("OK");
				break;
			case AfficheurProtocol.STATUS_ERR_NO_RESPONSE:
				out.println("Err: Pas de reponse");
				break;
			case AfficheurProtocol.STATUS_ERR_INCORRECT_REPONSE:
				out.println("Err: Reponse, mais incorrecte");
				break;
			case AfficheurProtocol.STATUS_ERR_CANNOT_OPEN_PORT:
				out.println("Err: Aucune Afficheur");
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
