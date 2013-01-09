package org.avm.device.knet.girouette;

import org.apache.log4j.Logger;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.KnetException;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsMsg;

public class KnetDevice4Girouette extends KnetDevice {
	private Logger _log = null;

	public KnetDevice4Girouette() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	public void start() {
		_log.debug("start GIROUETTE [" + KnetAgent.GIROUETTE_APP + "]");
		try {
			open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
					KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
					KnetAgent.GIROUETTE_APP, KnetAgent.GIROUETTE_SERVICES,
					KnetAgent.LOCAL_NODE);
			startListen();
		} catch (KnetException e) {
			_log.error("Erreur � la connexion � l'agent pour GIROUETTE", e);
			return;
		}
	}

	public void stop() {
		_log.debug("Stop GIROUETTE [" + KnetAgent.GIROUETTE_APP + "]");
		try {
			close();
		} catch (KnetException e) {
			_log.error("Erreur a la commande stop pour GIROUETTE", e);
		}
	}

	public void postCodeGirouette(String strCode) {
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsMsg.ROLE);
		KmsMarshaller cmde = (KmsMarshaller) ((org.avm.device.knet.model.KmsMsg.DefaultKmsFactory) kf)
				.create(KnetAgent.GIROUETTE_APP, KnetAgent.GIROUETTE_SERVICES,
						KnetAgent.LOCAL_NODE, strCode);
		try {
			_log.debug("Envoie de :" + cmde);
			post(cmde);
		} catch (KnetException e) {
			_log.error("Erreur KNET a la commande pour GIROUETTE", e);
		} catch (Exception e) {
			_log.error("Erreur generale a la commande pour GIROUETTE", e);
		}
	}

	public void receive(Kms kms) {
		_log
				.debug("GIROUETTE [" + KnetAgent.GIROUETTE_APP + "] receive "
						+ kms);
		if (kms == null)
			return;
	}

	public int getKnetApp() {
		return KnetAgent.GIROUETTE_APP;
	}

	public String getKnetAppAsString() {
		return "GIROUETTE_APP";
	}

}
