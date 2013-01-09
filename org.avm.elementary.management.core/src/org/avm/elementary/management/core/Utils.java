package org.avm.elementary.management.core;


class Utils {
	/**
	 * replace une chaine de caractere par une autre ex: 
	 * replace ("ftp://10.1.2.9/$u", "$u", "toto"); => ftp://10.1.2.9/toto
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
	
	public static String formatURL(String url, String terminalId, String terminalName, String terminalOwner, String plateformType) {
		String temp;
		temp = Utils.replace(url, "$i", terminalId);
		
		temp = Utils.replace(url, "$u", terminalName);//deprecated
		temp = Utils.replace(temp, "$v", terminalName);//deprecated
		temp = Utils.replace(temp, "$n", terminalName);
		

		temp = Utils.replace(temp, "$e", terminalOwner);
		temp = Utils.replace(temp, "$o", terminalOwner);
		

		temp = Utils.replace(temp, "$p", plateformType);
		
		return temp;
	}


}
