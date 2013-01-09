package org.avm.device.knet.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;

import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetAgentFactory;
import org.avm.device.knet.KnetAgentFactoryImpl;
import org.avm.device.knet.KnetException;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsList;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String LOGGER = "org.avm.device.knet";
	public static final String COMMAND_GROUP = "knet";
	private KnetAgentFactoryImpl _peer;

	protected CommandGroupImpl(ComponentContext context, KnetAgentFactory peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Commandes de configuration pour knet");
		_peer = (KnetAgentFactoryImpl) peer;
	}

	// public final static String USAGE_SHOWLIST = "";
	// public final static String[] HELP_SHOWLIST = new String[] { "Show list of
	// active triggers", };
	// public int cmdShowlist(Dictionary opts, Reader in, PrintWriter out,
	// Session session) {
	// _peer.setNoKnet();
	// return 0;
	// }

	public final static String USAGE_SHOWLIST = "";
	public final static String[] HELP_SHOWLIST = new String[] { "Show list of active triggers", };

	public int cmdShowlist(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		KnetAgent agent = _peer.getKnet();
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsList.ROLE);
		KmsMarshaller cmdeList = (KmsMarshaller) ((org.avm.device.knet.model.KmsList.DefaultKmsFactory) kf)
				.create(KnetAgent.KNET_APP);
		_log.debug("Envoie de :" + cmdeList);
		KmsRoot kms = null;
		try {
			kms = (KmsRoot) agent.send(cmdeList);
		} catch (KnetException e) {
			_log.error("Erreur � la commande pour KNET", e);
			return 0;
		}
		KmsList list = (KmsList) kms.getSubKms(KmsList.ROLE);
		if (list != null) {
			out.print(list);
		}
		return 0;
	}

}
