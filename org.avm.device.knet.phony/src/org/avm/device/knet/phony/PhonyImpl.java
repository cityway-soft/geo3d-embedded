package org.avm.device.knet.phony;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.KnetException;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsCall;
import org.avm.device.knet.model.KmsCalltrig;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsStop;
import org.avm.device.knet.phony.bundle.ConfigImpl;
import org.avm.device.phony.PhoneEvent;
import org.avm.device.phony.PhoneRingEvent;
import org.avm.device.phony.Phony;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

public class PhonyImpl extends KnetDevice implements Phony,
		ConfigurableService, ManageableService, ProducerService {

	public static final String RING = "ring";
	public static final String RELEASED = "released";
	public static final String SETUP = "setup";
	public static int AUTO_ANSWER = -1;

	private Logger _log;

	private ProducerManager _producer;

	private ConfigImpl _config;

	private Hashtable _hashContacts;

	public PhonyImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	public void setProducer(ProducerManager producer) {
		_log.debug("Initialisation du producer " + producer);
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
	}

	public void answer() {
		_log.debug("Answering ");
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsCall.ROLE);
		KmsMarshaller answer = (KmsMarshaller) ((org.avm.device.knet.model.KmsCall.DefaultKmsFactory) kf)
				.create(KnetAgent.PHONY_APP, KnetAgent.LOCAL_NODE, "accept");
		try {
			_log.debug("Posting " + answer);
			post(answer);
		} catch (KnetException e) {
			_log.error("Erreur a Answer", e);
			_producer.publish(new PhoneEvent(PhoneEvent.ERROR));
		}
	}

	public void dial(final String number) {
		_log.debug("dial phone number=" + number);
		_producer.publish(new PhoneEvent(PhoneEvent.DIALING));
	}

	public void call(String name) throws PhoneException {
		String phoneNumber = getPhoneNumber(name);
		if (phoneNumber == null) {
			throw new PhoneException("No phone number for '" + name + "'");
		}
		_log.debug("call " + name);
		dial(phoneNumber);
	}

	public void hangup() {
		_log.debug("Hangup ");
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsCall.ROLE);
		KmsMarshaller hangup = (KmsMarshaller) ((org.avm.device.knet.model.KmsCall.DefaultKmsFactory) kf)
				.create(KnetAgent.PHONY_APP, KnetAgent.LOCAL_NODE, "release");
		try {
			_log.debug("Posting " + hangup);
			post(hangup);
		} catch (KnetException e) {
			_log.error("Erreur a Hangup", e);
			_producer.publish(new PhoneEvent(PhoneEvent.ERROR));
		}
	}

	public void ringing(String source) {
		_log.debug("ringing.");
		_producer.publish(new PhoneRingEvent(source));
	}

	private void initContacts(String list) {
		_hashContacts = new Hashtable();
		if (list == null)
			return;
		StringTokenizer t = new StringTokenizer(list, ";");
		while (t.hasMoreElements()) {
			String str = t.nextToken();
			StringTokenizer t2 = new StringTokenizer(str, "=");
			String name = t2.nextToken();
			String phone = t2.nextToken();
			_hashContacts.put(name, phone);
		}
	}

	public Enumeration getContactList() {
		return _hashContacts.keys();
	}

	public String getPhoneNumber(String name) {
		return (String) _hashContacts.get(name);
	}

	public void receive(Kms kms) {
		_log.debug("PHONY [" + KnetAgent.PHONY_APP + "] receive " + kms);
		if (kms == null)
			return;
		Kms k = ((KmsRoot) kms).getSubKms();
		if (k instanceof KmsCalltrig) {
			KmsCalltrig calltrig = (KmsCalltrig) k;
			String status = calltrig.getStatus();
			if (status.equalsIgnoreCase(RING)) {
				_log.debug("ringing : publish PhoneRingEvent");
				_producer.publish(new PhoneRingEvent(calltrig.getIdent()));
			}
			if (status.equalsIgnoreCase(RELEASED)) {
				_log.debug("released : publish PhoneEvent");
				_producer.publish(new PhoneEvent(PhoneEvent.READY));
			}
			if (status.equalsIgnoreCase(SETUP)) {
				_log.debug("en ligne : publish PhoneEvent");
				_producer.publish(new PhoneEvent(PhoneEvent.ON_LINE));
			}
		}
	}

	public void start() {
		_log.info("start PHONY [" + KnetAgent.PHONY_APP + "]");
		try {
			open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
					KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
					KnetAgent.PHONY_APP, KnetAgent.M2M_APP,
					KnetAgent.LOCAL_NODE);
			startListen();
		} catch (KnetException e) {
			_log.error("Erreur � la connexion � l'agent pour la PHONY", e);
			return;
		}
		String list = _config.getContactList();
		_log.debug("Contact list=" + list);
		initContacts(list);

		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsCalltrig.ROLE);
		KmsMarshaller cmdeCall = (KmsMarshaller) ((org.avm.device.knet.model.KmsCalltrig.DefaultKmsFactory) kf)
				.create(KnetAgent.PHONY_APP, "calltrig");
		_log.debug("Envoie de :" + cmdeCall);
		try {
			post(cmdeCall);
		} catch (KnetException e) {
			_log.error("Erreur à la commande pour la PHONY", e);
			stop();
			return;
		}

	}

	public void stop() {
		_log.debug("stop PHONY [" + KnetAgent.PHONY_APP + "]");
		// if (_agentKnet == null)
		// return;
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsStop.ROLE);
		KmsMarshaller cmdeStop = (KmsMarshaller) ((org.avm.device.knet.model.KmsStop.DefaultKmsFactory) kf)
				.create(KnetAgent.PHONY_APP, "calltrig");
		_log.debug("Envoie de [" + cmdeStop + "]");
		try {
			post(cmdeStop);
			close();
		} catch (KnetException e) {
			_log.error("Erreur � la commande stop pour la PHONY", e);
		}
	}

	public String[] getTypeOfKms() {
		String[] types = new String[1];
		types[0] = KmsCalltrig.ROLE;
		return types;
	}

	public int getKnetApp() {
		return KnetAgent.PHONY_APP;
	}

	public String getKnetAppAsString() {
		return "PHONY_APP";
	}

	public void dialListenMode(String number) {
	}

	public int getDefaultSoundVolume() {
		return 0;
	}

	public int getSignalQuality() {
		return 0;
	}

	public void setRingSound(int num) throws Exception {
	}

	public void setSoundVolume(int val) {
	}

	public void testRingSound() {
	}

}
