package org.avm.business.site.client.common;

public class Util {
	public static int getHeure(String val) {
		int res = 0;
		try {
			int idx = val.indexOf(":");
			int start = 0;
			if (val.startsWith("0")) {
				start = 1;
			}
			int HH = Integer.parseInt(val.substring(start, idx).trim());
			int MM = Integer.parseInt(val.substring(idx + 1).trim());
			res = HH * 60 + MM;
		} catch (Throwable t) {

		}
		return res;
	}

	public static String formatHeure(int time) {
		// int ss = (int)((time) % 60);
		int mm = (int) ((time / 60) % 60);
		int hh = (int) ((time / 3600) % 24);

		StringBuffer buf = new StringBuffer();
		return ((hh < 10) ? "0" + hh : "" + hh) + ":"
				+ ((mm < 10) ? "0" + mm : "" + mm);
	}
}
