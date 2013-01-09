package org.avm.device.knet.gps;

import org.apache.log4j.Logger;
import org.avm.device.gps.Gps;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.KnetDevice;
import org.avm.device.knet.KnetException;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsPosition;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsStop;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.Unit;
import org.osgi.util.position.Position;

public class GpsImpl extends KnetDevice implements Gps, ConfigurableService,
		ManageableService, ProducerService {

	private Logger _log = null;
	private boolean debugOn = false;
	private ProducerManager _producer = null;
	private GpsConfig _config = null;

	private int current = NB_POS;
	private static final int NB_POS = 10;
	private Position[] positions = null;
	private Position lastValidPosition = null;
	private int nbPositions = 0;

	// //quantite de RADIAN en LONGITUDE pour 10 metres
	// private static double RAD4METERS_LON=0;
	// //quantite de RADIAN en LATITUDE pour 10 metres
	// private static double RAD4METERS_LAT=0;

	// private static final double PI = 3.14159265358;

	public GpsImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		positions = new Position[NB_POS];
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (GpsConfig) config;
		_log.debug("_config = " + _config);
	}

	public GpsConfig getConfig() {
		return _config;
	}

	public void setDebugOn() {
		_log.debug("setDebugOn()");
		debugOn = true;
	}

	public void unsetDebugOn() {
		_log.debug("unsetDebugOn()");
		debugOn = false;
	}

	public void newPosition(Position inPosition, boolean inValid) {
		nbPositions++;
		if (nbPositions > NB_POS) {
			nbPositions = NB_POS;
		}
		current++;
		if (current >= NB_POS) {
			current = 0;
		}
		positions[current] = inPosition;

		if (inValid) {
			lastValidPosition = inPosition;
		}
		// double x = inPosition.getLongitude().getValue() * 180d / Math.PI;
		// double y = inPosition.getLatitude().getValue() * 180d / Math.PI;
		// _log.debug("Position originale : lon = " + x + " lat = " + y);
		// if(_config.getCorrect().booleanValue()){
		// double delay = _config.getDelay().doubleValue();
		// Position pos = inPosition;
		// inPosition = correctPosition(pos, delay);
		// x = inPosition.getLongitude().getValue()* 180d / Math.PI;
		// y = inPosition.getLatitude().getValue() * 180d / Math.PI;
		// _log.debug("Position corrigee : lon = " + x + " lat = " + y);
		//
		// }

		// publish position
		if (_producer != null) {
			_log.debug("Publication d'une position.");
			_producer.publish(inPosition);
		} else {
			_log.warn("Pas de producer.");
		}
	}

	/**
	 * @see org.avm.device.generic.gps.Gps#getCurrentPosition()
	 */
	public Position[] getLastPositions() {
		Position[] lastPositions = null;

		if (nbPositions > 0) {
			int index = current;
			int nb = nbPositions;
			lastPositions = new Position[nbPositions];
			while (nb > 0) {
				nb--;
				lastPositions[nb] = positions[index];
				if (index > 0) {
					index--;
				} else {
					index = NB_POS - 1;
				}
			}
		}

		return (lastPositions);
	}

	/**
	 * @see org.avm.device.generic.gps.Gps#getCurrentPosition()
	 */
	public Position getCurrentPosition() {
		Position position = null;

		if (current < NB_POS) {
			position = positions[current];
		}

		return (position);
	}

	/**
	 * @see org.avm.device.generic.gps.Gps#getLastValidPosition()
	 */
	public Position getLastValidPosition() {
		return (lastValidPosition);
	}

	public void start() {
		_log.info("start GPS [" + KnetAgent.GPS_APP + "]");

		try {
			open(KnetAgent.KNETDHOST, KnetAgent.KNETDPORT,
					KnetAgent.AUTH_login, KnetAgent.AUTH_passwd,
					KnetAgent.GPS_APP, KnetAgent.M2M_APP, KnetAgent.LOCAL_NODE);
		} catch (KnetException e) {
			_log.error("Erreur open agent pour le GPS", e);
			return;
		}
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsPosition.ROLE);
		KmsMarshaller cmdePos = (KmsMarshaller) ((org.avm.device.knet.model.KmsPosition.DefaultKmsFactory) kf)
				.create(KnetAgent.GPS_APP, KmsPosition.ROLE, 1);
		_log.debug("Envoie de :" + cmdePos);
		try {
			post(cmdePos);
			startListen();
		} catch (KnetException e) {
			_log.error("Erreur � la commande pour le GPS", e);
			stop();
			return;
		}
	}

	public void stop() {
		_log.debug("stop GPS [" + KnetAgent.GPS_APP + "]");
		KmsFactory kf = (KmsFactory) KmsFactory.factories.get(KmsStop.ROLE);
		KmsMarshaller cmdeStop = (KmsMarshaller) ((org.avm.device.knet.model.KmsStop.DefaultKmsFactory) kf)
				.create(KnetAgent.GPS_APP, KmsPosition.ROLE);
		_log.debug("Envoie de [" + cmdeStop + "]");
		try {
			post(cmdeStop);
			close();
		} catch (KnetException e) {
			_log.error("Erreur � la commande stop pour le GPS", e);
		}
	}

	public void receive(Kms kms) {
		_log.debug("GPS [" + KnetAgent.GPS_APP + "] receive " + kms);
		if (kms == null)
			return;
		Kms k = ((KmsRoot) kms).getSubKms();
		if (k instanceof KmsPosition) {
			KmsPosition pos = (KmsPosition) k;
			Measurement meaLat = new Measurement(
					(pos.getLatitude() * Math.PI) / 18000000, 0, Unit.rad, pos
							.getDateAsLong());
			Measurement meaLong = new Measurement(
					(pos.getLongitude() * Math.PI) / 18000000, 0, Unit.rad, pos
							.getDateAsLong());
			Measurement meaAlt = new Measurement(pos.getAltitude(), 0, Unit.m,
					pos.getDateAsLong());
			// mettre en m/s
			Measurement meaSpeed = new Measurement(pos.getSpeed() / 3.6, 0,
					Unit.m_s, pos.getDateAsLong());
			Measurement meaTrack = new Measurement(
					(pos.getCourse() * Math.PI) / 180, 0, Unit.rad, pos
							.getDateAsLong());

			// add this pos now
			newPosition(new Position(meaLat, meaLong, meaAlt, meaSpeed,
					meaTrack), (0 != pos.getFix()));
		}
	}

	// public String[] getTypeOfKms() {
	// String[] types = new String[1];
	// types[0] = KmsPosition.ROLE;
	// return types;
	// }
	public int getKnetApp() {
		return KnetAgent.GPS_APP;
	}

	public String getKnetAppAsString() {
		return "GPS_APP";
	}
}
