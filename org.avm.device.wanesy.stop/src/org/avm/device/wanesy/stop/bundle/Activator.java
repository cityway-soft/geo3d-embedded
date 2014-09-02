package org.avm.device.wanesy.stop.bundle;

import org.apache.log4j.Logger;
import org.avm.device.wanesy.DeviceWanesy;
import org.avm.device.wanesy.stop.DeviceWanesyStop;
import org.avm.elementary.common.AbstractActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.startlevel.StartLevel;

public class Activator extends AbstractActivator  {

	private DeviceWanesyStop peer;

	
	private DeviceWanesy deviceWanesy;

	private Logger log = Logger.getInstance(this.getClass());
	
	public Activator() {
		peer = new DeviceWanesyStop();
	}

	


	protected void start(ComponentContext context) {
		log.info("on Start");
		deviceWanesy = (DeviceWanesy) context.locateService("wanesy");
		System.out.println("peer added is : " + peer);
		deviceWanesy.addDeviceListener(peer);
		retrieveStartLevelService(context.getBundleContext());
		peer.setWanesy (deviceWanesy);
		peer.start ();
	}

	protected void stop(ComponentContext context) {
		peer.stop ();
		log.info("on stop");
		deviceWanesy.removeDeviceListener(peer);
		
	}
	
	private void retrieveStartLevelService (BundleContext bc){
		ServiceReference sr = bc.getServiceReference(StartLevel.class
				.getName());
		StartLevel sl = (StartLevel) bc.getService(sr);
		peer.setStartLevelService(sl);
		
	}

}
