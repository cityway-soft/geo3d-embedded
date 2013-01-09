package org.avm.device.knet.mmi;

import org.apache.log4j.Logger;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.KnetException;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsMmi;
import org.avm.device.knet.model.KmsMsg;
import org.avm.device.knet.model.KmsRoot;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

/**
 * @author
 * 
 */
public class MmiImpl extends KnetDevice implements Mmi, ConfigurableService,
		ManageableService, ProducerService {
	private Logger _log = null;

	private MmiConfig _config = null;

	private ProducerManager _producer = null;

	private MmiDialogOut _currentDlgOut;

	private Object lock = new Object();

	public MmiImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (MmiConfig) config;
		_log.debug("_config = " + _config);
	}

	public MmiConfig getConfig() {
		return _config;
	}

	public void start() {
		_log.info("start MMI [" + KnetAgent.MMI_APP + "]");
		try {
			// public void open(String host, int port, String login, String
			// passwd, int from, int to, int id)
			open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
					KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
					KnetAgent.MMI_APP, KnetAgent.MMI_SERVICES,
					KnetAgent.LOCAL_NODE);
			startListen();
		} catch (KnetException e) {
			_log.error("Erreur à la connexion à l'agent pour le MMI", e);
			return;
		}
	}

	public void stop() {
		_log.debug("Stop MMI [" + KnetAgent.MMI_APP + "]");
		try {
			close();
		} catch (KnetException e) {
			_log.error("Erreur à la commande stop pour le MMI", e);
		}
	}

	public void submit(MmiDialogIn inDialog) {
		_log.debug("Submitting " + inDialog);
		if (inDialog == null)
			return;

		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsMmi.ROLE);
		KmsMarshaller mmiReq = (KmsMarshaller) ((org.avm.device.knet.model.KmsMmi.DefaultKmsFactory) kf)
				.create(KnetAgent.MMI_APP, KnetAgent.MMI_SERVICES,
						KnetAgent.LOCAL_NODE, inDialog.getMmiSubTrees());
		try {
			_log.debug("Posting " + mmiReq);
			post(mmiReq);
		} catch (KnetException e) {
			_log.error("Erreur au submit dialog", e);
		}
	}

	public void receive(Kms kms) {
		_log.debug("MMI [" + KnetAgent.MMI_APP + "] receive " + kms);
		if (kms == null)
			return;
		Kms k = ((KmsRoot) kms).getSubKms();
		if (k instanceof KmsMsg) {
			// if (kms instanceof KmsMsg){
			KmsMsg msg = (KmsMsg) k;
			MmiDialogOut out = setCurrentDlgOut(new MmiDialogOut(msg));
			if (_producer != null)
				_producer.publish(out);
			else
				_log
						.error("Producer est null ; l'evenement ne peut etre diffuse.");
		}
	}

	public MmiDialogOut setCurrentDlgOut(MmiDialogOut out) {
		_log.debug("TRT de " + out);
		synchronized (lock) {
			_currentDlgOut = out;
			lock.notifyAll();
		}
		return out;
	}

	public MmiDialogOut getDialogOut() {
		synchronized (lock) {
			try {
				if (_currentDlgOut == null) {
					_log.debug("wait for new DialogOut");
					lock.wait();
				}
			} catch (InterruptedException e) {
			}
			MmiDialogOut out = new MmiDialogOut(_currentDlgOut);
			_currentDlgOut = null;
			_log.debug("Mise a disposition de " + out);
			return out;
		}
	}

	public int getKnetApp() {
		return KnetAgent.MMI_APP;
	}

	public String getKnetAppAsString() {
		return "MMI_APP";
	}

}