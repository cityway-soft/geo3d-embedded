package org.avm.device.plateform;

import org.avm.device.fm6000.network.jni.COMVS_NETCONFIG;
import org.avm.device.fm6000.power.jni.COMVS_POWERMANAGEMENTJNI;
import org.avm.device.fm6000.screen.jni.COMVS_NIGHTMODE;
import org.avm.device.fm6000.system.jni.COMVS_SYSTEM;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static Activator _plugin;

	public Activator() {
		_plugin = this;
	}

	public static Activator getDefault() {
		return _plugin;
	}

	public void start(BundleContext context) throws Exception {

	}

	public void stop(BundleContext context) throws Exception {

	}

	static {
		try {
			Class.forName(COMVS_NETCONFIG.class.getName());
			Class.forName(COMVS_POWERMANAGEMENTJNI.class.getName());
			Class.forName(COMVS_NIGHTMODE.class.getName());
			Class.forName(COMVS_SYSTEM.class.getName());
		} catch (ClassNotFoundException e) {

		}
	}
}