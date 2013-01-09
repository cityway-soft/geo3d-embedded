package org.avm.device.knet.bearer;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.KnetException;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsStats;
import org.avm.device.knet.model.KmsStop;
import org.avm.device.knet.model.KmsSystem;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * 
 */
public class BearerManagerImpl extends KnetDevice implements BearerManager,
		ConfigurableService, ManageableService {

	private Logger _log = null;

	private BearerManagerConfig _config = null;
	private ConcurrentHashMap _reply;

	private String _currentBearerName;

	private Hashtable _listBearers;

	private static final long TIMEOUT = 1000;
	private static final int TRIALS = 60;

	public BearerManagerImpl() {
		_log = Logger.getInstance(this.getClass());
		_reply = new ConcurrentHashMap();
		_listBearers = new Hashtable();
		// _log.setPriority(Priority.DEBUG);
	}

	public void configure(Config config) {
		_config = (BearerManagerConfig) config;
	}

	public void addBearerDevice(BearerDevice device) {
		_listBearers.put(device.getName(), device);
	}

	public void removeBearerDevice(BearerDevice device) {
		_listBearers.remove(device.getName());
	}

	public void start() {
		_log.info("start BEARER [" + KnetAgent.BEARER_APP + "]");
		try {
			open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
					KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
					KnetAgent.BEARER_APP, KnetAgent.M2M_APP,
					KnetAgent.LOCAL_NODE);
		} catch (KnetException e) {
			_log
					.error(
							"Erreur à la connexion à l'agent pour le BEARER manager",
							e);
			return;
		}
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsStats.ROLE);
		// <stats period=\"3600\" name=\"stats\" />
		KmsMarshaller cmdeStats = (KmsMarshaller) ((org.avm.device.knet.model.KmsStats.DefaultKmsFactory) kf)
				.create(KnetAgent.BEARER_APP, "3600", "stats");
		_log.debug("Envoie de [" + cmdeStats + "]");
		try {
			post(cmdeStats);
			startListen();
		} catch (KnetException e) {
			_log.error("Erreur à la commande pour le BEARERMANAGER", e);
			stop();
			return;
		}
	}

	public void stop() {
		_log.debug("stop BEARER [" + KnetAgent.BEARER_APP + "]");
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsStop.ROLE);
		KmsMarshaller cmdeStop = (KmsMarshaller) ((org.avm.device.knet.model.KmsStop.DefaultKmsFactory) kf)
				.create(KnetAgent.BEARER_APP, "stats");
		_log.debug("Envoie de [" + cmdeStop + "]");
		try {
			post(cmdeStop);
			close();
		} catch (KnetException e) {
			_log.error("Erreur à la commande stop pour le BEARERMANAGER", e);
		}
	}

	/**
	 * Indique aux bearers qu'il y a eu un changement. Indique aux bearers QUE
	 * s'il y a eu un changement.
	 * 
	 * @param status :
	 *            nom du bearer
	 * @param status :
	 *            peut être connecté ou déconnecté
	 */
	private void notifyBearers(String bearerName, String status) {
		_log.debug("notifyBearers(" + bearerName + ", " + status + ")");
		if (_currentBearerName != null) {
			if (_currentBearerName.equals(bearerName)) {
				// Le bearer n'a pas changé : rien à faire.
				_log.debug("pas de changement de bearer (" + _currentBearerName
						+ ") : rien a faire");
				return;
			}
		}
		_currentBearerName = bearerName;
		_log.info("bearer " + _currentBearerName + " " + status);

		if (_listBearers != null) {
			Enumeration en = _listBearers.elements();
			while (en.hasMoreElements()) {
				BearerDevice bd = (BearerDevice) en.nextElement();
				bd.changeStatus(_currentBearerName, status);
			}
		}
	}

	public int getKnetApp() {
		return KnetAgent.BEARER_APP;
	}

	public String getKnetAppAsString() {
		return "BEARER_APP";
	}

	public void handover(String bearerName, String ssid, String key) {
		_log.info("handover " + bearerName);
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsSystem.ROLE);
		KmsMarshaller ho = (KmsMarshaller) ((org.avm.device.knet.model.KmsSystem.DefaultKmsFactory) kf)
				.create(KnetAgent.BEARER_APP, "handover", bearerName, ssid, key);
		ho.setIdentifiant(bearerName);
		KmsMarshaller km;
		try {
			ho.setIdentifiant(bearerName);
			km = send(ho);
			if (km instanceof KmsSystem) {
				String bearer = ((KmsSystem) km).getAttClass();
				String statut = ((((KmsSystem) km).getReport() == REPORT_OK) ? STATUS_CONN
						: STATUS_DECONN);
				_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
						+ "response [class=" + bearer + ", statut=" + statut
						+ "]"
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
				notifyBearers(bearer, statut);
			}

		} catch (KnetException e) {
			_log.error("KnetException error : " + e.getLocalizedMessage());
		}
	}

	public void handover2Default() {
		_log.info("handover par defaut (gprs)");
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsSystem.ROLE);
		KmsMarshaller ho = (KmsMarshaller) ((org.avm.device.knet.model.KmsSystem.DefaultKmsFactory) kf)
				.create(KnetAgent.BEARER_APP, "handover",
						BearerManager.BEARER_gprs);

		ho.setIdentifiant(BearerManager.BEARER_gprs);
		KmsMarshaller km;
		try {
			km = send(ho);
			if (km instanceof KmsSystem) {
				String bearer = ((KmsSystem) km).getAttClass();
				String statut = ((((KmsSystem) km).getReport() == REPORT_OK) ? STATUS_CONN
						: STATUS_DECONN);
				_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
						+ "response [class=" + bearer + ", statut=" + statut
						+ "]"
						+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
				notifyBearers(bearer, statut);
			}
		} catch (KnetException e) {
			_log.error("KnetException error", e);
		}
	}

	public void receive(Kms kms) {
		_log.debug("BEARER [" + KnetAgent.BEARER_APP + "] receive " + kms);
		if (kms == null)
			return;
		Kms k = ((KmsRoot) kms).getSubKms();
		KmsStats stat;
		if (k instanceof KmsStats) {
			stat = (KmsStats) k;
			if (stat.getBearer() == null)
				return;
			String status = stat.getStatus();
			if (status != null) {
				notifyBearers(stat.getBearer(), status);
			}
			return;
		}
		KmsSystem system;
		if (k instanceof KmsSystem) {
			system = (KmsSystem) k;
			String bearer = system.getAttClass();
			String statut = ((system.getReport() == REPORT_OK) ? STATUS_CONN
					: STATUS_DECONN);
			_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
					+ "response [class=" + bearer + ", statut=" + statut + "]"
					+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
			notifyBearers(bearer, statut);

		}
	}

	public String getCurrentBearerName() {
		return _currentBearerName;
	}

}