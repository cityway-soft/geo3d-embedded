package org.avm.device.apc.pccar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.avm.device.linux.watchdog.APCInformation;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator, APCInformation {

	private static final String DEVICE = "/dev/vsi0";
	private FileInputStream in = null;

	public void start(BundleContext context) throws Exception {
		context.registerService(APCInformation.class.getName(), this, null);
	}

	public void stop(BundleContext arg0) throws Exception {
		
	}

	public boolean open() {
		boolean ret = true;
		try {
			in = new FileInputStream(DEVICE);
		} catch (FileNotFoundException e) {
			ret = false;
		}
		return ret;
	}

	public int read() {
		int ret = APCInformation.STATE_ERROR;
		if (in != null){
			byte[] buffer = new byte[2];
			try {
				if (in.read(buffer) == 2) {
					if((buffer[1] & 0x20) == 0x20){
						ret = APCInformation.STATE_PERMANENT;
					}
					else{
						ret = APCInformation.STATE_APC;
					}
				}
			} catch (IOException e) {
				
			}
		}
		return ret;
	}

	public boolean close() {
		boolean ret = false;
		if (in != null) {
			try {
				in.close();
				ret = true;
			} catch (IOException e) {

			}
		}
		return ret;
	}

}
