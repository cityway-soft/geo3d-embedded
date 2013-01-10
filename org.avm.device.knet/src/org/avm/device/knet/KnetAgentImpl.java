package org.avm.device.knet;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.KmsAuth;
import org.avm.device.knet.model.KmsConf;
import org.avm.device.knet.model.KmsConnect;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.XmlError;
import org.avm.device.knet.model.KmsConnect.DefaultKmsFactory;
import org.avm.elementary.common.ManageableService;

public class KnetAgentImpl implements KnetAgent, ManageableService {

	private static final int MAX_TRIAL = 10;

	private KnetSocketManager _manager;

	private String _host, _login, _passwd, _from, _to, _id;

	private int _port;

	private Logger _log = null;

	public KnetAgentImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		_log.debug("KnetImpl" + this);
	}

	public void open(String host, int port, String login, String passwd,
			int from, int to, int id) throws KnetException {
		String fr = String.valueOf(from);
		String t = String.valueOf(to);
		String i = String.valueOf(id);
		open(host, port, login, passwd, fr, t, i);
	}

	public void open(String host, int port, String login, String passwd,
			int from) throws KnetException {
		String fr = String.valueOf(from);
		open(host, port, login, passwd, fr, null, null);
	}

	public void open(String host, int port, String login, String passwd,
			String from, String to, String id) throws KnetException {
		StringBuffer info = new StringBuffer();
		info.append(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS);
		info.append("\nopening :");
		info.append("\n\thost: " + host);
		info.append("\n\tport: " + port);
		info.append("\n\tlogin: " + login);
		info.append("\n\tpasswd: " + passwd);
		info.append("\n\tfrom: " + from);
		info.append("\n\tto: " + to);
		info.append("\n\tid: " + id);
		info.append(org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
		_log.info(info.toString());

		_host = host;
		_port = port;
		_login = login;
		_passwd = passwd;
		_from = from;
		_to = to;
		_id = id;

		_manager = new KnetSocketManager(host, port);
		// _manager.setReceiver(this);
		_manager.open();
		int cont = 0;
		try {
			while (KnetSocketManager.OPENED != _manager.getStatus()) {
				Thread.sleep(1000);
				if (cont++ > MAX_TRIAL) {
					_log.warn("! Fonctionnement SANS KNET !");
					throw (new KnetException("KNET inaccessible !"));
				}
			}
		} catch (InterruptedException e) {
			_log.error("InterruptedException", e);
		}
		authAndConnect();
	}

	private void authAndConnect() {
		if (KnetSocketManager.OPENED != _manager.getStatus())
			return;

		KmsAuth auth = new KmsAuth(_login, _passwd);
		_log.debug("open::emission " + auth);
		KmsAuth response = null;
		try {
			response = (KmsAuth) _manager.send(auth);
			_log.debug("open::reponse " + response);
			if (!response.getResult().equalsIgnoreCase("ok")) {
				_log.error("Echec authentification login " + _login
						+ ", passwd " + _passwd);
			}

			KmsFactory kf = (DefaultKmsFactory) KmsFactory.factories
					.get(KmsConnect.ROLE);
			KmsMarshaller connect = (KmsMarshaller) ((DefaultKmsFactory) kf)
					.create(_from, _to, _id);
			connect.setIdentifiant(KmsConnect.ROLE);
			_log.debug("open::emission " + connect);
			KmsRoot conf = null;
			try {
				conf = (KmsRoot) _manager.send(connect);
				_log.debug("open::reponse " + conf);

				if (conf.getSubRole().equalsIgnoreCase(KmsConf.ROLE)) {
					KmsConf kc = (KmsConf) conf.getSubKms(KmsConf.ROLE);
					if (!kc.getResult().equalsIgnoreCase("ok")) {
						_log.warn("open::Connect retourne ko!");
						if (!kc.IsAlreadyConnected())
							_log.error("Echec connection from " + _from
									+ " to " + _to + " [" + kc.getDescription()
									+ "]");
					} else {
						_log.info("Connection from " + _from + " to " + _to
								+ " ok");
						// canReceive = true;
					}
				}
			} catch (KnetException e) {
				_log.error("Erreur send Connect", e);
			}
		} catch (KnetException e) {
			_log.error("Erreur send Auth", e);
		}

	}

	public void close() throws KnetException {
		_manager.close();
	}

	public KmsMarshaller send(KmsMarshaller kms) throws KnetException {
		_log.debug("send " + kms);
		KmsRoot conf = (KmsRoot) _manager.send(kms);
		_log.debug("conf =" + conf);
		if (conf.getSubRole().equalsIgnoreCase(KmsConf.ROLE)) {
			KmsConf kc = (KmsConf) conf.getSubKms(KmsConf.ROLE);
			if (!kc.getResult().equalsIgnoreCase("ok")) {
				throw new KnetException("Echec send : " + kc.getDescription());
			}
		}
		return conf;
	}

	public void post(KmsMarshaller kms) throws KnetException {
		_log.debug("post " + kms);
		boolean postOK = false;
		short trials = 3;
		do {
			try {
				_manager.post(kms);
				postOK = true;
			} catch (KnetException ke) {
				_log.info("Retry post ");
			}
			if (trials-- == 0) {
				postOK = true;
			}
		} while (!postOK);
	}

	public KmsMarshaller receiveKms() {
		// _log.debug("receiveKms() ");
		KmsMarshaller kms = null;
		try {
			kms = _manager.recv();
		} catch (KnetException e) {
			_log.error("Le manager ne peut recevoir le message ", e);
			return null;
		}
		if (XmlError.ROLE.equals(kms.getRole())) {
			_log.error("Agent KNET retourne une erreur :"
					+ ((XmlError) kms).getDescription());
			return null;
		}

		String role = ((KmsRoot) kms).getSubRole();
		if (role == null) {
			_log.error("Reception d'un role null !");
			return null;
		}
		// //Bidouille pour ne pas afficher à chaque fois qu'on une position
		// if (!role.equalsIgnoreCase("position"))
		// _log.debug("reception " + role);

		if (KmsConf.ROLE.equals(role)) {
			_log.warn("reception " + role + "["
					+ ((KmsRoot) kms).getSubKms(role) + "]");
		}

		return kms;
	}

	public void start() {
		_log.debug("start KNET [" + KnetAgent.KNET_APP + "]");

		try {
			open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
					KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
					KnetAgent.KNET_APP, KnetAgent.M2M_APP, KnetAgent.LOCAL_NODE);
		} catch (KnetException e) {
			_log.error("Erreur open agent pour le KNET", e);
			return;
		}
	}

	public void stop() {
		_log.debug("stop KNET [" + KnetAgent.KNET_APP + "]");
		try {
			close();
		} catch (KnetException e) {
			_log.error("Erreur close agent pour le KNET", e);
			return;
		}
	}
}
