package org.avm.elementary.management.addons;

import org.avm.elementary.management.core.Management;

public class Utils {
	/**
	 * replace une chaine de caractere par une autre ex: ftp://10.1.2.9/$u en
	 * ftp://10.1.2.9/toto
	 */
	public static String replace(String chaine, String tag, String tag2) {
		int idx = chaine.indexOf(tag);
		if (idx != -1) {
			StringBuffer buf = new StringBuffer();
			buf.append(chaine.substring(0, idx));
			buf.append(tag2);
			return buf.toString()
					+ replace(chaine.substring(idx + tag.length()), tag, tag2);
		} else {
			return chaine;
		}

	}

	public static String formatURL(String url, String vehicule,
			String exploitant, String plateform) {
		String temp;
		temp = Utils.replace(url, "$u", vehicule);
		temp = Utils.replace(temp, "$v", vehicule);

		temp = Utils.replace(temp, "$p", plateform);
		temp = Utils.replace(temp, "$e", exploitant);
		return temp;
	}

	public static String formatURL(String url) {
		String vehiculeid = System.getProperty(Management.VEHICULE_PROPERTY);
		String exploitant = System
				.getProperty(Management.EXPLOITATION_PROPERTY);
		String plateform = System.getProperty(Management.PLATEFORM_PROPERTY);
		return formatURL(url, vehiculeid, exploitant, plateform);
	}
}
