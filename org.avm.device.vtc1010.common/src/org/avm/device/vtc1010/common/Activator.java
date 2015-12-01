package org.avm.device.vtc1010.common;

import org.avm.device.vtc1010.common.api.Vtc1010APC;
import org.avm.device.vtc1010.common.api.Vtc1010IO;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

		
	private Vtc1010APC vtc1010apcImpl = new Vtc1010APCImpl();
	private Vtc1010IO vtc1010IoImpl = new Vtc1010IOImpl();
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		context.registerService(Vtc1010APC.class.getName(), vtc1010apcImpl, null);
		context.registerService(Vtc1010IO.class.getName(), vtc1010IoImpl, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
	}

}

