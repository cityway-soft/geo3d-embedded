package org.avm.device.apc.ihmi;

import org.avm.device.ihmi.z8.Z8Access;
import org.avm.device.linux.watchdog.APCInformation;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator, APCInformation {

	private int lastState = APCInformation.STATE_ERROR;
	private int fileId = 0;

	public void start(BundleContext context) throws Exception {
		context.registerService(APCInformation.class.getName(), this, null);
	}

	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public boolean open() {
		fileId = Z8Access.opendevice();
		return (fileId != 0);
	}

	public int read() {

		int current = Z8Access.readSystemCurrentState(fileId);
		while (current == lastState) {
			current = Z8Access.readSystemCurrentState(fileId);
			synchronized (this) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {

				}
			}
		}
		lastState = current;
		return (current == Z8Access.SYSTEM_WAITING_FOR_SHUTDOWN) ? APCInformation.STATE_PERMANENT
				: APCInformation.STATE_APC;
	}

	public boolean close() {
		boolean ret = false;
		if (fileId != 0) {
			Z8Access.closedevice(fileId);
			ret = true;
		}
		return ret;
	}

}
