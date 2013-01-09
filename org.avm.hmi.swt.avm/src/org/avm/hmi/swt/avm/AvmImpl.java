package org.avm.hmi.swt.avm;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.business.core.Avm;
import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.business.protocol.phoebus.AvanceRetard;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.avm.bundle.ConfigImpl;
import org.avm.hmi.swt.desktop.Desktop;
import org.avm.hmi.swt.desktop.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.util.measurement.State;

public class AvmImpl implements ConsumerService, ManageableService,
		UserSessionServiceInjector, ConfigurableService {

	private static final String SERVICE_OCCASIONNEL = Messages
			.getString("AvmImpl.occasionnel"); //$NON-NLS-1$

	private static final String SERVICE_KM_A_VIDE = Messages
			.getString("AvmImpl.km_a_vide"); //$NON-NLS-1$

	protected static final String NAME = Messages.getString("AvmImpl.titre"); //$NON-NLS-1$

	private AvmIhm _avmihm;

	private Display _display;

	private Desktop _desktop;

	private Avm _avm;

	private Logger _log;

	private AvmImpl _instance;

	private UserSessionService _session;

	private boolean _demoMode;

	private boolean _georefMode;

	private Object _lastError;
	
	private int _periode=12;


	public AvmImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
		_instance = this;
	}

	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
		if (desktop != null) {
			_display = desktop.getDisplay();
		}
	}

	public void start() {
	}

	public void stop() {
		logout();
		close();
	}

	public void open() {
		_display.asyncExec(new Runnable() {

			public void run() {
				System.out.println("open : begin");

				if (_avmihm == null) {
					try {
						_log.info("instantiation AvmIHM...");
						_avmihm = new AvmIhm(_desktop.getMiddlePanel(),
								SWT.NONE);
						_avmihm.setAvm(_avm);
						_avmihm.setBase(_desktop);
						_avmihm.setDemoMode(_demoMode);
						_avmihm.setGeorefRole(_georefMode);
						_avmihm.setDemoPeriode(_periode);
						_log.info("instantiation AvmIHM setperiode...");
						if (_avm != null && _avm.getModel().getState() != null) {
							//_log.debug("Synchronizing with AVM"); //$NON-NLS-1$
							State state = _avm.getModel().getState();
							System.out.println("open : call notifyAvm before : instance=" + _instance);
							System.out.println("open : synchronize with state " + state + "...");
							_instance.notifyAvm(state);
							System.out.println("open : call notifyAvm before");
						}
						_log.info("instantiation AvmIHM ok...");
					} catch (Throwable t) {
						t.printStackTrace();
					}

				}
				

				
				System.out.println("AvmIhm = " + _avmihm);
				_desktop.addTabItem(NAME, _avmihm, 0);
				_desktop.activateItem(NAME);

				System.out.println("open : end");

			}
		});

	}

	public void close() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (_avmihm != null) {
					_avmihm.close();
					_avmihm = null;
				}
			}
		});
	}

	public void notify(Object o) {
		_log.info(o);
		System.out.println("AvmImpl.java notify : BEGIN [DISABLED]");

		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(UserSessionService.class.getName())) {
				notifyUserSessionService(state);
			} else if (_session.getState().getValue() == UserSessionService.AUTHENTICATED) {
				notifyAvm(state);
			}
		} else if (o instanceof AvanceRetard) {
			_log.debug("Receive 'AvanceRetard' : " + o); //$NON-NLS-1$
			if (_avmihm != null) {
				_avmihm.setAvanceRetard(_avm.getModel().getAvanceRetard());
			}
		} else if (o instanceof Point) {
			_log.debug("Receive 'Point' : " + o); //$NON-NLS-1$
			if (_avmihm != null) {
				_avmihm.setPoint(_avm.getModel().getDernierPoint());
				_avmihm.setAvanceRetard(_avm.getModel().getAvanceRetard());
			}
		}
		System.out.println("AvmImpl.java notify : END");

	}

	private synchronized void notifyUserSessionService(State s) {
		//_log.debug("State : " + s); //$NON-NLS-1$
		if (s.getValue() == UserSessionService.AUTHENTICATED) {
			login();
		} else {
			logout();
		}
	}

	public synchronized void notifyAvm(State state) {
		System.out.println("AvmImpl.java notifyAvm : BEGIN state=" + state);
		if (_avmihm != null && _avm != null && _avmihm.isDisposed() == false && isAuthenticated()) {
			switch (state.getValue()) {
			case AvmModel.STATE_INITIAL: {
				activateInitial();
			}
				break;
			case AvmModel.STATE_ATTENTE_SAISIE_SERVICE: {
				if (_avm.getModel().getPlanification().isConfirmed()) {
					_avmihm.setMessageBox(null, null, MessageBox.MESSAGE_NORMAL);
				}
				activateSaisieService();
			}
				break;
			case AvmModel.STATE_ATTENTE_SAISIE_COURSE: {
				if (_avm.getModel().getPlanification().isConfirmed()) {
					_avmihm.setMessageBox(null, null, MessageBox.MESSAGE_NORMAL);
				}
				activateSaisieCourse();
			}
				break;
			case AvmModel.STATE_ATTENTE_DEPART: {
				_log.debug(AvmModel.STATE_NAMES[state.getValue()]);
				activateAttenteDepart();
			}
				break;
			case AvmModel.STATE_EN_COURSE_SERVICE_SPECIAL: {
				_log.debug(_avm.getModel().getState());
				activateServiceSpecial();
			}
				break;
			case AvmModel.STATE_EN_COURSE_HORS_ITINERAIRE: {
				_log.debug(AvmModel.STATE_NAMES[state.getValue()]);
				System.out
						.println("notifyAvm STATE_EN_COURSE_HORS_ITINERAIRE : call activateSuiviCourse Before ");
				activateSuiviCourse();
				System.out
						.println("notifyAvm STATE_EN_COURSE_HORS_ITINERAIRE : call activateSuiviCourse After ");

				_avmihm.setHorsItineraire(true);
				System.out
						.println("notifyAvm STATE_EN_COURSE_HORS_ITINERAIRE : finished. ");
			}
				break;
			case AvmModel.STATE_EN_COURSE_ARRET_SUR_ITINERAIRE: {
				_log.debug(AvmModel.STATE_NAMES[state.getValue()]);
				activateSuiviCourse();
				notify(_avm.getModel().getDernierPoint());
			}
				break;
			case AvmModel.STATE_EN_COURSE_INTERARRET_SUR_ITINERAIRE: {
				_log.debug(AvmModel.STATE_NAMES[state.getValue()]);
				activateSuiviCourse();
				_log.debug("entree 'etat interarret' :" + _avm.getModel()); //$NON-NLS-1$
				Point p = _avm.getModel().getDernierPoint();
				if (p == null) {
					p = _avm.getModel().getProchainPoint();
				}
				notify(p);
			}
				break;
			case AvmModel.STATE_EN_PANNE: {
				_log.debug(AvmModel.STATE_NAMES[state.getValue()]);
			}
				break;
			}// switch

			if (_avm.getModel().getLastError() != null
					&& !_avm.getModel().getLastError().equals(_lastError)) {
				_avmihm.setMessageBox(
						Messages.getString("AvmImpl.attention"), _avm.getModel().getLastError(), //$NON-NLS-1$
						MessageBox.MESSAGE_WARNING);
			}
		}
		System.out.println("AvmImpl.java notifyAvm : END");

	}

	private void activateSuiviCourse() {
		System.out.println("AvmImpl.java call activateSuiviCourse BEGIN--");
		Course course = _avm.getModel().getCourse();
		_avmihm.activateSuiviCourse(course);
		System.out.println("AvmImpl.java call activateSuiviCourse END--");
	}

	private void activateServiceSpecial() {
		ServiceAgent sa = _avm.getModel().getServiceAgent();
		if (sa.getIdU() == ServiceAgent.SERVICE_KM_A_VIDE) {
			_avmihm.setMessage(SERVICE_KM_A_VIDE);
		} else if (sa.getIdU() == ServiceAgent.SERVICE_OCCASIONNEL) {
			_avmihm.setMessage(SERVICE_OCCASIONNEL);
		}
		_avmihm.activateServiceSpecial();
	}

	private void activateAttenteDepart() {
		Course course = _avm.getModel().getCourse();
		info();
		_avmihm.activateDepart(course);
	}

	private void info() {
		ServiceAgent sa = _avm.getModel().getServiceAgent();
		Course c = _avm.getModel().getCourse();
		String serv = Messages.getString("AvmImpl.SERVICE"); //$NON-NLS-1$
		StringBuffer msg = new StringBuffer();
		if (sa == null) {
			_avmihm.setMessage(null);
			return;
		}

		if (sa.isPlanifie()) {
			serv = Messages.getString("AvmImpl.SERVICE-PLANIFIE"); //$NON-NLS-1$
		}
		msg.append(serv);
		msg.append(" "); //$NON-NLS-1$
		msg.append(sa.getIdU());
		if (c != null) {
			msg.append(" - "); //$NON-NLS-1$
			msg.append(Messages.getString("AvmImpl.COURSE")); //$NON-NLS-1$
			msg.append(" "); //$NON-NLS-1$
			msg.append((c.getNom() == null) ? "#" + c.getIdu() : c.getNom());
		}
		_avmihm.setMessage(msg.toString());
	}

	private void activateSaisieCourse() {
		ServiceAgent sa = _avm.getModel().getServiceAgent();
		info();
		_avmihm.activateSaisieCourse(sa);
	}

	private void activateInitial() {
		if (_avmihm != null) {
			_avmihm.setMessage(null); //$NON-NLS-1$
			_avmihm.activateInitial();
		}
		MessageBox.setMessage(Messages.getString("AvmImpl.attention"), _avm
				.getModel().getLastError(), MessageBox.MESSAGE_NORMAL,
				SWT.CENTER);
	}

	private void activateSaisieService() {
		if (_avm.getModel().getPlanification() == null
				|| _avm.getModel().getPlanification().isConfirmed() == false) {
			String errorMessage = _avm.getModel().getLastError();

			_avmihm.setMessage(null); //$NON-NLS-1$
			MessageBox.setMessage(null, null, MessageBox.MESSAGE_NORMAL,
					SWT.CENTER);
			if (errorMessage == null) {
				MessageBox
						.setMessage(
								Messages.getString("AvmImpl.attention"), Messages.getString("AvmImpl.attente-planification"), //$NON-NLS-1$ //$NON-NLS-2$
								MessageBox.MESSAGE_NORMAL, SWT.CENTER);
			} else {
				MessageBox.setMessage(
						Messages.getString("AvmImpl.attention"), errorMessage, //$NON-NLS-1$ //$NON-NLS-2$
						MessageBox.MESSAGE_WARNING, SWT.CENTER);
			}

		} else {
			if (_avm.getModel().getPlanification().isCorrect() == false) {
				MessageBox.setMessage(null, null, MessageBox.MESSAGE_NORMAL,
						SWT.CENTER);
				if (_avmihm != null) {
					_avmihm.setMessage(Messages
							.getString("AvmImpl.aucune-planification")); //$NON-NLS-1$
				}
			} else {
				info();
			}
		}
		_avmihm.activateSaisieService();
	}

	public void setAvm(Avm avm) {
		_avm = avm;
		if (_avmihm != null) {
			_avmihm.setAvm(_avm);
			notifyAvm(_avm.getModel().getState());
		}
	}

	public void unsetAvm(Avm avm) {
		setAvm(null);
		_avmihm.setAvm(null);
	}

	public void setDemoMode(boolean b) {
		_demoMode = b;
		if (_avmihm != null) {
			_avmihm.setDemoMode(b);
		}
	}

	public void setGeorefRole(boolean b) {
		_georefMode = b;
		if (_avmihm != null) {
			_avmihm.setGeorefRole(b);
		}
	}
	
	private boolean isAuthenticated(){
		return _session != null && _session.getState().getValue() == UserSessionService.AUTHENTICATED && _session.hasRole("conducteur");
	}

	public void login() {
		_log.info("Login : " + _session);

		if (_session != null) {
			_log.info("Login state : " + _session.getState());
			_log.info("Login hasRole(conducteur) ? : "
					+ _session.hasRole("conducteur"));

			if (isAuthenticated()) {
				open();
				setDemoMode(_session.hasRole("demo")
						|| _session.hasRole("test"));
				setGeorefRole(_session.hasRole("georef"));
			}
		}
	}

	public synchronized void logout() {
		setGeorefRole(false);
		_display.asyncExec(new Runnable() {
			public void run() {
				if (_desktop != null && _avmihm != null) {
					_avmihm.logout();
					_desktop.removeTabItem(NAME);
				}
			}
		});
		
	}

	public void setUserSessionService(UserSessionService service) {
		_session = service;
		//login();
	}

	public void unsetUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void configure(Config config) {
		_periode = ((AvmIhmConfig)config).getPeriode();
	}

	public void setDemoPeriode(int periode) {
		_periode = periode;
		if (_avmihm != null){
			_avmihm.setDemoPeriode(periode);
		} 
	}

}