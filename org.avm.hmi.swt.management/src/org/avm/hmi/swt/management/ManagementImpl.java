package org.avm.hmi.swt.management;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.useradmin.UserSessionService;
import org.avm.elementary.useradmin.UserSessionServiceInjector;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.knopflerfish.service.console.ConsoleService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.util.measurement.State;

public class ManagementImpl implements Management, ManageableService,
		BundleListener, ConsumerService, ConfigurableService,
		BundleContextInjector, UserSessionServiceInjector {

	private static final String NAME = "Maintenance";

	private Display _display;

	private Desktop _desktop;

	private ManagementIhm _managementihm;

	private Logger _log;

	private BundleContext _context;

	private ConsoleService _console;

	private boolean _visible = false;

	private Config _config;

	private UserSessionService _session;

	public ManagementImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
	}
	


	public void setDesktop(Desktop desktop) {
		_desktop = desktop;
		if (_desktop != null) {
			_display = _desktop.getDisplay();
		}
	}

	public void start() {
		login();
	}

	public void stop() {
		logout();
	}

	public Display getDisplay() {
		return _desktop.getDisplay();
	}

	public void setBundleContext(BundleContext context) {
		if (_context != null) {
			_context.removeBundleListener(this);
		}
		if (context != null) {
			_context = context;
			context.addBundleListener(this);
		}
	}

	public void open() {
		_display.syncExec(new Runnable() {
			public void run() {
				if (_desktop != null) {
					_managementihm = new ManagementIhm(_desktop
							.getMiddlePanel(), SWT.NONE);
					_managementihm.setConsoleService(_console);
					_desktop.addTabItem(NAME, _managementihm, 0);
					_managementihm.configure(_config);
					_managementihm.setBundleContext(_context);
					bundleChanged(null);
					_desktop.activateItem(NAME);
				}
			}
		});
	}

	public void close() {
		_display.syncExec(new Runnable() {
			public void run() {
				try {
					if (_managementihm != null
							&& _managementihm.isDisposed() == false) {
						_managementihm.dispose();
						_desktop.removeTabItem(NAME);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
	}

	public void bundleChanged(BundleEvent event) {
		if (_context == null)
			return;

		if (event == null) {
			Bundle[] bundles = _context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				Bundle bundle = bundles[i];
				long id = bundle.getBundleId();
				int state = bundle.getState();
				String bundleName = (String) bundle.getHeaders().get(
						"Bundle-Name"); //$NON-NLS-1$
				String version = (String) bundle.getHeaders().get(
						"Bundle-Version"); //$NON-NLS-1$
				_managementihm.update(id, bundleName, version, state);
			}
		} else {
			Bundle bundle = event.getBundle();
			long id = bundle.getBundleId();
			int state = bundle.getState();
			String bundleName = (String) bundle.getHeaders().get("Bundle-Name"); //$NON-NLS-1$
			String version = (String) bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
			_managementihm.update(id, bundleName, version, state);
		}

	}

	public void setConsoleService(ConsoleService console) {
		_console = console;
	}

	public boolean isVisible() {
		if (_managementihm == null || _managementihm.isDisposed()) {
			_visible = false;
		} else {
			_display.syncExec(new Runnable() {
				public void run() {
					try {
						_visible = _managementihm.isVisible();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			});
		}
		// _log.info("Management IHM is"+(_visible?"":" NOT")+" visible");
		return _visible;
	}

	public void configure(Config config) {
		_config = config;
		if (_managementihm != null) {
			_managementihm.configure(config);
		}
	}

	public void setUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void unsetUserSessionService(UserSessionService service) {
		_session = service;
	}

	public void login() {
		if (_session == null || (_session != null && _session.hasRole("admin"))) {
			open();
		}
	}

	public void logout() {
		close();
	}

	public void notify(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getValue() == UserSessionService.AUTHENTICATED) {
				login();
			} else {
				logout();
			}
		}
	}
}