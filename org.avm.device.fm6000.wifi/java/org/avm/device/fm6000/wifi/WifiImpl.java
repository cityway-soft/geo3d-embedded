package org.avm.device.fm6000.wifi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.fm6000.wifi.jni.COMVS_WIFI;
import org.avm.device.fm6000.wifi.jni.COMVS_WIFIDEVICE_CONFIGURATION;
import org.avm.device.fm6000.wifi.jni.COMVS_WIFIDEVICE_EAPOL_PARAMS;
import org.avm.device.fm6000.wifi.jni.COMVS_WIFIDEVICE_WEP_CONFIGURATION;
import org.avm.device.plateform.Network;
import org.avm.device.wifi.Wifi;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.osgi.util.measurement.State;

public class WifiImpl implements Wifi, ConfigurableService, ProducerService,
		ManageableService {

	private WifiConfig _config;

	private ProducerManager _producer;

	private Properties _properties = new Properties();

	private Logger _log = Logger.getInstance(this.getClass());

	public WifiImpl() {

	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (WifiConfig) config;
	}

	public Properties getProperties() {
		return _properties;
	}

	public void start() {
		connect();
	}

	public void stop() {
		
	}

	public void connect() {
		if (!isConnected()) {
			_log.debug("connect...");
			COMVS_WIFI.Comvs_DeleteWifiPreferedNetworks();
			initilize();
			Thread deamon = new Thread(new StateNoticationTask(false));
			deamon.setDaemon(true);
			deamon.start();
		}
	}

	public void disconnect() {
		_log.debug("disconnect...");
		if (isConnected()) {
			COMVS_WIFI.Comvs_DeleteWifiPreferedNetworks();
			Thread deamon = new Thread(new StateNoticationTask(true));
			deamon.setDaemon(true);
			deamon.start();
		}
	}

	public boolean isConnected() {
		_log.debug("isConnected...");
		boolean result = false;
		try {
			result = !getAddress().equals(InetAddress.getByName("0.0.0.0"));
		} catch (Exception e) {
		}
		_log.debug("isConnected : " + result);
		return result;
	}

	protected void initilize() {
		_log.debug("initilize()");
		Dictionary p = _config.getProperties();
		Enumeration e = p.keys();
		while (e.hasMoreElements()) {
			String ssid = (String) e.nextElement();
			Properties config = _config.getProperty(ssid);
			if (p != null && config.getProperty("adhoc") != null) {
				WifiParameters parameters = new WifiParameters(ssid, config);
				addNetwork(parameters);
			}
		}
	}

	private void addNetwork(WifiParameters config) {

		System.out.println("[DLA] addNetwork :" + config);
		COMVS_WIFIDEVICE_CONFIGURATION network = new COMVS_WIFIDEVICE_CONFIGURATION();
		COMVS_WIFIDEVICE_WEP_CONFIGURATION wep = new COMVS_WIFIDEVICE_WEP_CONFIGURATION();
		COMVS_WIFIDEVICE_EAPOL_PARAMS authentication = new COMVS_WIFIDEVICE_EAPOL_PARAMS();
		network.setStWEP_config(wep);
		network.setSt802_1x_mode_authentication(authentication);

		// ssid
		String ucSSID = config.getSSID().trim();
		ucSSID = (ucSSID.length() > COMVS_WIFI.SSID_SIZE_MAX) ? ucSSID
				.substring(0, COMVS_WIFI.SSID_SIZE_MAX) : ucSSID;
				
		network.setUcSSID(ucSSID);
		// TODO network.setNbOfConfig(value)

		// infrastructure mode
		if (config.isAdhoc()) {
			network.setIMode_infrastructure(0);
		} else {
			network.setIMode_infrastructure(1);
		}

		// encryption mode
		System.out.println("[DLA] Encryption=" + config.isEncryption());
		if (config.isEncryption()) {

			network.setUlMode_cipher(0);
			System.out.println("[DLA] AutomaticWEPKey=" + config.isAutomaticWEPKey());
			if (!config.isAutomaticWEPKey()) {
				// wep key
				System.out.println("[DLA] wepkey=" + config.getWEPKey());
				if (config.getWEPKey() != null) {
					String ucWepKey = config.getWEPKey().trim();
					ucWepKey = (ucWepKey.length() > COMVS_WIFI.WEP_KEY_SIZE_MAX) ? ucWepKey
							.substring(0, COMVS_WIFI.WEP_KEY_SIZE_MAX)
							: ucWepKey;
					wep.setUlWEP_key_index(1);
					wep.setUcWEP_key(ucWepKey);
					wep.setUlWEP_key_size(ucWepKey.length());
					network.setStWEP_config(wep);
				}
			}
			// authentication mode
			if (config.isOpenAuthentication()) {
				network.setIWifi_mode_authentication(0);
			} else {
				network.setIWifi_mode_authentication(1);
			}

		}
		// no encryption
		else {
			network.setUlMode_cipher(1);
		}

		if (config.isAuthentication()) {
			authentication.setIEnable8021x(1);
			if (config.getEAPtype().compareTo("TLS") == 0) {
				authentication.setUlEapType(13);
			} else if (config.getEAPtype().compareTo("PEAP") == 0) {
				authentication.setUlEapType(25);
			} else if (config.getEAPtype().compareTo("MD5") == 0) {
				authentication.setUlEapType(4);
			}
		} else {
			authentication.setIEnable8021x(0);
		}

		COMVS_WIFI.Comvs_SetWifiPreferedNetwork(network);

	}

	private InetAddress getAddress() {
		_log.debug("getAddress");
		InetAddress result = null;
		InetAddress[] array = Network.getInetAddresses();
		System.out.println("WifiImpl.getAddress()");
		if (array != null && array.length > 0) {
			result = array[0];
		} else {
			try {
				result = InetAddress.getByName("0.0.0.0");
			} catch (UnknownHostException e) {
			}
		}
	
		return result;
	}

	private void sleep(long value) {
		try {
			Thread.sleep(value);
		} catch (InterruptedException e) {
		}
	}

	class StateNoticationTask implements Runnable {

		private static final int TIMEOUT = 30;

		private boolean _reverse;

		public StateNoticationTask(boolean reverse) {
			_reverse = reverse;
		}

		public void run() {
			int count = TIMEOUT;

			while ((_reverse ^ !isConnected()) && count-- > 0) {
				sleep(1000);
			}
			if (isConnected()) {
				_properties.put(Wifi.class.getPackage().getName() + ".address",
						getAddress().toString());
				_producer.publish(new State(1, Wifi.class.getPackage()
						.getName()
						+ ".connected"));
			} else {
				_properties.remove(Wifi.class.getPackage().getName()
						+ ".address");
				_producer.publish(new State(0, Wifi.class.getPackage()
						.getName()
						+ ".disconnected"));
			}

		}
	}
}