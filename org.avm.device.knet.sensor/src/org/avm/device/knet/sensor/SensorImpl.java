package org.avm.device.knet.sensor;

import org.apache.log4j.Logger;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.KnetException;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsBeep;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsInputtrig;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsStop;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;

public class SensorImpl extends KnetDevice implements Sensor,
		ConfigurableService, ManageableService {
	private Logger _log = null;
	private LinkedQueue _readerChannel4PorteAV;
	private LinkedQueue _readerChannel4PorteAR;
	private LinkedQueue _readerChannel4Compost;

	private SensorConfig _config = null;

	public SensorImpl() {
		_log = Logger.getInstance(this.getClass());
		// _jdb = JdbLogger.getJdbInstance("sensor");
		// _log.setPriority(Priority.DEBUG);
		_readerChannel4PorteAV = new LinkedQueue();
		_readerChannel4PorteAR = new LinkedQueue();
		_readerChannel4Compost = new LinkedQueue();
	}

	public SensorConfig getConfig() {
		return _config;
	}

	public void configure(Config config) {
		_config = (SensorConfig) config;
	}

	public void beep(int inDuration) {
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsBeep.ROLE);
		KmsMarshaller cmde = (KmsMarshaller) ((org.avm.device.knet.model.KmsBeep.DefaultKmsFactory) kf)
				.create(KnetAgent.SENSOR_APP, inDuration);
		try {
			_log.debug("Envoie de :" + cmde);
			post(cmde);
		} catch (KnetException e) {
			_log.error("Erreur � la commande pour SENSOR", e);
			return;
		}
	}

	public void receive(Kms kms) {
		_log.debug("SENSOR [" + KnetAgent.SENSOR_APP + "] receive " + kms);
		if (kms == null)
			return;
		Kms k = ((KmsRoot) kms).getSubKms();
		if (k instanceof KmsInputtrig) {
			KmsInputtrig inputtrig = (KmsInputtrig) k;
			String digital = inputtrig.getDigital();
			if (digital.equals(Sensor.PORTE_AV_ID)) {
				// IO porte avant actionne
				String etat = inputtrig.getValue();
				_log.debug("Porte avant"
						+ (etat.equals(Sensor.FERME_ID) ? " fermee"
								: " ouverte"));
				try {
					_readerChannel4PorteAV.put(etat);
				} catch (InterruptedException e) {
				}
				// if (etat.equals(Sensor.FERME_ID)){
				// //
				// _jdb.jdb(JdbEventFactory.getJdbEvent(EventTypes.EVT_PAV_F));
				// }else{
				// //
				// _jdb.jdb(JdbEventFactory.getJdbEvent(EventTypes.EVT_PAV_O));
				// }
			}
			if (digital.equals(Sensor.PORTE_ARR_ID)) {
				// IO porte arriere actionne
				String etat = inputtrig.getValue();
				_log.debug("Porte arriere"
						+ (etat.equals(Sensor.FERME_ID) ? " fermee"
								: " ouverte"));
				try {
					_readerChannel4PorteAR.put(etat);
				} catch (InterruptedException e) {
				}
				// if (etat.equals(Sensor.FERME_ID)){
				// //
				// _jdb.jdb(JdbEventFactory.getJdbEvent(EventTypes.EVT_PAR_F));
				// }else{
				// //
				// _jdb.jdb(JdbEventFactory.getJdbEvent(EventTypes.EVT_PAR_O));
				// }
			}
			if (digital.equals(Sensor.COMPOST_ID)) {
				// Composteur actionne
				_log.debug("Composteur actionne");
				try {
					_readerChannel4Compost.put(Sensor.COMPOST_ID);
				} catch (InterruptedException e) {
				}
				// _jdb.jdb(JdbEventFactory.getJdbEvent(EventTypes.EVT_COMP));
			}
		}
	}

	public void start() {
		_log.info("start SENSOR [" + KnetAgent.SENSOR_APP + "]");
		try {
			open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
					KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
					KnetAgent.SENSOR_APP, KnetAgent.M2M_APP,
					KnetAgent.LOCAL_NODE);
			startListen();
		} catch (KnetException e) {
			_log.error("Erreur � la connexion � l'agent pour SENSOR", e);
			return;
		}
		KmsFactory kf = (KmsFactory) KmsFactory.factories
				.get(KmsInputtrig.ROLE);
		KmsMarshaller cmdePAV = (KmsMarshaller) ((org.avm.device.knet.model.KmsInputtrig.DefaultKmsFactory) kf)
				.create(KnetAgent.SENSOR_APP, Sensor.PORTE_AV_ID);
		KmsMarshaller cmdePAR = (KmsMarshaller) ((org.avm.device.knet.model.KmsInputtrig.DefaultKmsFactory) kf)
				.create(KnetAgent.SENSOR_APP, Sensor.PORTE_ARR_ID);
		KmsMarshaller cmdeCOM = (KmsMarshaller) ((org.avm.device.knet.model.KmsInputtrig.DefaultKmsFactory) kf)
				.create(KnetAgent.SENSOR_APP, Sensor.COMPOST_ID, "up");
		try {
			_log.debug("Envoie de :" + cmdePAV);
			post(cmdePAV);
			_log.debug("Envoie de :" + cmdePAR);
			post(cmdePAR);
			_log.debug("Envoie de :" + cmdeCOM);
			post(cmdeCOM);
		} catch (KnetException e) {
			_log.error("Erreur � la commande pour SENSOR", e);
			stop();
			return;
		}
	}

	public void stop() {
		// if (_agentKnet == null)
		// return;
		_log.debug("stop SENSOR [" + KnetAgent.SENSOR_APP + "]");
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsStop.ROLE);
		KmsMarshaller cmdeStopPAV = (KmsMarshaller) ((org.avm.device.knet.model.KmsStop.DefaultKmsFactory) kf)
				.create(KnetAgent.SENSOR_APP, KmsInputtrig.IOTRIG
						+ Sensor.PORTE_AV_ID);
		KmsMarshaller cmdeStopPAR = (KmsMarshaller) ((org.avm.device.knet.model.KmsStop.DefaultKmsFactory) kf)
				.create(KnetAgent.SENSOR_APP, KmsInputtrig.IOTRIG
						+ Sensor.PORTE_ARR_ID);
		KmsMarshaller cmdeStopCOM = (KmsMarshaller) ((org.avm.device.knet.model.KmsStop.DefaultKmsFactory) kf)
				.create(KnetAgent.SENSOR_APP, KmsInputtrig.IOTRIG
						+ Sensor.COMPOST_ID);
		try {
			_log.debug("Envoie de [" + cmdeStopPAV + "]");
			post(cmdeStopPAV);
			_log.debug("Envoie de [" + cmdeStopPAR + "]");
			post(cmdeStopPAR);
			_log.debug("Envoie de [" + cmdeStopCOM + "]");
			post(cmdeStopCOM);
			close();
		} catch (KnetException e) {
			_log.error("Erreur � la commande stop pour SENSOR", e);
		}
	}

	// public String[] getTypeOfKms() {
	// String[] types = new String[1];
	// types[0] = KmsInputtrig.ROLE;
	// return types;
	// }

	public int getKnetApp() {
		return KnetAgent.SENSOR_APP;
	}

	public String getKnetAppAsString() {
		return "SENSOR_APP";
	}

	public String readCompost() {
		String var = null;
		try {
			var = (String) _readerChannel4Compost.take();
			_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
					+ "readCompost::" + var + " supprimé de la queue"
					+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
		} catch (InterruptedException e) {
		}
		return var;
	}

	public String readFrontDoor() {
		String var = null;
		try {
			var = (String) _readerChannel4PorteAV.take();
			_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
					+ "readFrontDoor::" + var + " supprimé de la queue"
					+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
		} catch (InterruptedException e) {
		}
		return var;
	}

	public String readRearDoor() {
		String var = null;
		try {
			var = (String) _readerChannel4PorteAR.take();
			_log.debug(org.avm.elementary.log4j.Constants.SETCOLOR_SUCCESS
					+ "readRearDoor::" + var + " supprimé de la queue"
					+ org.avm.elementary.log4j.Constants.SETCOLOR_NORMAL);
		} catch (InterruptedException e) {
		}
		return var;
	}

}
