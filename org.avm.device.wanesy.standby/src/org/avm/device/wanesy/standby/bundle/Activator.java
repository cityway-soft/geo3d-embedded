package org.avm.device.wanesy.standby.bundle;

import org.apache.log4j.Logger;
import org.avm.device.wanesy.DeviceWanesy;
import org.avm.device.wanesy.standby.DeviceWanesyStandby;
import org.avm.elementary.common.AbstractActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.startlevel.StartLevel;

public class Activator extends AbstractActivator  {

	private DeviceWanesyStandby peer;

	
	private DeviceWanesy deviceWanesy;

	private Logger log = Logger.getInstance(this.getClass());
	
	public Activator() {
		peer = new DeviceWanesyStandby();
	}

	


	protected void start(ComponentContext context) {
		log.info("on Start");
		deviceWanesy = (DeviceWanesy) context.locateService("wanesy");
		deviceWanesy.addDeviceListener(peer);
		retrieveStartLevelService(context);
		retrieveGPS(context.getBundleContext());
		peer.setWanesy (deviceWanesy);
		peer.start ();
	}

	private void retrieveGPS(BundleContext bundleContext) {
		Bundle[] list = bundleContext.getBundles();
		for (int i = 0; i < list.length; ++i){
			if ("org.avm.device.generic.gps".equals(list[i].getSymbolicName())){
				peer.setGps(list[i]);
				break;
			}
		}
	}




	protected void stop(ComponentContext context) {
		peer.stop ();
		log.info("on stop");
		deviceWanesy.removeDeviceListener(peer);
		
	}
	
	private void retrieveStartLevelService (ComponentContext cc){
		StartLevel sl = (StartLevel)cc.locateService(StartLevel.class.getName());
		peer.setStartLevelService(sl);
	}

}
