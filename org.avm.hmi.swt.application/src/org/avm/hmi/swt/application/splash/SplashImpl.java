package org.avm.hmi.swt.application.splash;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;

public class SplashImpl implements Splash, ServiceListener,
		SynchronousBundleListener {

	private BundleContext _context;

	private SplashIhm _splashIhm;

	private int _bundlesCount = -1;

	private SplashVisibleTimerTask _task;

	
	public SplashImpl() {
		super();
		_splashIhm = new SplashIhm();
	}

	public void setBundleContext(BundleContext ctx) {
		_context = ctx;
	}

	public void start() {
		setMaxAtStartup();
		_splashIhm.start();
		_splashIhm.setDefaultSplashImage();
		_context.addServiceListener(this);
		_context.addBundleListener(this);
	}

	public void stop() {
		_context.removeServiceListener(this);
		_context.removeBundleListener(this);
		_splashIhm.stop();
	}

	private void setMaxAtStartup() {
		if (_bundlesCount == -1) {
			_bundlesCount = _context.getBundles().length;
			for (int i = 0; i < _context.getBundles().length; i++) {
				if (_context.getBundles()[i].getState() == Bundle.ACTIVE) {
					_bundlesCount++;
				}
			}
			_splashIhm.setProgressBarMax(_bundlesCount);
		}
	}

	private void setMaxAtUpdate() {
		if (_bundlesCount == -1) {
			_bundlesCount = _context.getBundles().length;
			for (int i = 0; i < _context.getBundles().length; i++) {
				if (_context.getBundles()[i].getState() != Bundle.ACTIVE) {
					_bundlesCount++;
				}
			}
			// -- empirique (!)
			_splashIhm.setProgressBarMax(_bundlesCount / 3);
		}
	}

	private void show(String task) {
		_splashIhm.setVisible(true);

		_splashIhm.setTask(task);
		if (_task != null) {
			_task.cancel();
			_task = null;
		}
		_task = new SplashVisibleTimerTask();
		Timer timer = new Timer();
		timer.schedule(_task, 6000);
		
	}

	public void serviceChanged(ServiceEvent event) {
		ServiceReference sr = event.getServiceReference();
		
		Object obj = _context.getService(sr);
		Class[] interfaces = obj.getClass().getInterfaces();
		_context.ungetService(sr);
		if(interfaces.length>0){
			String task = interfaces[0].getName();
			show(task);
		}
	}


	public void bundleChanged(BundleEvent evt) {
		setMaxAtUpdate();
		if (evt.getType() == BundleEvent.UNRESOLVED) {
			_splashIhm.setManagementSplashImage();
			show(Messages.getString("SplashImpl.mise-a-jour") //$NON-NLS-1$
					+ evt.getBundle().getSymbolicName());
		}
		if (evt.getType() == BundleEvent.UPDATED) {
			_splashIhm.setManagementSplashImage();
			show("");
		}
	}



	class SplashVisibleTimerTask extends TimerTask {
		public void run() {
			_splashIhm.setVisible(false);
			_bundlesCount = -1;
		}
	}



	public void setDisplay(Display display) {
		_splashIhm.setDisplay( display );
	}

}
