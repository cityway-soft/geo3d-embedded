package org.avm.hmi.mmi.application.bundle;

import org.avm.device.knet.mmi.Mmi;
import org.avm.device.knet.mmi.MmiInjector;
import org.avm.hmi.mmi.application.actions.ProcessRunnable;
import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.application.display.AVMDisplayImpl;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements AVMDisplay, BundleActivator,
		ServiceTrackerCustomizer {

	static final String PID = AVMDisplay.class.getName();

	private AVMDisplayImpl _peer;

	// private SplashImpl _splash;

	private BundleContext _context;

	private ServiceTracker _tracker;

	private ServiceRegistration _sr;

	public Activator() {
		_peer = new AVMDisplayImpl();
	}

	public void start(BundleContext context) throws Exception {
		try {
			_context = context;
			_tracker = new ServiceTracker(context, Mmi.class.getName(), this);
			_peer.start();
			_sr = context.registerService(AVMDisplay.class.getName(), _peer,
					null);
			_tracker.open();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void stop(BundleContext context) throws Exception {
		try {
			_sr.unregister();
			_tracker.close();
			_peer.stop();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public Object addingService(ServiceReference reference) {
		Object o = _context.getService(reference);

		if (o instanceof Mmi) {
			Mmi mmi = (Mmi) o;
			if (_peer instanceof MmiInjector) {
				((MmiInjector) _peer).setMmi(mmi);
			}
		}
		return o;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		Object o = _context.getService(reference);

		if (o instanceof Mmi) {
			Mmi mmi = (Mmi) o;
			if (_peer instanceof MmiInjector) {
				((MmiInjector) _peer).setMmi(mmi);
			}
		}
	}

	public void removedService(ServiceReference reference, Object service) {
		Object o = _context.getService(reference);

		if (o instanceof Mmi) {

			if (_peer instanceof MmiInjector) {
				((MmiInjector) _peer).setMmi(null);
			}
		}
	}

	public void setScreen2fg(MmiDialogInWrapper s) {
		_peer.setScreen2fg(s);
	}

	public void submit() {
		_peer.submit();
	}

	public void setMsg(String msg) {
		_peer.setMessage(msg);
	}

	public void setProcess(String action, String etat, ProcessRunnable process) {
		_peer.setProcess(action, etat, process);
	}

	public ProcessRunnable getProcess(String action, String etat) {
		return _peer.getProcess(action, etat);
	}

	public void setProcessArgs(String action, String etat, String[] args) {
		_peer.setProcessArgs(action, etat, args);
	}

	public void clearMessages() {
		_peer.clearMessages();
	}

	public void resetSoftKey2fgView(String key) {
		_peer.resetSoftKey2fgView(key);
	}

	public void setSoftKey2fgView(String key, String sk) {
		_peer.setSoftKey2fgView(key, sk);
	}

	public void unsetViewFromfgAlways(String key, MmiDialogInWrapper screen) {
		_peer.unsetViewFromfgAlways(key, screen);
	}

	public void setView2fgAlways(MmiDialogInWrapper screen) {
		_peer.setView2fgAlways(screen);
	}

	public void back() {
		_peer.back();
	}

	public void updateScreen2fg(MmiDialogInWrapper screen) {
		_peer.updateScreen2fg(screen);
	}

	public MmiDialogInWrapper getFgScreen() {
		return _peer.getFgScreen();
	}

	public void setMessage(String msg) {
		_peer.setMessage(msg);
	}

	public void startAttente() {
		_peer.startAttente();
	}

	public void stopAttente() {
		_peer.stopAttente();
	}

}
