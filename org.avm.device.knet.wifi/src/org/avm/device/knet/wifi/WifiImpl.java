/**
 *
 */

package org.avm.device.knet.wifi;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.bearer.BearerDevice;
import org.avm.device.knet.bearer.BearerManager;
import org.avm.device.wifi.Wifi;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.osgi.util.measurement.State;

/**
 */

public class WifiImpl implements BearerDevice, Wifi, ConfigurableService,
		ProducerService {

	private Logger _log;

	private ProducerManager _producer;

	private WifiConfig _config;

	private BearerManager _bearerManager;

	public WifiImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void unsetBearerManager() {
		// Informer le bearerManager qu'il ne gère plus this.
		_bearerManager.removeBearerDevice((BearerDevice) this);
		_bearerManager = null;
	}

	public void setBearerManager(BearerManager bearer) {
		_bearerManager = bearer;
		_bearerManager.addBearerDevice((BearerDevice) this);
	}

	public void configure(Config config) {
		_log.debug("Receive configuration");
		_config = (WifiConfig) config;
	}

	public boolean isConnected() {
		String bearer = _bearerManager.getCurrentBearerName();
		if (bearer == null)
			return false;
		return BearerManager.BEARER_wlan.equalsIgnoreCase(bearer);
	}

	public void connect() {
		_log.debug("Try to connect Wifi");
		String bearer = _bearerManager.getCurrentBearerName();
		_log.debug(bearer);
		if (!BearerManager.BEARER_wlan.equalsIgnoreCase((bearer == null) ? ""
				: bearer)) {
			_bearerManager.handover(BearerManager.BEARER_wlan, _config
					.getSSID(), _config.getWEPKey());
		} else
			_log.info("cannot connect to Wifi, still trying");
	}

	public void disconnect() {
		_log.debug("Try to disconnect Wifi");
		String bearer = _bearerManager.getCurrentBearerName();
		_log.debug(bearer);
		if (!bearer.equalsIgnoreCase(BearerManager.BEARER_default)) {
			_bearerManager.handover2Default();
		}
	}

	// private void setDefaultConfiguration(WifiConfig config) {
	// _config.setSSID(config.getSSID());
	// _config.setAdhoc(config.isAdhoc());
	// _config.setEncryption(config.isEncryption());
	// _config.setAuthentication(config.isAuthentication());
	// _config.setAutomaticWEPKey(config.isAutomaticWEPKey());
	// _config.setOpenAuthentication(config.isOpenAuthentication());
	// _config.setWEPKey(config.getWEPKey());
	// _config.setEAPtype(config.getEAPtype());
	// }

	// public void start() {
	// if (_agentKnet == null) {
	// _log.error("Agent KNET non initialisé !!");
	// return;
	// }
	// _log.info("start WIFI [" + KnetAgent.WIFI_APP + "]");
	// try {
	// _agentKnet
	// .open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
	// KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
	// KnetAgent.WIFI_APP, KnetAgent.M2M_APP,
	// KnetAgent.LOCAL_NODE);
	// } catch (KnetException e) {
	// _log.error("Erreur à la connexion à l'agent pour WIFI", e);
	// return;
	// }
	// connect();
	// }
	//
	// public void stop() {
	// if (_agentKnet == null)
	// return;
	// _log.debug("stop WIFI [" + KnetAgent.WIFI_APP + "]");
	// try {
	// _agentKnet.close();
	// } catch (KnetException e) {
	// _log.error("Erreur à la commande stop pour WIFI", e);
	// }
	// }

	// public String[] getTypeOfKms() {
	// String[] types = new String[1];
	// types[0] = KmsStats.ROLE;
	// return types;
	// }

	public int getKnetApp() {
		return KnetAgent.WIFI_APP;
	}

	public String getName() {
		return BearerManager.BEARER_wlan;
	}

	public void start() {
		// Informer le bearerManager qu'il gère this.
		_bearerManager.addBearerDevice((BearerDevice) this);
	}

	public void stop() {
		// Informer le bearerManager qu'il ne gère plus this.
		_bearerManager.removeBearerDevice((BearerDevice) this);
	}

	public void changeStatus(String bearerName, String status) {
		if (bearerName.equals(BearerManager.BEARER_wlan)) {
			if (status.equals(BearerManager.STATUS_CONN)) {
				// _log.info("Publication d'un
				// wifiStatut(true,"+Wifi.MASTERIP+")");
				_producer.publish(new State(1, Wifi.class.getName()));
			} else {
				// _log.info("Publication d'un
				// wifiStatut(false,"+Wifi.MASTERIP+")");
				_producer.publish(new State(0, Wifi.class.getName()));
			}
		}
	}

	public String getPid() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public Properties getProperties() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public List getAlarm() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public String getProducerPID() {
		// TODO Raccord de méthode auto-généré
		return null;
	}
}