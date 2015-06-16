package org.avm.device.wanesy.standby;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsStandbyTrig;
import org.avm.device.knet.model.KmsSystem;
import org.avm.device.wanesy.DeviceWanesyListener;
import org.osgi.framework.Bundle;

public class DeviceWanesyStandby implements DeviceWanesyListener {

	private Logger log = Logger.getInstance(this.getClass());

	private List bundlesToStop = null;

	public DeviceWanesyStandby() {
	}

	public String getListeningOn() {
		return "standby;system";
	}

	public void onDataReceived(KmsMarshaller kmsMarshaller) {
		if (kmsMarshaller.getRole().equals("standby")) {
			KmsStandbyTrig standbyTrig = (KmsStandbyTrig) kmsMarshaller;
			log.info("Standby called with delay :  " + standbyTrig.getDelay());
			stopBundles();
		} else if (kmsMarshaller.getRole().equals("system")) {
			KmsSystem system = (KmsSystem) kmsMarshaller;
			log.info("System with cause :  " + system.getAct());
			if ("wakeup".equals(system.getAct())) {
				startBundles();
			}
		}
	}

	private void stopBundles() {

		if (bundlesToStop != null) {
			Iterator iter = bundlesToStop.iterator();
			while(iter.hasNext()){
				try {
					Bundle bundle = (Bundle)iter.next();
					log.info("Bundle "+ bundle.getSymbolicName() + " stopping...");
					bundle.stop();
					log.info("Bundle "+ bundle.getSymbolicName() + " stopped.");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void startBundles() {
		if (bundlesToStop != null) {
			Iterator iter = bundlesToStop.iterator();
			while(iter.hasNext()){
				try {
					Bundle bundle = (Bundle)iter.next();
					log.info("Bundle "+ bundle.getSymbolicName() + " starting...");
					bundle.start();
					log.info("Bundle "+ bundle.getSymbolicName() + " started.");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void start() {

	}

	public void stop() {

	}

	protected void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	public void setBundlesToStop(List bundlesToStop2) {
		this.bundlesToStop = bundlesToStop2;
	}

}
