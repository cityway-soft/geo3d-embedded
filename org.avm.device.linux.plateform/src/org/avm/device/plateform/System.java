package org.avm.device.plateform;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.osgi.framework.Version;

public class System {

	private static boolean _updated = false;

	private static WeakHashMap _map = new WeakHashMap();

	private static DateFormat _df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final Logger _log = Logger.getInstance(System.class);

	public static Long exec(String name, String args) {
		Long handle = null;
		int result = -1;
		try {
			String[] array = { "sh", "-c", name + " " + args };
			java.lang.System.out.println("[Plateform] Exec : " + array[0] + " " + array[1] + " " + array[2]);
			Process process = Runtime.getRuntime().exec(array);
			handle = new Long(process.hashCode());
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			_map.put(handle, process);
			result = process.exitValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		java.lang.System.out.println("[Plateform] Execution done.");

		return handle;

	}

	public static int kill(Long handle) {
		int result = -1;
		Object object = _map.remove(handle);
		if (object != null && object instanceof Process) {
			Process process = (Process) object;
			process.destroy();
		}
		return result;

	}

	public static boolean isOnTime() {

		if (_updated == false) {
			Plugin plugin = Activator.getPlugin();
			Version version = plugin.getVersion();
			if (version != null) {
				try {
					String qualifier = version.getQualifier();
					if (qualifier.indexOf("qualifier") != -1){
						return true;
					}
					DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					Date date = df.parse(qualifier);
					Date now = new Date();

					if (now.after(date)) {
						_log.info("[DSU] set isOnTime true");
						_updated = true;
					}
				} catch (Exception e) {
				}
			}
		}
		return _updated;
	}

	public static int settime(long seconds) {
		int result = -1;

		String text = "'" + _df.format(new Date(seconds * 1000)) + "'";
		String[] array = { "sh", "-c", "date -s " + text + " && hwclock -u -w" };

		try {
			Process process = Runtime.getRuntime().exec(array);
			process.waitFor();
			result = (process.exitValue() == 0) ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result == 1) {
			_updated = true;
		}

		return result;
	}

}
