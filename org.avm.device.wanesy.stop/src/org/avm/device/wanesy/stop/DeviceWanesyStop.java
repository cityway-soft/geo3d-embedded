package org.avm.device.wanesy.stop;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsStandbyTrig;
import org.avm.device.knet.model.KmsStopReqTrig;
import org.avm.device.wanesy.DeviceWanesy;
import org.avm.device.wanesy.DeviceWanesyListener;
import org.osgi.service.startlevel.StartLevel;

public class DeviceWanesyStop implements DeviceWanesyListener {

	private Logger log = Logger.getInstance(this.getClass());

	private DeviceWanesy deviceWanesy = null;

	private StartLevel sl = null;

	private static final int EXIT_CODE = 2;

	private static final int START_LEVEL = 1;

	public DeviceWanesyStop() {
	}

	public String getListeningOn() {
		return "stopreq";
	}

	public void onDataReceived(KmsMarshaller kmsMarshaller) {
		if (kmsMarshaller.getRole().equals("stopreq")) {
			KmsStopReqTrig stopreqTrig = (KmsStopReqTrig) kmsMarshaller;
			log.info("Stop req called with cause :  " + stopreqTrig.getCause());
			stopApplication();
		}
	}

	private void stopApplication() {
		System.out.println("sl : "+ sl);
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
