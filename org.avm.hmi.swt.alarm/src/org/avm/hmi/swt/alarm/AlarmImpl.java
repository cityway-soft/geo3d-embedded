package org.avm.hmi.swt.alarm;

import java.util.List;

import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.measurement.State;

public class AlarmImpl implements AlarmIhm, ManageableService, ProducerService,
		ConsumerService, UserSessionServiceInjector, ConfigurableService {
	protected static final String NAME = "Alarmes";

	private AlarmIhmImpl _ihm;

	private Desktop _desktop;

	private Logger _log;

	private Display _display;

	private ProducerManager _producer;

	private UserSessionService _session;

	private boolean _logged = false;

	private Config _config;

	private ComponentContext _context;

	public AlarmImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public void setBase(Desktop base) {
		_desktop = base;
		if (_desktop != null) {
			_display = _desktop.getDisplay();
		}
	}

	public void start() {
		loggedOn();
	}

	public void stop() {
		loggedOut();
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
		if (_ihm != null) {
			_ihm.setProducer(_producer);
		}
	}

	public List getAlarm() {
		if (_ihm != null) {
			return _ihm.getAlarm();
		}
		return null;
	}

	public String getProducerPID() {
		return AlarmIhm.class.getName();
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getValue() == UserSessionService.AUTHENTICATED) {
				loggedOn();
			} else {
				loggedOut();
			}
		}
	}

	public void loggedOn() {
		if (_session != null && _session.hasRole("alarm") && !_logged) {
			_display.asyncExec(new Runnable() {
				public void run() {
					if (_ihm == null || _ihm.isDisposed()) {
						_ihm = new AlarmIhmImpl(_desktop.getMiddlePanel(),
								SWT.NONE);
						_ihm.setProducer(_producer);
						_ihm.configure(_config);
						_ihm.setContext(_context);
						if (_desktop != null) {
							_desktop.addTabItem(NAME, _ihm);
						}
					}
				}
			});
			_logged = true;
		}
	}

	public void loggedOut() {
		if (_logged) {
			_display.asyncExec(new Runnable() {
				public void run() {
					if (_ihm != null && _ihm.isDisposed() == false) {
						_ihm.dispose();
						_ihm = null;
						_desktop.removeTabItem(NAME);
					}
				}
			});
			_logged = false;
		}
	}

	public void setUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void unsetUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void configure(Config config) {
		_config = config;
	}

	public void setContext(ComponentContext context) {
		_context = context;
	}

}