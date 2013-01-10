package org.avm.device.plateform;

import org.avm.device.fm6000.system.jni.COMVS_SYSTEM;

public class System {

	private static boolean _updated = false;

	public static Long exec(String name, String args) {
		return new Long(COMVS_SYSTEM.exec(name, args));
	}

	public static int kill(Long handle) {
		return COMVS_SYSTEM.kill((int)handle.longValue());
	}

	public static int settime(long date) {
		int result =  COMVS_SYSTEM.settime(date);
		if(result == 1){
			_updated = true;
		}
		return result;
	}
	
	public static boolean isOnTime(){
		return _updated;
	}
}
