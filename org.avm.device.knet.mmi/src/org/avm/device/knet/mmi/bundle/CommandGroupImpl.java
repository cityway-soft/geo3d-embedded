package org.avm.device.knet.mmi.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.knet.mmi.MmiDialogOut;
import org.avm.device.knet.mmi.MmiImpl;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String LOGGER = "org.avm.device.knet.mmi";
	public static final String COMMAND_GROUP = "knet.mmi";
	private ConfigImpl _config;
	private MmiImpl _peer;

	protected CommandGroupImpl(ComponentContext context, MmiImpl peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Commandes de configuration pour MmiManager");
		_peer = peer;
		_config = (ConfigImpl) config;
	}

	/*
	 * <?xml version= "1.0"?><kms from="253" date="2007-09-13 12:16:16" > <msg><rsp
	 * id=attente saisie authentification action=V /> <text name=valeur >007</text>
	 * </msg></kms>
	 */
	// Handover
	public final static String USAGE_NEWDIALOGOUT = "<id><action><valeur>";
	public final static String[] HELP_NEWDIALOGOUT = new String[] { "Simule l'emission d'un dialogue out", };

	public int cmdNewdialogout(Dictionary opts, Reader in, PrintWriter out,
			Session sessionlogin) {
		String stateId = (String) opts.get("id");
		String action = (String) opts.get("action");
		String valeur = (String) opts.get("valeur");
		StringBuffer kms = new StringBuffer();
		_peer.setCurrentDlgOut(new MmiDialogOut(stateId, action, "valeur",
				valeur));
		out.println("");
		return 0;
	}

}
