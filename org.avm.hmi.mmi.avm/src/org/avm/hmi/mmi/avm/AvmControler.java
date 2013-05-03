package org.avm.hmi.mmi.avm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.business.core.AbstractAvmModelListener;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Authentification;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.business.protocol.phoebus.MessageText;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.variable.Variable;
import org.avm.hmi.mmi.application.actions.Keys;
import org.avm.hmi.mmi.application.actions.ProcessCustomizer;
import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.osgi.util.measurement.Measurement;

public class AvmControler extends AbstractAvmModelListener implements
		ProcessCustomizer, AvmMmi, ConsumerService, ManageableService {

	private AvmView _avmView;

	private AVMDisplay _base;

	private Logger _log;

	private Variable _beeper;

	private boolean _stopAttenteDepart = false;

	private Thread _threadAttenteDep;

	private boolean _isMainteneur = false;

	// private boolean _started = false;

	public AvmControler() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
	}

	public void start() {
		_log.debug("starting AvmControler");
		_avmView = AvmView.createInstance(_base);
		addProcess();
		_base.stopAttente();
		synchronize();
		// _started = true;
	}

	public void setBeeper(Variable var) {
		_beeper = var;
	}

	public void synchronize() {
		_log.debug("synchronize()...");
		if (_avm != null) {
			notify(_avm.getModel().getState());
			_log.debug("... OK");
		}
	}

	public void stop() {
		_log.debug("stopping AvmControler");
		// _started = false;
	}

	public void isMainteneur() {
		_isMainteneur = true;
	}

	/*
	 * States.ETAT_CHOIX_ACT devient
	 * AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE States.ETAT_SEG devient
	 * AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE States.ETAT_DECOMPTE_T
	 * devient AvmModel.STRING_STATE_ATTENTE_DEPART States.ETAT_COURSE_SEG_DA
	 * devient AvmModel.STRING_STATE_EN_COURSE_SUR_ITINERAIRE
	 * States.ETAT_COURSE_SEG devient
	 * AvmModel.STRING_STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE States.ETAT_HS
	 * devient AvmModel.STRING_STATE_EN_PANNE States.ETAT_COURSE_KM devient
	 * AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL States.ETAT_COURSE_SO
	 * devient AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL
	 * States.ETAT_VALID_HS devient "VALID_"+AvmModel.STRING_STATE_EN_PANNE
	 * States.ETAT_VALID_SO devient
	 * "VALID_"+AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL
	 * States.ETAT_VALID_KM devient
	 * "VALID_"+AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL
	 * States.ETAT_VALID_SEG devient
	 * "VALID_"+"VALID_"+AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE
	 * States.ETAT_VALID_FIN_SEG devient
	 * "VALID_FIN_"+AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE
	 * States.ETAT_VALID_FIN_HS devient
	 * "VALID_FIN_"+AvmModel.STRING_STATE_EN_PANNE States.ETAT_VALID_FIN_SO
	 * devient "VALID_FIN_"+AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL
	 * States.ETAT_VALID_FIN_KM devient
	 * "VALID_FIN_"+AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL States.ALL
	 * devient "ALL_STATES"
	 */
	public void addProcess() {
		// Choix activite
		_base.setProcess(Keys.KEY_SEG,
				AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE,
				new OnSelectionService(_avmView));
		_base.setProcess(Keys.KEY_SO,
				AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE,
				new OnSelectionServiceSpec(_avmView,
						ServiceAgent.SERVICE_OCCASIONNEL));
		_base.setProcess(Keys.KEY_KM,
				AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE,
				new OnSelectionServiceSpec(_avmView,
						ServiceAgent.SERVICE_KM_A_VIDE));

		// validation du choix
		_base.setProcess(Keys.KEY_V,
				AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE,
				new OnValidationNoSeg(_avm, true, _avmView));
		_base.setProcess(Keys.KEY_V, "VALID_"
				+ AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE,
				new OnValidationSeg(_avm, true, _avmView));
		_base.setProcess(Keys.KEY_V, "VALID_" + AvmModel.STRING_STATE_EN_PANNE,
				new OnValidationPanne(_avm, true, _avmView));
		_base.setProcess(Keys.KEY_V, "VALID_SO_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnValidationServSpec(_avm, true,
						ServiceAgent.SERVICE_OCCASIONNEL, _avmView));
		_base.setProcess(Keys.KEY_V, "VALID_FIN_SO_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnValidationFinServSpec(_avm, true, _avmView));
		_base.setProcess(Keys.KEY_V, "VALID_KM_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnValidationServSpec(_avm, true,
						ServiceAgent.SERVICE_KM_A_VIDE, _avmView));
		_base.setProcess(Keys.KEY_V, "VALID_FIN_KM_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnValidationFinServSpec(_avm, true, _avmView));
		_base.setProcess(Keys.KEY_V, "VALID_FIN_"
				+ AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
				new OnValidationFinSeg(_avm, true, _avmView));
		// _base.setProcess(Keys.KEY_V, "VALID_FIN_"+
		// AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE,new
		// OnValidationFinSeg(_avm, true, _avmView));
		_base.setProcess(Keys.KEY_V, AvmModel.STRING_STATE_EN_PANNE,
				new OnSelectionPanne(_avmView, this));

		// Saisie en cours de route
		_base.setProcess(Keys.KEY_V, "ALL_STATES", new OnValidationAllStates(
				_avm, true, _avmView));
		_base.setProcess(Keys.KEY_C, "ALL_STATES", new OnValidationAllStates(
				_avm, false, _avmView));
		_base.setProcess(Keys.KEY_V, "PROGRESSBAR",
				new OnValidationProgressBar(this));
		_base.setProcess(Keys.KEY_C, "PROGRESSBAR",
				new OnValidationProgressBar(this));

		// Gestion de la déviation et du cas ou le prochain arrêt n'est pas
		// géolocalisé
		// 1- on se met en déviation
		_base.setProcess(Keys.KEY_DEV,
				AvmModel.STRING_STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE,
				new OnSelectionDeviation(_avm, _avmView));
		_base.setProcess(Keys.KEY_DEV, "LABO_"
				+ AvmModel.STRING_STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE,
				new OnSelectionEntreeSortieArret(_avm, _avmView, true));
		// //2- on entre dans un point non géolocalisé
		// _base.setProcess(Keys.KEY_DEV,AvmModel.STRING_STATE_EN_COURSE_HORS_ITINERAIRE,new
		// OnSelectionEntreeSortieArret(_avm, _avmView, true));
		// 2- on sort d'un point non géolocalisé
		_base.setProcess(Keys.KEY_DEV,
				AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
				new OnSelectionEntreeSortieArret(_avm, _avmView, false));

		// Annulation du choix
		_base.setProcess(Keys.KEY_C,
				AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE,
				new OnValidationNoSeg(_avm, false, _avmView));
		_base.setProcess(Keys.KEY_C, "VALID_"
				+ AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE,
				new OnValidationSeg(_avm, false, _avmView));
		_base.setProcess(Keys.KEY_C, "VALID_" + AvmModel.STRING_STATE_EN_PANNE,
				new OnValidationPanne(_avm, false, _avmView));
		_base.setProcess(Keys.KEY_C, "VALID_SO_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnValidationServSpec(_avm, false,
						ServiceAgent.SERVICE_OCCASIONNEL, _avmView));
		_base.setProcess(Keys.KEY_C, "VALID_FIN_SO_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnValidationFinServSpec(_avm, false, _avmView));
		_base.setProcess(Keys.KEY_C, "VALID_KM_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnValidationServSpec(_avm, false,
						ServiceAgent.SERVICE_KM_A_VIDE, _avmView));
		_base.setProcess(Keys.KEY_C, "VALID_FIN_KM_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnValidationFinServSpec(_avm, false, _avmView));
		_base.setProcess(Keys.KEY_C, "VALID_FIN_"
				+ AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE,
				new OnValidationFinSeg(_avm, false, _avmView));
		_base.setProcess(Keys.KEY_C, "VALID_FIN_"
				+ AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
				new OnValidationFinSeg(_avm, false, _avmView));

		// Selection
		_base.setProcess(Keys.KEY_FIN_SEG,
				AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
				new OnSelectionFinServ(_avm, _avmView));
		_base.setProcess(Keys.KEY_FIN_HS, AvmModel.STRING_STATE_EN_PANNE,
				new OnSelectionFinPanne(_avm));
		_base.setProcess(Keys.KEY_FIN_SO, "SO_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnSelectionFinServSpec(_avmView,
						ServiceAgent.SERVICE_OCCASIONNEL));
		_base.setProcess(Keys.KEY_FIN_KM, "KM_"
				+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL,
				new OnSelectionFinServSpec(_avmView,
						ServiceAgent.SERVICE_KM_A_VIDE));
		_base
				.setProcess(Keys.KEY_BACK, "ALL_STATES", new OnSelectionBack(
						_avm));
	}

	public void setBase(AVMDisplay base) {
		_base = base;
	}

	public void stopAttenteDepart() {
		_stopAttenteDepart = true;
		if (_threadAttenteDep != null)
			_threadAttenteDep.interrupt();
		_threadAttenteDep = null;
	}

	public void onNotify(Object o) {
		if (o instanceof MessageText) {
			MessageText msg = (MessageText) o;

			if (msg == null) {
				_base.setMessage(null);
			} else {
				_base.setMessage(msg.getMessage());
			}
		}
	}

	public void onStateInitial(AvmModel model) {
		// _log.debug(model.getState());
		// _log.debug("launch barre");
		// si on a un LOGOUT, on revient à cet état avec une authentification
		// null
		if (model.getAuthentification() == null) {
			_log.debug("model.getAuthentification()==null");
			return;
		}
		if (model.getAuthentification().isPrisePoste() == false) {
			_log.debug("model.getAuthentification().isPrisePoste()==false");
			_base.startAttente();
			_avm.prisePoste(0, 0);
		}
	}

	public void onStateAttenteSaisieService(AvmModel model) {
		// _log.debug(model.getState());
		// Attendre que l'éventuelle planif soit arrivée ...
		if (!checkConditions(model)) {
			_log
					.debug("model.getPlanification().isConfirmed() est faux : return");
			_base.startAttente();
			return;
		}
		_log
				.debug("model.getPlanification().isConfirmed() est vrai : on continue");
		stopAttenteDepart();
		OnValidationSeg.initCourseIndex();
		ServiceAgent sa = model.getServiceAgent();
		// Si le sa a été émis pour indiquer que la SM vient d'entrer dans
		// l'état attente sa.
		if (sa == null) {
			// On attend un SA
			Authentification auth = model.getAuthentification();
			if (model.getLastError() != null) {
				_avmView.activateSaisieService(
						AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE, Messages
								.getString("avm.service")
								+ Messages.getString("avm.incorrect"));
			} else {
				String msg = Messages.getString("avm.bonjour")
						+ auth.getPrenom();
				_avmView.activateSaisieService(
						AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE, msg);
			}
		} else {
			if (!sa.isCorrect()) {
				_avmView.activateSaisieService(
						AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE, Messages
								.getString("avm.service")
								+ sa.getIdU()
								+ Messages.getString("avm.incorrect"));
			} else {
				// quand on vient d'annuler saisiecourse car le sa ne contient
				// pas de course
				_avmView
						.activateSaisieService(AvmModel.STRING_STATE_ATTENTE_SAISIE_SERVICE);
			}
		}
	}

	public void onStateAttenteSaisieCourse(AvmModel model) {
		// _log.debug(model.getState());
		stopAttenteDepart();
		if (!checkConditions(model)) {
			_log
					.debug("model.getPlanification().isConfirmed() est faux : return");
			_base.startAttente();
			return;
		}
		ServiceAgent sa = model.getServiceAgent();
		if (sa == null) {
			_log.info("sa == null : annuler");
			_avm.annuler();
			return;
		}
		if (sa.getNbCourse() == 0) {
			// pas de course dans le sa
			_log.info("sa.getNbCourse() == 0 : annuler");
			_avm.annuler();
			return;
		}
		int currentCourseIndex = OnValidationSeg.getCourseIndex();
		_log.debug("currentCourseIndex = " + currentCourseIndex);
		if (currentCourseIndex >= sa.getNbCourse()) {
			// On n'a plus de course dans le service : le service est fini.
			_log.info("sa.getNbCourse() = " + sa.getNbCourse() + " (index="
					+ currentCourseIndex + ") => annuler");
			_avm.finService();
			return;
		}
		// s'il nous reste des courses dans le service.
		Course course = sa.getCourseByRang(currentCourseIndex);
		if (course == null) {
			_log.info("sa.getCourseByRang(" + currentCourseIndex
					+ ") == null : annuler");
			_avm.annuler();
			return;
		}
		_avmView.activateValidCourse("VALID_"
				+ AvmModel.STRING_STATE_ATTENTE_SAISIE_COURSE, course.getNom(),
				course.getHeureDepart(), course.getDestination());
	}

	public void onStateAttenteDepart(AvmModel model) {
		// _log.debug(model.getState());
		if (!checkConditions(model)) {
			_log
					.debug("model.getPlanification().isConfirmed() est faux : return");
			_base.startAttente();
			return;
		}
		stopAttenteDepart();
		_stopAttenteDepart = false;
		_threadAttenteDep = new Thread(new Wait4Departure(model));
		_threadAttenteDep.start();
	}

	public void onStateEnCourseArretSurItineraire(AvmModel model) {
		// _log.debug(model.getState());
		// Point courant :
		if (!checkConditions(model)) {
			_log
					.debug("model.getPlanification().isConfirmed() est faux : return");
			_base.startAttente();
			return;
		}
		Point point = model.getDernierPoint();
		if (point == null) {
			// le point ne correspond pas a un arret
			// rien a faire ... pour le moment
			// _log.debug(arret + " ne correspond pas a un arret.");
			// //$NON-NLS-1$
			return;
		}
		String nomArret = point.getNom();
		String heureArret = point.getHeureArriveeTheorique();

		stopAttenteDepart();

		// Point suivant :
		Point nextpoint = model.getProchainPoint();
		if (nextpoint == null) {
			// le prochain point n'existe pas : dernier arrêt
			_log.debug("Dernier arret");
			nextpoint = new Point(0, 0, "Dernier Arrêt", 0, 0, 0);
			// _avmView.activateValidFinService("VALID_FIN_"+AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
			// model.getServiceAgent().toString());
		}
		String nomNextArret = nextpoint.getNom();
		String heureNextArret = nextpoint.getHeureArriveeTheorique();

		// le 11/01/2008 on souhaite pouvoir terminer une course à tout moment.
		// int rang = point.getRang();
		// int lastRg = model.getCourse().getTerminusArrive().getRang();
		// if ((rang >= lastRg - 1)) {
		// // Avant dernier arret
		// _log.debug("avant dernier arret");
		// _avmView.activateArret(
		// AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
		// nomArret, heureArret, nomNextArret, heureNextArret, true);
		// } else {
		// _log.debug("Arret entre le depart et l'avant dernier arret");
		// _avmView.activateArret(
		// AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE,
		// nomArret, heureArret, nomNextArret, heureNextArret, false);
		// }
		_avmView.activateArret(
				AvmModel.STRING_STATE_EN_COURSE_ARRET_SUR_ITINERAIRE, nomArret,
				heureArret, nomNextArret, heureNextArret);
	}

	public void onStateEnCourseHorsItineraire(AvmModel model) {
		// _log.debug(model.getState());
		if (!checkConditions(model)) {
			_log
					.debug("model.getPlanification().isConfirmed() est faux : return");
			_base.startAttente();
			return;
		}
		Point point = model.getProchainPoint();
		if (point == null) {
			// le point ne correspond pas a un arret
			// rien a faire ... pour le moment
			// _log.debug(arret + " ne correspond pas a un arret.");
			// //$NON-NLS-1$
			return;
		}
		stopAttenteDepart();
		_avmView.activateSuiviCourse(
				AvmModel.STRING_STATE_EN_COURSE_HORS_ITINERAIRE,
				point.getNom(), point.getHeureArriveeTheorique(), model
						.getAvanceRetard(), AvmView.DEVIE);
	}

	public void onStateEnCourseInterarretSurItineraire(AvmModel model) {
		// _log.debug(model.getState());
		if (!checkConditions(model)) {
			_log
					.debug("model.getPlanification().isConfirmed() est faux : return");
			_base.startAttente();
			return;
		}
		Point point = model.getProchainPoint();
		if (point == null) {
			// le point ne correspond pas a un arret
			// rien a faire ... pour le moment
			// _log.debug(arret + " ne correspond pas a un arret.");
			// //$NON-NLS-1$
			return;
		}
		stopAttenteDepart();
		_log.debug("getX() =" + point.getLongitude() + "getY() =" + point.getLatitude());
		if (point.isGeoref()) {
			_avmView.activateSuiviCourse(
					AvmModel.STRING_STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE,
					point.getNom(), point.getHeureArriveeTheorique(), model
							.getAvanceRetard(), AvmView.NORMAL);
		} else {
			_log.debug("le prochain point n'est pas geolocalise");
			// 21/01/2008 : Le client préfère être mis en déviation dans ce cas
			// _avmView.activateSuiviCourse(
			// "LABO_"+AvmModel.STRING_STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE,
			// point.getNom(), point.getHeureArriveeTheorique(),
			// model.getAvanceRetard(), AvmView.LABO);
			_avm.sortieItineraire();
		}
	}

	public void onStateEnCourseServiceSpecial(AvmModel model) {
		// _log.debug(model);
		if (!checkConditions(model)) {
			_log
					.debug("model.getPlanification().isConfirmed() est faux : return");
			_base.startAttente();
			return;
		}
		ServiceAgent sa = model.getServiceAgent();
		if (sa == null) {
			_avmView.refresh();
			return;
		}
		if (sa.getIdU() == ServiceAgent.SERVICE_KM_A_VIDE) {
			_avmView.activateSuiviCourseKM("KM_"
					+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL);
		}
		if (sa.getIdU() == ServiceAgent.SERVICE_OCCASIONNEL) {
			_avmView.activateSuiviCourseSO("SO_"
					+ AvmModel.STRING_STATE_EN_COURSE_SERVICE_SPECIAL);
		}
	}

	public void onStateEnCourseSurItineraire(AvmModel model) {
		if (!checkConditions(model)) {
			_log
					.debug("model.getPlanification().isConfirmed() est faux : return");
			_base.startAttente();
			return;
		}
		// _log.debug(model.getState());
		// _avmView.activateEnDeviation(AvmModel.STRING_STATE_EN_COURSE_SUR_ITINERAIRE,
		// false);
	}

	public void onStateEnPanne(AvmModel model) {
		// _log.debug(model.getState());
		stopAttenteDepart();
		_avmView.activatePanne(AvmModel.STRING_STATE_EN_PANNE);
		OnValidationSeg.initCourseIndex();
	}

	public void onStatePause(AvmModel model) {
		// _log.debug(model.getState());
	}

	private boolean checkConditions(AvmModel model) {
		if (!model.getPlanification().isConfirmed()) {
			return false;
		}
		return true;
	}

	private class Wait4Departure implements Runnable {
		AvmModel _model;

		public Wait4Departure(AvmModel model) {
			_model = model;
		}

		private int getDelay2Departure() {
			if (_model == null)
				return 0;
			if (_model.getCourse() == null)
				return 0;
			String departureTime = _model.getCourse().getHeureDepart();
			Calendar calNow = GregorianCalendar.getInstance();
			int HHnow = calNow.get(Calendar.HOUR_OF_DAY);
			int MMnow = calNow.get(Calendar.MINUTE);
			StringTokenizer st = new StringTokenizer(departureTime, "h"); //$NON-NLS-1$
			int HHdep = Integer.parseInt(st.nextToken().trim());
			int MMdep = Integer.parseInt(st.nextToken().trim());
			int deltaHH = HHdep - HHnow;
			int deltaMM = MMdep - MMnow;
			int delay2Departure = deltaHH * 60 + deltaMM; // en minutes
			return delay2Departure;
		}

		public void run() {
			int delai = getDelay2Departure();
			Point p = _avm.getModel().getCourse().getPointAvecRang(2);
			if (p != null) {
				_avmView.activateAttenteDepart(
						AvmModel.STRING_STATE_ATTENTE_DEPART, delai, _avm
								.getModel().getCourse().getTerminusDepart()
								.getNom(), p.getNom(), p
								.getHeureArriveeTheorique());
			} else {
				_avmView.activateAttenteDepart(
						AvmModel.STRING_STATE_ATTENTE_DEPART, 0, _avm
								.getModel().getCourse().getTerminusDepart()
								.getNom(), null, null);
			}
			do {
				_log.debug("Delai avant depart : " + delai);
				_avmView.refreshAttenteDepart(delai);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
				delai = getDelay2Departure();
			} while ((delai > 0) && (_stopAttenteDepart == false)
					&& (_threadAttenteDep.isInterrupted() == false));

			stopAttenteDepart();
			if (_avm.getModel().getState().getValue() == AvmModel.STATE_ATTENTE_DEPART) {
				_log.debug("Ordre de depart ...");
				if (_beeper != null)
					_beeper.setValue(new Measurement(2));
				else
					_log.debug("Beeper non initialise."); //$NON-NLS-1$
				_log.debug("launch barre");
				_base.startAttente();
				_avm.depart();
			}
		}
	}

}
