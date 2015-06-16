package org.avm.device.wanesy.standby.bundle;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.device.wanesy.DeviceWanesy;
import org.avm.device.wanesy.standby.DeviceWanesyStandby;
import org.avm.elementary.common.AbstractActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator {

	private DeviceWanesyStandby peer;

	private DeviceWanesy deviceWanesy;

	private static final String[] BUNDLES_TO_STOP = {
			"org.avm.device.generic.gps", "org.avm.elementary.media." };

	private Logger log = Logger.getInstance(this.getClass());

	public Activator() {
		peer = new DeviceWanesyStandby();
	}

	protected void start(ComponentContext context) {
		log.info("on Start");
		deviceWanesy = (DeviceWanesy) context.locateService("wanesy");
		
		deviceWanesy.addDeviceListener(peer);
		retrieveGPS(context.getBundleContext());
		peer.start();
	}

	private void retrieveGPS(BundleContext bundleContext) {
		Bundle[] list = bundleContext.getBundles();
		List bundlesToStop = new ArrayList();
		for (int i = 0; i < list.length; ++i) {
			for (int j = 0; j < BUNDLES_TO_STOP.length; j++) {
				if (list[i].getSymbolicName().indexOf(BUNDLES_TO_STOP[j]) != -1) {
					bundlesToStop.add(list[i]);

					if (bundlesToStop.size() == BUNDLES_TO_STOP.length) {
						break;
					}
				}
			}
		}
		peer.setBundlesToStop(bundlesToStop);
	}

	protected void stop(ComponentContext context) {
		peer.stop();
		log.info("on stop");
		deviceWanesy.removeDeviceListener(peer);

	}

}
