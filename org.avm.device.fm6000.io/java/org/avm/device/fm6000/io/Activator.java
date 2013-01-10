package org.avm.device.fm6000.io;

import org.avm.device.fm6000.io.jni.COMVS_IO;
import org.avm.elementary.common.AbstractActivator;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator {

	private int _handle = -1;

	private static Activator _plugin;

	public static Activator getDefault() {
		return _plugin;
	}

	protected int getHandle() {
		if(_handle == -1){
			_handle = COMVS_IO.open("DIO1:");
		}
		return _handle;
	}

	protected void start(ComponentContext context) {
		_plugin = this;
		_handle = getHandle();
	}

	protected void stop(ComponentContext context) {
		COMVS_IO.close(_handle);
		_handle = -1;
		_plugin = null;
	}

}
