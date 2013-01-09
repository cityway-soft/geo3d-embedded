package org.avm.hmi.swt.application.bundle;

import org.avm.hmi.swt.application.display.AVMDisplay;
import org.avm.hmi.swt.application.display.AVMDisplayImpl;
import org.avm.hmi.swt.application.splash.SplashImpl;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements AVMDisplay, BundleActivator/*
															 * ,
															 * ServiceTrackerCustomizer
															 */{

	static final String PID = AVMDisplay.class.getName();

	private AVMDisplayImpl _peer;

	private SplashImpl _splash;

	private ServiceRegistration _sr;

	public Activator() {
		_peer = new AVMDisplayImpl();
		boolean splashEnable = System.getProperty("org.avm.splash", "true")
				.equals("true");
		if (splashEnable) {
			_splash = new SplashImpl();
		}
	}

	public void start(BundleContext context) throws Exception {
		try {
			_peer.start();
			if (_splash != null) {
				_splash.setDisplay(_peer.getDisplay());
				_splash.setBundleContext(context);
				_splash.start();
			}
			_sr = context.registerService(AVMDisplay.class.getName(), _peer,
					null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void stop(BundleContext context) throws Exception {
		_sr.unregister();
		if (_splash != null) {
			_splash.stop();
		}

		_peer.stop();
	}

	public Display getDisplay() {
		return _peer.getDisplay();
	}

}
