package org.avm.device.wanesy.standby;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsStandbyTrig;
import org.avm.device.knet.model.KmsSystem;
import org.avm.device.wanesy.DeviceWanesy;
import org.avm.device.wanesy.DeviceWanesyListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.startlevel.StartLevel;

public class DeviceWanesyStandby implements DeviceWanesyListener {

	private Logger log = Logger.getInstance(this.getClass());

	private DeviceWanesy deviceWanesy = null;

	private StartLevel sl = null;

	private static final int EXIT_CODE = 2;

	private static final int START_LEVEL = 1;

	private Bundle gps = null;

	public Bundle getGps() {
		return gps;
	}

	public void setGps(Bundle gps) {
		this.gps = gps;
	}

	public DeviceWanesyStandby() {
	}

	public String getListeningOn() {
		return "standby;system";
	}

	public void onDataReceived(KmsMarshaller kmsMarshaller) {
		if (kmsMarshaller.getRole().equals("standby")) {
			KmsStandbyTrig standbyTrig = (KmsStandbyTrig) kmsMarshaller;
			log.info("Standby called with delay :  " + standbyTrig.getDelay());
			stopGPS();
		} else if (kmsMarshaller.getRole().equals("system")) {
			KmsSystem system = (KmsSystem) kmsMarshaller;
			log.info("System with cause :  " + system.getAct());
			if ("wakeup".equals(system.getAct())) {
				startGPS();
			}
		}
	}

	private void startGPS() {
		if (gps != null) {
			try {
				gps.start();
			} catch (BundleException e) {
				log.error("cannot start gps", e);
			}
		}
	}

	private void stopGPS() {
		try {
			gps.stop();
		} catch (BundleException e) {
			log.error("cannot stop gps", e);
		}
	}

	private void stopApplication() {

		sl.setStartLevel(START_LEVEL);
		long now = System.currentTimeMillis();
		while (sl.getStartLevel() != START_LEVEL) {
			sleep(1000);
			if (System.currentTimeMillis() - now > 30 * 1000) {
				log.debug("shutdown timeout ");
				break;
			}
		}

		System.exit(EXIT_CODE);
	}

	public void setWanesy(DeviceWanesy deviceWanesy) {
		this.deviceWanesy = deviceWanesy;
	}

	public void start() {

	}

	public void stop() {

	}

	public void setStartLevelService(StartLevel sl) {
		this.sl = sl;
	}

	protected void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

}
