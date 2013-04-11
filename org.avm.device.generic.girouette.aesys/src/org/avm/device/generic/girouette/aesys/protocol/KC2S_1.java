
package org.avm.device.generic.girouette.aesys.protocol;

import org.avm.device.girouette.GirouetteProtocol;

public class KC2S_1 extends GirouetteProtocol {
	
	static {
		GirouetteProtocolFactory.factory.put(KC2S_1.class, new KC2S_1());
	}
	
	public KC2S_1() {
	
	}
	
	public byte[] generateDestination(final String destination) {
	
		// [STX][LG1][LG2][...data...][CS1][CS2][ETX]
		// -- [...data...]
		StringBuffer dest = new StringBuffer();
		dest.append(destination);
		while (dest.length() < 4) {
			dest.insert(0, "0");
		}
		String code = toHexaAscii(dest.toString());
		// -- calcul [LG] (LG=56 => LG1=35 et LG2=36)
		int len = dest.length() + 4;// z+CS1+CS2+ETX
		String slen = toHexaAscii(len);
		// --creation trame
		StringBuffer buf = new StringBuffer();
		// -- ajout [STX]
		buf.append(STX);
		// -- ajout [LG1][LG2]
		if (len < 10) {
			buf.append("30");
		}
		buf.append(slen);
		// -- ajout data [...data...]
		buf.append(Integer.toHexString((char) 'z').toUpperCase());
		buf.append(code);
		// System.out.println("append("+code+") code");
		// -- calcul [CS] (LG=12 => CS1=31 et CS2=32)
		String cs = checksum(buf.toString().getBytes());
		// -- ajout [CS1][CS2]
		cs = toHexaAscii(cs);
		// System.out.println("append("+cs+") cs");
		buf.append(cs);
		// System.out.println("append("+ETX+") etx");
		buf.append(ETX);
		return buf.toString().getBytes();
	}
	
	private String checksum(byte[] buffer) {
	
		int cs = 0;
		int i = 1;
		cs = buffer[0];
		while (i < buffer.length) {
			cs = cs ^ buffer[i];
			i++;
		}
		cs = (byte) cs;
		String hexCS = Integer.toHexString(cs).toUpperCase();
		if (cs < 10) {
			hexCS = "0" + hexCS;
		}
		return hexCS;
	}
	
	/*
	 * 
	 * 	
	 */
	private static final String STX = "02";
	
	private static final String ETX = "03";
	
	private String toHexaAscii(String str) {
	
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			result.append("3");
			result.append(str.charAt(i));
		}
		return result.toString();
	}
	
	private String toHexaAscii(int val) {
	
		return toHexaAscii(Integer.toString(val));
	}


}
