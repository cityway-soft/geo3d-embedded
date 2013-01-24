package org.avm.device.afficheur;

public class Utils {
	public static final String format(String message) {
		StringBuffer buffer = new StringBuffer();
		message = message.toLowerCase();

		for (int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);

			switch (c) {
			case 'é':
			case 'ê':
			case 'è':
			case 'ë':
				buffer.append('e');
				break;
			case 'à':
			case 'ä':
				buffer.append('a');
				break;
			case 'î':
			case 'ï':
				buffer.append('i');
				break;
			case 'ô':
				buffer.append('o');
				break;
			case 'û':
			case 'ù':
			case 'ü':
				buffer.append('u');
				break;
			case 'ç':
				buffer.append('c');
				break;
			default:
				buffer.append(c);
			}
		}

		return buffer.toString().toUpperCase();
	}
}
