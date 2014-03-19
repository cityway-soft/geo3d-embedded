package org.avm.business.billettique.atoumod;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.avm.business.core.AbstractAvmModelListener;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmInjector;
import org.avm.business.core.AvmModel;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.osgi.util.measurement.State;

import fr.cityway.avm.billettique.atoumod.model.TicketingSystemState;

public class BillettiqueImpl implements ConfigurableService, AvmInjector,
		ManageableService, ConsumerService, Billettique, Constants,
		PCE415Listener, ProducerService, JDBInjector {

	private Logger _log = Logger.getInstance(this.getClass());

	private BillettiqueConfig _config;

	private ModelListener _listener;

	private Avm _avm;

	private boolean _initialized = false;

	private AlarmService _alarmService;

	private PCE415 client;

	private int etatPrecedent = -1;

	private int arretPrecedent;

	private int coursePrecedente;

	private Date connectionDate;

	private ProducerManager _producer;

	private State _state = new State(0, Billettique.class.getName());

	private Boolean _previousState = null;

	private boolean _enabled;

	private JDB _jdb;

	public static final String JDB_TAG = "billettique";

	
	public void configure(Config config) {
		_config = (BillettiqueConfig) config;

		if (client != null) {
			client.shutdown();
			client = null;
		}
		try {
			_log.info("create client on " + _config.getHost() + ":"
					+ _config.getPort() + " with tsurv=" + _config.getTSurv()
					+ ", nsurv=" + _config.getNSurv());
			client = new PCE415(_config.getHost(), _config.getPort(),
					_config.getTSurv(), _config.getNSurv(), _config.getLocalPort());
			client.setListener(this);
		} catch (SocketException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		}
	}

	public void setAvm(Avm avm) {
		_avm = avm;
		initialize();
	}

	public void unsetAvm(Avm avm) {
		_listener = null;
		_initialized = false;
	}

	private void initialize() {
		if (!_initialized) {
			if (_avm != null) {
				_listener = new ModelListener(_avm);
				_listener.notify(_avm.getModel().getState());
				_initialized = true;
			}
		}
	}

	public void setAlarmService(AlarmService service) {
		_alarmService = service;
	}

	public void unsetAlarmService(AlarmService service) {
		_alarmService = service;
	}

	public void start() {
		if (client != null) {
			client.launch();
		}
	}

	public void stop() {
		if (client != null) {
			client.shutdown();
		}
		_initialized = false;

	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(AlarmService.class.getName())) {

			} else if (_listener != null) {
				_listener.notify(o);
			}
		}
	}

	class ModelListener extends AbstractAvmModelListener {

		public ModelListener(Avm avm) {
			super(avm);
		}

		public void onStateAttenteDepart(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStateAttenteSaisieCourse(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStateAttenteSaisieService(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStateEnCourseArretSurItineraire(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStateEnCourseHorsItineraire(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStateEnCourseInterarretSurItineraire(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStateEnCourseServiceSpecial(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStateEnPanne(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStateInitial(AvmModel model) {
			publishToBillettique(model);
		}

		public void onStatePause(AvmModel model) {
			publishToBillettique(model);
		}
	}

	private void publishToBillettique(AvmModel model) {
		_log.info("Prepare to publish...");
		int course = 0;
		int matricule = 0;
		int ligne = 0;
		int point = 0;
		int etat = 0;
		int sens = 1;

		if (model.getCourse() != null) {
			course = model.getCourse().getIdu();
			sens = model.getCourse().getSens();
		}
		if (model.getAuthentification() != null) {
			matricule = model.getAuthentification().getMatricule();
		}
		if (model.getDernierPoint() != null) {
			point = model.getDernierPoint().getIdu();
		}
		if (model.getCourse() != null) {
			ligne = model.getCourse().getLigneIdu();
		}
		switch (model.getState().getValue()) {
		case AvmModel.STATE_INITIAL:
		case AvmModel.STATE_ATTENTE_SAISIE_SERVICE: {
			etat = TicketingSystemState.SERVICE_FERME;
		}
			break;
		case AvmModel.STATE_ATTENTE_SAISIE_COURSE: {
			etat = TicketingSystemState.SERVICE_OUVERT_COURSE_FERMEE;
		}break;
		case AvmModel.STATE_EN_COURSE_HORS_ITINERAIRE:{
			etat = TicketingSystemState.NON_LOCALISE;
		}
			break;
		case AvmModel.STATE_ATTENTE_DEPART:
		case AvmModel.STATE_EN_COURSE_ARRET_SUR_ITINERAIRE:
		case AvmModel.STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE: {
			// -- correction : demande ACS état "course ouverte" si et seulement
			// si point détecté.
			if (model.getDernierPoint() != null) {
				etat = TicketingSystemState.SERVICE_OUVERT_COURSE_OUVERTE;
			} else {
				etat = TicketingSystemState.SERVICE_OUVERT_COURSE_FERMEE;
			}
		}
			break;

		}
		if (client != null) {
			client.setLigne(ligne);
			client.setCourse(course);
			client.setPoint(point);
			client.setSens(sens);
			client.setMatricule(matricule);
			client.setEtatExploitation(etat);
			try {
				if (etatPrecedent != etat || coursePrecedente != course
						|| arretPrecedent != point) {
					etatPrecedent = etat;
					coursePrecedente = course;
					arretPrecedent = point;
					_log.info("state changed: course=" + course + ", arret="+point + ", etat=" + etat);
					client.sendMessageInterrogation();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			_log.error("PCE415 not initialized (null!)");
		}
	}

	public void setEnable(boolean b) {
		
		if (b) {
			client.launch();
		
			_log.info("Ticketing interface enabled");
		} else {
			client.shutdown();
			_previousState=null;
			connected(false);
			_log.info("Ticketing interface disabled");
		}
	}

	public Date getConnectionDate() {
		return connectionDate;
	}

	public void connected(boolean state) {
		if (_previousState == null || _previousState.booleanValue() != state) {
			_previousState = new Boolean(state);
			if (state) {
				connectionDate = new Date();
				_state = new State(1, Billettique.class.getName());
				journalize("CONNECTED");
			} else {
				connectionDate = null;
				_state = new State(0, Billettique.class.getName());
				journalize("NOT-CONNECTED");
			}
			_producer.publish(_state);
		}

	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}
	
	public void journalize(String message) {
		_log.info(message);

		if (_jdb != null) {
			try {
				_jdb.journalize(JDB_TAG, message);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

}
