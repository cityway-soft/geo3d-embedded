package org.avm.device.generic.girouette.spec.protocol;

import org.avm.device.girouette.GirouetteProtocol;

public class SPEC extends GirouetteProtocol {
	/*
	 * 
	 * 	
	 */
	private static final byte STX = 0x02;
	private static final byte ETX = 0x03;

	static {
		GirouetteProtocolFactory.factory.put(SPEC.class, new SPEC());
	}

	public byte[] generateDestination(final String destination) {
		// [STX][LG1][LG2][...data...][CS1][CS2][ETX]
		final int count = 0;
		// -- [...data...]
		final StringBuffer dest = new StringBuffer();
		dest.append(destination);
		while (dest.length() < 4) {
			dest.insert(0, "0");
		}

		final byte[] ret = new byte[dest.length() + 3];
		// -- calcul [LG] (LG=56 => LG1=35 et LG2=36)

		// --creation trame
		final StringBuffer buf = new StringBuffer();
		// -- ajout [STX]
		buf.append((char) STX);
		// -- ajout [LG1][LG2] toujours = 08 car la commande fait 5 octets +
		// CHECKx2 + ETX
		buf.append("08");
		// -- ajout data [...data...]
		buf.append('z');
		buf.append(dest.toString());
		// -- calcul [CS] (LG=12 => CS1=31 et CS2=32)
		final String cs = checksum(buf.toString().substring(1).getBytes());
		// -- ajout [CS1][CS2]
		buf.append(cs);
		buf.append((char) ETX);

		return buf.toString().getBytes();
	}

	protected static String checksum(final byte[] buffer) {
		int cs = 0;

		cs = buffer[0];
		for (int i = 1; i < buffer.length; ++i) {
			cs = cs ^ buffer[i];
		}
		cs = (byte) cs;
		final byte b[] = new byte[2];
		b[0] = ((byte) (cs / 16 + 0x30));
		b[1] = ((byte) (cs % 16 + 0x30));
		return new String(b);
	}
}
