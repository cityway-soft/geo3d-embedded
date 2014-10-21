package org.avm.business.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.business.core.bundle.ConfigImpl;
import org.avm.business.core.event.Authentification;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.business.protocol.phoebus.DepartCourse;
import org.avm.business.protocol.phoebus.Deviation;
import org.avm.business.protocol.phoebus.FinCourse;
import org.avm.business.protocol.phoebus.FinPrisePoste;
import org.avm.business.protocol.phoebus.FinPriseService;
import org.avm.business.protocol.phoebus.Message;
import org.avm.business.protocol.phoebus.Planification;
import org.avm.business.protocol.phoebus.PrisePoste;
import org.avm.business.protocol.phoebus.PriseService;
import org.avm.business.protocol.phoebus.Service;
import org.avm.device.gps.Gps;
import org.avm.device.gps.GpsInjector;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.database.Database;
import org.avm.elementary.database.DatabaseInjector;
import org.avm.elementary.geofencing.Balise;
import org.avm.elementary.geofencing.GeoFencing;
import org.avm.elementary.geofencing.GeoFencingInjector;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.messenger.Messenger;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.elementary.variable.Variable;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.util.measurement.State;

public class AvmImpl implements Avm, ConfigurableService, ManageableService,
		ProducerService, ConsumerService, DatabaseInjector, GpsInjector,
		AvmStateMachine, JDBInjector, Serializable, GeoFencingInjector,
		UserSessionServiceInjector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final boolean DEBUG = Boolean.valueOf(
			System.getProperty("org.avm.debug", "false")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$

	private static final String AVM_SERIALIZATION_FILE = System
			.getProperty("org.avm.home") //$NON-NLS-1$
			+ "/bin/avmmodel.bin"; //$NON-NLS-1$

	private static final int ALARM_DEVIATION_INDEX = 2;

	private transient Alarm alarmDeviation;

	private transient Logger _log;

	private transient JDB _jdb;

	private transient ProducerManager _producer;

	private transient ConfigImpl _config;

	private transient Gps _gps;

	private AvmStateMachineContext _fsm;

	private transient AvmDatasource _currentDatasource;

	private transient SuiviItineraire _suiviItineraire;

	private transient DefaultPrisePoste _defautPrisePosteService;

	private transient Messenger _messenger;

	// --modele

	private AvmModelManager _model;

	// Controler

	private transient AvmPlanificationDatasource _planificationDatasource;

	private transient AvmDatabaseDatasource _databaseDatasource;

	private transient UserSessionService _session;

	private transient Variable _odometer;

	private transient Timer _timerAttentePlanification = null;

	private transient boolean _flagCheckPlanification = true;

	private transient GeoFencing _geofencing;

	private transient boolean _login = false;

	private transient double _indexOdometer = 0;

	private transient boolean _flagSendPriseService = true;

	private transient AlarmService _alarmService;

	private int _lastState = -1;

	private AvmImpl() {
		_model = new AvmModelManager();
		init();
	}

	private void init() {
		_log = Logger.getInstance(this.getClass());
		_suiviItineraire = new SuiviItineraire();
		_suiviItineraire.setAvm(this);
		_defautPrisePosteService = new DefaultPrisePoste();
		_defautPrisePosteService.setAvm(this);
	}

	public void start() {
		if (_fsm == null) {
			_fsm = new AvmStateMachineContext(this);
			_fsm.setDebugFlag(DEBUG);
		}
		_suiviItineraire.setAvm(this);
		_suiviItineraire.setProducer(_producer);
		_defautPrisePosteService.setAvm(this);
		_defautPrisePosteService.setProducer(_producer);
		_defautPrisePosteService.setOdometer(_odometer);
		if (_model.isDepart()) {
			_suiviItineraire.start();
		}

		if (!_login) {
			startGestionDefautPrisePoste();
		}
	}

	public void stop() {
		_fsm = null;
		_suiviItineraire.stop();
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
		if (config != null) {
			if (_suiviItineraire != null)
				_suiviItineraire.configure(config);
			else
				_log.warn("SuiviCourse est null!!"); //$NON-NLS-1$
		}
	}

	/**
	 * Injection des services
	 */
	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.avm.elementary.database.DatabaseInjector#setDatabase(org.avm.elementary
	 * .database.Database)
	 */
	public void setDatabase(Database database) {
		debug("Initialisation de la database " + database); //$NON-NLS-1$
		_databaseDatasource = AvmDatabaseDatasource.getInstance();
		try {
			_databaseDatasource.setDatabase(database);
		} catch (AvmDatabaseException e) {
			_model.setError(e.getError());
		}
		_planificationDatasource = AvmPlanificationDatasource.getInstance();
		_planificationDatasource.setDatabase(database);
		_currentDatasource = _databaseDatasource;
		_model.setDatasource(_databaseDatasource);
		checkPlanification();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.avm.elementary.database.DatabaseInjector#unsetDatabase(org.avm.elementary
	 * .database.Database)
	 */
	public void unsetDatabase(Database database) {
		_databaseDatasource = null;
		_planificationDatasource = AvmPlanificationDatasource.getInstance();
		_planificationDatasource.setDatabase(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.gps.GpsInjector#setGps(org.avm.device.gps.Gps)
	 */
	public void setGps(Gps gps) {
		_gps = gps;
		if (_suiviItineraire != null) {
			_suiviItineraire.setGps(_gps);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.gps.GpsInjector#unsetGps(org.avm.device.gps.Gps)
	 */
	public void unsetGps(Gps gps) {
		_gps = null;
	}

	/*
	 * 
	 */
	public void setOdometer(Variable odometer) {
		_odometer = odometer;
		resetOdometer();
		_suiviItineraire.setOdometer(_odometer);
	}

	private void resetOdometer() {
		if (_odometer != null) {
			_indexOdometer = _odometer.getValue().getValue();
		}
	}

	private double getOdometerValue() {
		if (_odometer != null) {
			return _odometer.getValue().getValue() - _indexOdometer;
		} else {
			return -1;
		}
	}

	/*
	 * 
	 */
	public void setMessenger(Messenger messenger) {
		_messenger = messenger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.avm.elementary.jdb.JDBInjector#setJdb(org.avm.elementary.jdb.JDB)
	 */
	public void setJdb(JDB jdb) {
		_jdb = jdb;
		_suiviItineraire.setJdb(jdb);
		_defautPrisePosteService.setJdb(jdb);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.avm.elementary.jdb.JDBInjector#unsetJdb(org.avm.elementary.jdb.JDB)
	 */
	public void unsetJdb(JDB jdb) {
		_jdb = null;
		_defautPrisePosteService.unsetJdb(_jdb);
		_suiviItineraire.unsetJdb(_jdb);
	}

	/*
	 * 
	 */
	public void setPreferencesService(PreferencesService prefs) {
		_planificationDatasource = AvmPlanificationDatasource.getInstance();
		_planificationDatasource.setPreferencesService(prefs);
	}

	/*
	 * 
	 */
	public void unsetPreferencesService(PreferencesService prefs) {
		if (_planificationDatasource != null) {
			_planificationDatasource.unsetPreferencesService(null);
		}
	}

	public void notify(Object o) {
		debug("Notify : " + o); //$NON-NLS-1$
		if (o instanceof State) {
			if (o instanceof State) {
				State state = (State) o;
				if (state.getName().equals(AlarmService.class.getName())) {
					Alarm alarm = _alarmService.getAlarm(new Integer(
							ALARM_DEVIATION_INDEX));
					if (_model.isHorsItineraire() != alarm.isStatus()) {
						if (alarm.isStatus()) {
							sortieItineraire();
						} else {
							_log.info("Impossible de desactiver une déviation !");
						}
					}

				} else if (state.getName().equals(
						UserSessionService.class.getName())) {
					if (state.getValue() == UserSessionService.AUTHENTICATED) {
						loggedOn();
					} else {
						loggedOut();
					}
				}
			}
		} else if (o instanceof Planification) {
			cancelTimer();
			Planification planif = (Planification) o;
			_flagCheckPlanification = true;
			_planificationDatasource.checkCurrentMatricule(_model
					.getAuthentification().getMatricule());
			_planificationDatasource.notify(planif);
			_model.setPlanification(_planificationDatasource.getPlanification());
			debug(_model.getPlanification());

			StringBuffer buf = new StringBuffer();
			buf.append("PLANIF;"); //$NON-NLS-1$
			buf.append(_model.getPlanification().getId());
			buf.append(";"); //$NON-NLS-1$
			buf.append(_model.getPlanification().getVersion());
			buf.append(";"); //$NON-NLS-1$
			buf.append(_model.getPlanification().getServiceIdu());
			buf.append(";"); //$NON-NLS-1$
			buf.append(_model.getPlanification().isCorrect());
			journalize(buf.toString());

			checkPlanification();
			checkForcePriseService();
			synchronize("receive planification"); //$NON-NLS-1$
		} else if (o instanceof Balise) {
			if (_suiviItineraire != null && _suiviItineraire.isRunning()
					&& !_model.isGeorefMode()) {
				Balise balise = (Balise) o;
				if (balise.isInside()) {
					entree(balise.getId());
				} else {
					sortie(balise.getId());
				}
			}
		}

	}

	// interface homme-machine
	public void login(String matricule, String nom, String prenom) {
		if (_login)
			return;
		Authentification aut = new Authentification(
				Integer.parseInt(matricule), nom, prenom);
		Authentification previousAut = _model.getAuthentification();
		debug("Authentification precedente :" + previousAut); //$NON-NLS-1$
		if (previousAut != null) {
			if (!previousAut.equals(aut)) {
				_log.warn("Attention : Conducteur " //$NON-NLS-1$
						+ previousAut.getMatricule()
						+ " (" //$NON-NLS-1$
						+ previousAut.getPrenom()
						+ ") déjà en service , donc FinPoste du conducteur precedent!"); //$NON-NLS-1$
				finPoste();
				_model.setAuthentification(aut);
				_log.info("Prise en compte nouveau conducteur " //$NON-NLS-1$
						+ _model.getAuthentification());
				_planificationDatasource.checkCurrentMatricule(_model
						.getAuthentification().getMatricule());
				_model.setPlanification(_planificationDatasource
						.getPlanification());
				_flagCheckPlanification = true;
			} else {
				debug("Envoi de la prise de poste au serveur"); //$NON-NLS-1$
				_model.setAuthentification(aut);
				_planificationDatasource.checkCurrentMatricule(_model
						.getAuthentification().getMatricule());
				_model.setPlanification(_planificationDatasource
						.getPlanification());
				sendPrisePoste();
			}
		} else {
			debug("Pas de prise de poste au préalable"); //$NON-NLS-1$
			_model.setAuthentification(aut);
			_planificationDatasource.checkCurrentMatricule(_model
					.getAuthentification().getMatricule());
			_model.setPlanification(_planificationDatasource.getPlanification());
		}
		_login = true;
		stopGestionDefautPrisePoste();
		_model.setError(null);
		synchronize("login"); //$NON-NLS-1$
	}

	public void logout() {// String matricule) {
		if (!_login)
			return;
		_login = false;
		startGestionDefautPrisePoste();
		finPoste();
		if (_session != null) {
			_session.logout();
		}
	}

	/**
	 * PRISE DE POSTE
	 */

	public void prisePoste(int replaceVehicule, int replaceMatricule) {
		try {
			_fsm.prisePoste(replaceVehicule, replaceMatricule);
		} catch (RuntimeException e) {
			_log.error("Erreur 'Prise de poste'", e); //$NON-NLS-1$
		}
	}

	public void actionPrisePoste(int replaceVehicule, int replaceMatricule) {
		try {
			_log.info("action prise poste begin"); //$NON-NLS-1$
			if (_model.getAuthentification() != null) {
				_model.getAuthentification().setMatriculeRemplace(
						replaceMatricule);
				_model.getAuthentification().setVehiculeRemplace(
						replaceVehicule);
			}
			_log.info("action prise poste send pp"); //$NON-NLS-1$
			sendPrisePoste();
			_log.info("action prise poste end"); //$NON-NLS-1$

		} catch (RuntimeException e) {
			_log.error("Error on action prise de poste", e); //$NON-NLS-1$
		}
	}

	private void sendPrisePoste() {
		try {
			_currentDatasource = _databaseDatasource;
			_model.setDatasource(_databaseDatasource);// pour que la version
			// envoyee soit celle de
			// database
			PrisePoste priseposte = new PrisePoste();
			priseposte.getEntete().getChamps().setPosition(1);
			if (_model.getPlanification().isCorrect()) {
				priseposte.setPlanification(_model.getPlanification().getId());
				priseposte.setChecksum(_planificationDatasource.getChecksum());
			} else {
				priseposte.setPlanification(0);
				priseposte.setChecksum(0);
			}

			if (_model.getAuthentification() != null) {
				priseposte.setConducteurRemplacement(_model
						.getAuthentification().getMatriculeRemplace());
				priseposte.setVehiculeRemplacement(_model.getAuthentification()
						.getVehiculeRemplace());
			} else {
				priseposte.setConducteurRemplacement(0);
				priseposte.setConducteurRemplacement(0);
			}

			initService(priseposte.getEntete().getService());
			sendMessage(priseposte);
			_model.getAuthentification().setPrisePosteDone(true);

			StringBuffer buf = new StringBuffer();
			buf.append("PRISEPOSTE;"); //$NON-NLS-1$
			buf.append(_model.getDatasourceVersion());
			buf.append(";"); //$NON-NLS-1$
			buf.append(_model.getAuthentification().getMatricule());
			buf.append(";"); //$NON-NLS-1$
			buf.append(_model.getAuthentification().getNom());
			buf.append(" "); //$NON-NLS-1$
			buf.append(_model.getAuthentification().getPrenom());
			journalize(buf.toString());

			attentePlanification();
		} catch (Throwable t) {
			_log.error("sendPrisePoste");
			_log.error(t);
			t.printStackTrace();
		}
	}

	/**
	 * PRISE DE SERVICE
	 */

	public void priseService(int service) {
		try {
			_fsm.priseService(service);
		} catch (RuntimeException e) {
			_log.error("Error 'prise de service'", e); //$NON-NLS-1$
		}
	}

	public boolean isServiceSpecial(int service) {
		if (service == ServiceAgent.SERVICE_OCCASIONNEL
				|| service == ServiceAgent.SERVICE_KM_A_VIDE) {
			_model.reset();
			_model.setServiceAgent(new ServiceAgent(true, service, null));
			return true;
		}
		return false;
	}

	public boolean isServiceNormal(int sag_idu) {

		if (_currentDatasource == null) {
			_model.setError(AvmDatabaseException
					.getError(AvmDatabaseException.ERR_MODE_PLANIFICATION_SANS_BASE));
		} else if (sag_idu != -1) {
			_model.reset();
			try {
				long t0 = System.currentTimeMillis();

				_currentDatasource.setCheckValidite(_config.isCheckValidite());
				ServiceAgent sa = _currentDatasource.getServiceAgent(sag_idu);
				if (_log.isDebugEnabled()) {
					debug("DELTA = " + (System.currentTimeMillis() - t0));
					debug("sa " + sa); //$NON-NLS-1$
				}
				_model.setServiceAgent(sa);
				_model.setError(null);
			} catch (AvmDatabaseException ade) {
				_model.setError(ade.getError()); //$NON-NLS-1$
			} catch (Throwable t) {
				_model.setError(AvmDatabaseException
						.getError(AvmDatabaseException.ERR_BASE_INTROUVABLE)); //$NON-NLS-1$
			}

		}
		boolean result = _model.getServiceAgent() != null
				&& _model.getServiceAgent().isCorrect();
		debug("isServiceNormal " + result); //$NON-NLS-1$
		return result;
	}

	public void actionService(boolean correct, int service) {
		if (correct == false) {
			debug("#action service : FAILED (" + service + ")#"); //$NON-NLS-1$ //$NON-NLS-2$
			synchronize("action service"); //$NON-NLS-1$
		} else {
			_model.setError(null);
			debug("#action service : OK (" + service + ")#"); //$NON-NLS-1$ //$NON-NLS-2$
			cancelTimer();
			_flagSendPriseService = true;
			journalize("SERVICE;" + service); //$NON-NLS-1$
		}
	}

	/**
	 * PRISE DE COURSE
	 */

	public void priseCourse(int course) {
		_model.setCourse(null);
		try {
			_fsm.priseCourse(course);
		} catch (RuntimeException e) {
			_log.error("Erreur 'Prise de course'", e); //$NON-NLS-1$
		}
	}

	public boolean isCourseCorrect(int courseIDU) {
		if (courseIDU != -1 && _currentDatasource != null) {
			try {

				_model.setError(null);
				ServiceAgent currentSa = _model.getServiceAgent();
				_log.info("isCourseCorrect  - currentSA=" + currentSa);
				_log.info("datasource = " + _currentDatasource);
				Course course = _currentDatasource.getCourse(currentSa,
						courseIDU);
				_log.info("getcourse = " + course);
				if (course != null) {
					course.setTerminee(false);
				}
				// _model.getServiceAgent().getCourseByIdu(course.getIdu())
				// .setTerminee(false);
				_model.setCourse(course);
			} catch (Throwable t) {
				_log.error(t);
				t.printStackTrace();
				_model.setError(AvmDatabaseException
						.getError(AvmDatabaseException.ERR_BASE_INTROUVABLE)); //$NON-NLS-1$
			}

		}
		return (_model.getCourse() != null);
	}

	public void actionCourse(boolean correct, int course) {
		if (correct == false) {
			debug("#action course : FAILED (" + course + ")#"); //$NON-NLS-1$ //$NON-NLS-2$
			synchronize("action course"); //$NON-NLS-1$
		} else {
			debug("#action course : OK (" + course + ")#"); //$NON-NLS-1$ //$NON-NLS-2$
			if (_flagSendPriseService == true) {
				_log.debug("Prepare to send message prise-service... ");
				_flagSendPriseService = false;

				PriseService priseService = new PriseService();
				priseService.getEntete().getChamps().setPosition(1);
				initService(priseService.getEntete().getService());
				sendMessage(priseService);
				try {
					//-- pour éviter d'envoyer en même temps prise de service et prise de course
					//-- (pas le temps de faire mieux!!!)
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				if (_log.isDebugEnabled()) {
					_log.debug("Message prise-service sent : " + priseService);
				}
			}
			DepartCourse departCourse = new DepartCourse();
			departCourse.getEntete().getChamps().setPosition(1);
			initService(departCourse.getEntete().getService());
			sendMessage(departCourse);
			if (_log.isDebugEnabled()) {
				_log.debug("Message depart-course sent : " + departCourse);
			}
			journalize("COURSE;" + course + ";" + getModel().getCourse().getLigneIdu() + ";" + getModel().getCourse().getParcoursIdu()); //$NON-NLS-1$
		}
	}

	private void checkIfVehiculeIsAtTerminusDepart() {
		Point terminusDepart = _model.getCourse().getTerminusDepart();
		if (terminusDepart == null) {
			// TSE
			return;
		}
		boolean inside = isInsideBalise(terminusDepart.getId());
		debug("Depart de la course : " + terminusDepart);
		debug("Sommes nous au terminus : " + inside);

		if (terminusDepart.isDesservi() == false && inside) {
			// -- entree balise terminus depart avant la prise de service
			// (l'entree
			// -- a donc ete ignoree)
			_log.debug("la prise de course a ete faite a l'arret : forcage entree");
			entree(terminusDepart.getId());
		} else {
			_log.debug("la prise de course a ete faite avant l'arrivee au terminus : ok");
		}
	}

	/**
	 * DEPART
	 * 
	 */

	public void depart() {
		try {
			int balise = -1;
			if (_odometer != null) {
				journalize("HLP;" + getOdometerValue());
				resetOdometer();
			}
			if (_geofencing != null) {
				Set zone = _geofencing.getZone();
				if (zone != null) {
					for (int i = 1; i <= _model.getCourse().getNombrePoint(); i++) {
						Iterator iter = zone.iterator();
						Point p = _model.getCourse().getPointAvecRang(i);
						while (iter.hasNext()) {
							Balise b = (Balise) iter.next();
							if (b.getId() == p.getId()) {
								balise = b.getId();
								_log.info("Depart depuis l'arret : " + p);
								break;
							}
						}
						if (balise != -1) {
							break;
						}
					}
				}
			}
			_log.info("Depart depuis balise : " + balise);

			_fsm.depart(balise);
		} catch (RuntimeException e) {
			_log.error("Erreur 'Depart'", e); //$NON-NLS-1$
		}
	}

	public void actionDepart() {
		debug("#action depart"); //$NON-NLS-1$
		if (_model.isDepart() == false) {
			_model.setDepart(true);
			// DepartCourse departCourse = new DepartCourse();
			// departCourse.getEntete().getChamps().setPosition(1);
			// initService(departCourse.getEntete().getService());
			// sendMessage(departCourse);
		}
	}

	/**
	 * 
	 * FIN COURSE
	 */

	public void finCourse() {
		try {
			_fsm.finCourse();
		} catch (RuntimeException e) {
			_log.error("Erreur 'Fin de course'", e); //$NON-NLS-1$
		}
	}

	public void actionFinCourse() {
		debug("#action fin course#"); //$NON-NLS-1$

		if (_model.getCourse() != null) {
			FinCourse finCourse = new FinCourse();
			initService(finCourse.getEntete().getService());
			finCourse.getEntete().getChamps().setPosition(1);
			sendMessage(finCourse);

			if (_log.isDebugEnabled()) {
				debug("Model=" + _model); //$NON-NLS-1$
			}
			journalize("FINCOURSE;" + _model.getCourse().getIdu() + ";" + getOdometerValue()); //$NON-NLS-1$ //$NON-NLS-2$
			resetOdometer();
			syncJdb();

			_model.getServiceAgent()
					.getCourseByIdu(_model.getCourse().getIdu())
					.setTerminee(true);
			_model.getCourse().setTerminee(true);
			_model.setGeorefMode(false);
			_model.setVehiculeFull(false);
			_model.setHorsItineraire(false);
			notifyAlarmDeviation(false);
			_model.setDepart(false);

			// FLA ajout de la sauvegarde dernier idu de course
			_model.setLastCourseIdu(_model.getCourse().getIdu());

		}
		_model.setDepart(false);
		_model.setCourse(null);
	}

	/**
	 * FIN SERVICE
	 */

	public void finService() {
		try {
			_fsm.finService();
		} catch (RuntimeException e) {
			_log.error("Error 'fin de service'", e); //$NON-NLS-1$
		}
	}

	public void actionFinService() {
		debug("#action fin de service#"); //$NON-NLS-1$
		cancelTimer();
		if (_model.getServiceAgent() != null) {
			FinPriseService finService = new FinPriseService();
			finService.getEntete().getChamps().setPosition(1);
			initService(finService.getEntete().getService());
			sendMessage(finService);
			journalize("FINSERVICE;" + _model.getServiceAgent().getIdU()); //$NON-NLS-1$
			_model.getServiceAgent().setTermine(true);
			_model.setServiceAgent(null);
		}
		checkPlanification();
		syncJdb();
	}

	/**
	 * FIN POSTE
	 */

	public void finPoste() {
		try {
			_fsm.finPoste();
		} catch (RuntimeException e) {
			_log.error("Erreur 'Fin de poste'", e); //$NON-NLS-1$
		}

	}

	public void actionFinPoste() {
		StringBuffer buf = new StringBuffer();
		buf.append("FINPOSTE;"); //$NON-NLS-1$
		buf.append(_model.getAuthentification() == null ? 0 : _model
				.getAuthentification().getMatricule());
		journalize(buf.toString());
		FinPrisePoste finPrisePoste = new FinPrisePoste();
		finPrisePoste.getEntete().getChamps().setPosition(1);
		initService(finPrisePoste.getEntete().getService());
		_model.getPlanification().confirm(true);
		sendMessage(finPrisePoste);
		_model.setAuthentification(null);
	}

	/**
	 * GESTION PLANIFICATION
	 */

	/**
	 * force la prise de service si la planification est valide.
	 */
	public void checkForcePriseService() {
		if (_timerAttentePlanification != null) {
			// si attente de reception de planification alors
			// ne pas provoquer la prise de service.
			return;
		}
		boolean planifMustBeClosed = _model.getPlanification().isCorrect();
		if (_model.getPlanification().isCorrect()
				&& _model.getAuthentification().isPrisePoste()
				&& _currentDatasource == _planificationDatasource) {
			debug("Prise service automatique"); //$NON-NLS-1$
			int sag_idu = _model.getPlanification().getServiceIdu();
			ServiceAgent sa;
			try {
				sa = _currentDatasource.getServiceAgent(sag_idu);
				sa.setAutomaticLabel(_config.getAutomaticSALabel());
				debug("service planifie " + sag_idu + " termine ? => "
						+ sa.isTermine());
				if (sa.isTermine() == false) {
					planifMustBeClosed = false;
					if (_model.getState().getValue() == AvmModel.STATE_ATTENTE_SAISIE_SERVICE) {
						priseService(sag_idu);
					}
				}
			} catch (AvmDatabaseException e) {
				e.printStackTrace();
			}
		}
		if (planifMustBeClosed) {
			_log.info("Planification must be closed => datasource = database");
			_model.getPlanification().confirm(false);
			_currentDatasource = _databaseDatasource;
			if (_model.getState().getValue() == AvmModel.STATE_ATTENTE_SAISIE_SERVICE
					&& (_databaseDatasource == null || _databaseDatasource
							.getServicesAgent().size() == 0)) {
				_model.setError(AvmDatabaseException
						.getError(AvmDatabaseException.ERR_MODE_PLANIFICATION_SANS_BASE));
				synchronize("Pas de service disponible, donc fin de poste");
				debug("attente saisie service et (pas de database ou pas de sag) : logout !");
				logout();

			}
		}

	}

	/**
	 * Controle la validité de la planification et utilise le 'datasource'
	 * correct (datasource represente l'entite grace a laquelle les donnees
	 * service/courses/points seront recuperees : soit depuis la base de donnees
	 * 'databaseDatasource', soit depuis la planification
	 * 'planificationDatasource' ou les deux lorsque un service theorique est
	 * planifie (dans ce cas l'embarque "a connaissance" des courses ).
	 */
	public void checkPlanification() {
		if (_flagCheckPlanification) {
			debug("<<Check Planification"); //$NON-NLS-1$
			debug("Planification: " + _model.getPlanification()); //$NON-NLS-1$
			if (_timerAttentePlanification != null) {
				// si attente de reception de planification alors
				// ne rien faire.
				return;
			}
			if (_model.getPlanification().isCorrect()) {
				_currentDatasource = _planificationDatasource;
				debug("Datasource = planification"); //$NON-NLS-1$
				int sag_idu = _model.getPlanification().getServiceIdu();
				ServiceAgent sag = _planificationDatasource
						.getServiceAgent(sag_idu);
				debug("Service Agent planifie'=" + sag); //$NON-NLS-1$
				_model.setServiceAgent(sag);
			} else {
				_currentDatasource = _databaseDatasource;
				debug("Datasource = database"); //$NON-NLS-1$
			}

			_model.setDatasource(_currentDatasource);
			debug("end Check>>"); //$NON-NLS-1$
			//--DLA synchronize("check planification"); //$NON-NLS-1$
			_flagCheckPlanification = false;
		} else {
			debug("Planification not checked (not modified)"); //$NON-NLS-1$
		}
	}

	private void cancelTimer() {
		if (_timerAttentePlanification != null) {
			debug("Annulation Attente Planification"); //$NON-NLS-1$
			_model.getPlanification().confirm(true);
			_timerAttentePlanification.cancel();
			_timerAttentePlanification = null;
		}
	}

	public void attentePlanification() {
		synchronize("attente planification"); //$NON-NLS-1$
		cancelTimer();
		_model.getPlanification().confirm(false);
		_timerAttentePlanification = new Timer();
		_timerAttentePlanification.schedule(new PlanificationTimerTask(),
				10 * 1000);
	}

	public class PlanificationTimerTask extends TimerTask {
		public void run() {
			debug("Planification timer task");
			_timerAttentePlanification = null;
			_model.getPlanification().confirm(true);
			debug("Planification:" + _model.getPlanification()); //$NON-NLS-1$
			checkPlanification();
			checkForcePriseService();
			synchronize("fin attente planification (timertask)"); //$NON-NLS-1$
		}
	}

	/**
	 * ACTION ANNULER
	 */

	public void annuler() {
		try {
			_fsm.annuler();
		} catch (RuntimeException e) {
			_log.error("Erreur 'annuler'", e); //$NON-NLS-1$
		}
	}

	public void actionAnnuler() {
		debug("#action annuler#"); //$NON-NLS-1$
	}

	/**
	 * SORTIE ITINERAIRE
	 */

	public void sortieItineraire() {
		try {
			_fsm.sortieItineraire();
		} catch (RuntimeException e) {
			_log.error("Erreur 'sortie itineraire'", e); //$NON-NLS-1$
		}
	}

	public void exitHorsItineraire() {
		_model.setHorsItineraire(false);
		notifyAlarmDeviation(false);
	}

	public void entryHorsItineraire() {
		_model.setHorsItineraire(true);
		if (_model.getDernierPoint() != null) {
			// -- notification seulement si on a quitté le premier arrêt
			notifyAlarmDeviation(true);
		}
	}

	public void actionSortieItineraire() {
		debug("#action sortie itineraire#"); //$NON-NLS-1$
		Deviation deviation = new Deviation();
		deviation.getEntete().getChamps().setPosition(1);
		deviation.getEntete().getChamps().setProgression(1);
		initService(deviation.getEntete().getService());
		sendMessage(deviation);
		journalize("HORSITINERAIRE"); //$NON-NLS-1$
		syncJdb();
	}

	private void notifyAlarmDeviation(boolean state) {
		boolean changed = false;
		if (alarmDeviation == null) {
			alarmDeviation = new Alarm(new Integer(ALARM_DEVIATION_INDEX));
			changed = true;
		} else {
			changed = (alarmDeviation.isStatus() != state);
		}

		if (changed) {
			alarmDeviation.setStatus(state);
			_producer.publish(alarmDeviation);
		}
	}

	/**
	 * MODE LABO
	 */
	public void setGeorefMode(boolean mode) {
		_model.setGeorefMode(mode);
	}

	public boolean isGeorefMode() {
		return _model.isGeorefMode();
	}

	// model

	public void entryEnCourseServiceSpecial() {
		debug("#entryEnCourseServiceSpecial#"); //$NON-NLS-1$
		// Ajout LBR
		DepartCourse departCourse = new DepartCourse();
		departCourse.getEntete().getChamps().setPosition(1);
		initService(departCourse.getEntete().getService());
		sendMessage(departCourse);
		debug("#entryEnCourseServiceSpecial#"); //$NON-NLS-1$
	}

	private void initService(Service service) {
		if (service != null) {
			int conducteur = (_model.getAuthentification() != null) ? _model
					.getAuthentification().getMatricule() : 0;
			service.setConducteur(conducteur);

			int course = (_model.getCourse() != null) ? _model.getCourse()
					.getIdu() : 0;
			service.setCourse(course);

			int serviceAgent = (_model.getServiceAgent() != null) ? _model
					.getServiceAgent().getIdU() : 0;
			service.setServiceAgent(serviceAgent);
		}
	}

	public void synchronize(String reason) {
		// if (_lastState != _model.getState().getValue()) {
		_log.debug("Synchronize IHM (" + reason + ": new state = " + _model.getState()); //$NON-NLS-1$ //$NON-NLS-2$
		_producer.publish(_model.getState());
		_log.info("new state published.");
		// _lastState = _model.getState().getValue();
		// }
	}

	public void sendMessage(Message msg) {
		if (_model.getAuthentification() != null
				&& _model.getAuthentification().isDemoRole()) {
			_log.info("No message send for user with role 'demo'");
			return;
		}
		if (_messenger != null) {
			Hashtable d = new Hashtable();
			d.put("destination", "sam"); //$NON-NLS-1$ //$NON-NLS-2$
			d.put("binary", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				_log.debug("Sending message" + msg);
				_messenger.send(d, msg);
			} catch (Exception e) {
				_log.error("Error sendMessage", e); //$NON-NLS-1$
				_log.error(e); //$NON-NLS-1$
			}
		} else {
			_log.warn("Messenger is null ; cannot send " + msg);
		}
	}

	public List getAlarm() {
		if (_defautPrisePosteService != null
				&& _defautPrisePosteService.getAlarm().isStatus()) {
			LinkedList list = new LinkedList();
			list.add(_defautPrisePosteService.getAlarm());
			return list;
		} else {
			return null;
		}
	}

	public String getProducerPID() {
		return Avm.class.getName();
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

	private void syncJdb() {
		if (_jdb != null) {
			try {
				_jdb.sync();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public void stateChange(int state) {
		_model.setState(state);

		debug("stateChanged"); //$NON-NLS-1$
		synchronize("nouvel etat"); //$NON-NLS-1$
	}

	public AvmModel getModel() {
		return _model;
	}

	public void entryEnPanne() {
		// TODO Auto-generated method stub
	}

	public void notifyProgression(int pourcentage) {
		// TODO Auto-generated method stub
	}

	public void resetService() {
		_model.setServiceAgent(null);
	}

	public void resetCourse() {
		_model.setCourse(null);
		_model.setInsidePoint(false);
		_model.setAvanceRetard(0);
		_model.setDernierPoint(null);
	}

	public void startGestionDefautPrisePoste() {
		_log.info("start defaut prise de poste...");
		_defautPrisePosteService.start();
	}

	public void stopGestionDefautPrisePoste() {
		_log.info("stop defaut prise de poste.");
		_defautPrisePosteService.stop();
	}

	public void startSuiviItineraire() {
		_suiviItineraire.start();
		checkIfVehiculeIsAtTerminusDepart();
	}

	public void stopSuiviItineraire() {
		_suiviItineraire.stop();
	}

	public void checkSuiviItineraire() {
		if (_suiviItineraire.isRunning() == false
				&& _model.getState().getValue() >= AvmModel.STATE_ATTENTE_DEPART) {
			_suiviItineraire.start();
		}
	}

	public boolean isArret(int balise) {
		boolean result = _suiviItineraire.isArret(balise);
		debug(balise + " is arret : " + result); //$NON-NLS-1$
		return result;
	}

	public boolean isProchainArret(int balise) {
		boolean result = _suiviItineraire.isProchainArret(balise);
		debug(balise + " is prochain arret : " + result); //$NON-NLS-1$
		return result;
	}

	public boolean isTerminusDepart(int balise) {
		boolean result = _suiviItineraire.isTerminusDepart(balise);
		debug(balise + " is terminus depart : " + result); //$NON-NLS-1$
		return result;
	}

	public boolean isTerminusArrivee(int balise) {
		boolean result = _suiviItineraire.isTerminusArrivee(balise);
		debug(balise + " is terminus arrivee : " + result); //$NON-NLS-1$
		return result;
	}

	public boolean isArretCourant(int balise) {
		boolean result = _suiviItineraire.isArretCourant(balise);
		debug(balise + " is arret courant : " + result); //$NON-NLS-1$
		return result;
	}

	public void entree(int balise) {
		try {
			_fsm.entree(balise);
		} catch (RuntimeException e) {
			_log.error("Erreur 'entree balise [" + balise + "]'"); //$NON-NLS-1$
			_log.error("Erreur 'entree balise [" + balise + "]'", e);
			_log.error(e);
		}
	}

	public void sortie(int balise) {
		_log.info("Current state:" + _fsm.getState());
		try {
			_fsm.sortie(balise);
		} catch (RuntimeException e) {
			_log.error("Erreur 'sortie balise [" + balise + "]'"); //$NON-NLS-1$
			_log.error("Erreur 'sortie balise [" + balise + "]'", e);
			_log.error(e);

		}
	}

	public void actionFinHorsItineraire(int balise) {
		_suiviItineraire.entree(balise, true);
	}
	
	public void actionEntreeArret(int balise) {
		_suiviItineraire.entree(balise);
	}

	public void actionSortieArret(int balise) {
		_suiviItineraire.sortie(balise);
	}

	public void actionSortieArret() {
		if (_model.getDernierPoint() != null) {
			_suiviItineraire.sortie(_model.getDernierPoint().getId());
		}
	}

	/**
	 * SERIALISATION DE AVM
	 */

	public void serialize() {
		ObjectOutputStream ostream = null;
		try {
			debug("avm serialisation begin"); //$NON-NLS-1$
			long t0 = System.currentTimeMillis();
			FileOutputStream out = new FileOutputStream(AVM_SERIALIZATION_FILE);
			ostream = new ObjectOutputStream(new BufferedOutputStream(out));
			ostream.writeObject(AvmImpl.this);
			_log.debug("avm serialisation begin flush (" + (System.currentTimeMillis() - t0) / 1000 + "s)"); //$NON-NLS-1$ //$NON-NLS-2$
			ostream.flush();
			_log.debug("avm serialisation end flush (" + (System.currentTimeMillis() - t0) / 1000 + "s)"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			_log.error("Error serialization", e); //$NON-NLS-1$
		} finally {
			try {
				ostream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Utilise' pour la deserialisation de AVM
	 * 
	 * @param istream
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream istream)
			throws java.io.IOException, ClassNotFoundException {
		// Do the default read first which sets _fsm to null.
		istream.defaultReadObject();
		// Now set the FSM's owner.
		_fsm.setOwner(this);
		return;
	}

	private static boolean isOlderThanToday(File file) {
		Date modifiedDate = new Date(file.lastModified());
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		Date today = cal.getTime();
		return modifiedDate.before(today);
	}

	public static AvmImpl deserialize() throws StreamCorruptedException,
			FileNotFoundException, IOException, ClassNotFoundException {
		AvmImpl appInstance = null;
		ObjectInputStream istream = null;
		File file = null;

		file = new File(AVM_SERIALIZATION_FILE);
		if (file.exists() == false || isOlderThanToday(file)) {
			info("Fichier serialis� trop vieux : ignor� (et d�truit)");
			file.delete();
			return new AvmImpl();
		}

		try {
			istream = new ObjectInputStream(new FileInputStream(
					AVM_SERIALIZATION_FILE));

			appInstance = (AvmImpl) istream.readObject();
			appInstance.init();
			info("Fichier serialis� charg�");

		} catch (Exception t) {
			info("Destruction du fichier s�rialis�"); //$NON-NLS-1$
			file.delete();
		} finally {
			istream.close();
		}
		return appInstance;
	}

	private static void info(String string) {
		System.out.println("[AvmCore] " + string);
	}

	public static AvmImpl getInstance() {
		AvmImpl avm = null;
		try {
			avm = deserialize();
		} catch (Exception e) {
			info("Erreur de d�serialisation"); //$NON-NLS-1$
			e.printStackTrace();
			avm = null;
		}
		if (avm == null) {
			avm = new AvmImpl();
		}
		return avm;
	}

	public void checkCourse() {
		if (_model.getServiceAgent() != null) {
			ServiceAgent sa = _model.getServiceAgent();
			if (sa.isTermine()) {
				debug("Controle pour fin service : derniere course terminee => FinService automatique !");
				finService();
			} else {
				debug("Controle pour fin service : derniere course non terminee	 (pas de FinService)");
			}
		}
	}

	public void setGeoFencing(GeoFencing geofencing) {
		_geofencing = geofencing;
	}

	public void unsetGeoFencing(GeoFencing geofencing) {
		_geofencing = geofencing;
	}

	private boolean isInsideBalise(int balise) {
		boolean result = false;

		if (_geofencing != null) {
			Set zone = _geofencing.getZone();
			if (zone != null) {
				Iterator iter = zone.iterator();
				debug("Somme nous dans la balise : " + balise);
				while (iter.hasNext() && result == false) {
					Balise b = (Balise) iter.next();
					result = (b.getId() == balise);
				}
			}

		}

		return result;
	}

	public void setGeorefRole(boolean b) {
		if (_model.getAuthentification() != null) {
			_model.getAuthentification().setGeorefRole(b);
		}
	}

	private void setDemoRole(boolean b) {
		if (_model.getAuthentification() != null) {
			_model.getAuthentification().setDemoRole(b);
		}
	}

	public void loggedOn() {
		if (_session != null) {
			if (_session.hasRole("conducteur")) {
				String matricule = (String) _session.getUserProperties().get(
						UserSessionService.MATRICULE);
				String nom = (String) _session.getUserProperties().get(
						UserSessionService.NOM);
				String prenom = (String) _session.getUserProperties().get(
						UserSessionService.PRENOM);
				_log.info("login: matricule=" + matricule + " , nom=" + nom
						+ " " + prenom);
				login(matricule, nom, prenom);
			}
			setGeorefRole(_session.hasRole("georef"));
			setDemoRole(_session.hasRole("demo"));
		}
	}

	public void loggedOut() {
		logout();// matricule);
		setGeorefRole(false);
	}

	public void setUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void unsetUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void setAlarmService(AlarmService service) {
		_alarmService = service;

	}

	public void unsetAlarmService(AlarmService service) {
		_alarmService = service;
	}

	public void showMessage() {
		synchronize("Message conducteur");
		debug("Error=" + _model.getLastError());
	}

	public void setVehiculeFull(boolean b) {
		_model.setVehiculeFull(b);
		synchronize("vehicule complet");
	}

	private void debug(Object debug) {
		if (_log.isDebugEnabled() && debug != null) {
			_log.debug(debug.toString());
		}
	}

	public void checkAutomaticCourse() {
		System.out.println("auto: " + _config.isAutomaticCourseMode());
		System.out.println("sa: " + _model.getServiceAgent());

		if (_config.isAutomaticCourseMode() && _model.getServiceAgent() != null) {
			ServiceAgent sa = _model.getServiceAgent();
			System.out.println("sa auto " + sa.isAutomaticCourse());
			System.out.println("sa lib " + sa.getLibelle());
			System.out.println("sa lab " + sa.getAutomaticLabel());
			if (sa != null && sa.isAutomaticCourse()) {
				int idu = _model.getLastCourseIdu();
				System.out.println("idu " + idu);
				if (idu != -1) {
					Course nextCourse = sa.getNextCourse(idu);
					resetCourse();
					priseCourse(nextCourse.getIdu());
				}
			}
		}
	}

}
