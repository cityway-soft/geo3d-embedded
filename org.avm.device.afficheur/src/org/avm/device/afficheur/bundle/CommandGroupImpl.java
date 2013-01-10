package org.avm.device.afficheur.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.afficheur.Afficheur;
import org.avm.device.afficheur.AfficheurDevice;
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

}
