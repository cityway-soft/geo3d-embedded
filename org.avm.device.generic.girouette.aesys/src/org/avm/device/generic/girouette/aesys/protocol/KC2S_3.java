package org.avm.device.generic.girouette.aesys.protocol;

import org.avm.device.girouette.GirouetteProtocol;

/**
 * Spec AESYS : 
 * Version = v1.1.0.s3
 * Date = 17/03/2005
 * Changes = Added SPV/RPV command to change the communication baud rate.
 *
 * @author flabourot
 *
 */


public class KC2S_3 extends GirouetteProtocol {
	
	static {
		GirouetteProtocolFactory.factory.put(KC2S_3.class, new KC2S_3());
	}
	
	
	/*
	 * STX <Address> <Command> <Data Length> <Data> ETX <Checksum> STX : 0x02
	 * Adress : A - Z, toujours à A Command : commande VIS utilisée Data Length
	 * : longeur des données sur 4 char en hexa, =15 soit 000F dans notre cas
	 * Data : la destination ETX : 0x03
	 */
	private static final byte STX = 0x02;
	private static final byte ETX = 0x03;

	public byte[] generateDestination(final String destination) {
		// [STX][Address (1)][Command (3)][Data Length
		// (4)][...data...][ETX][CKS]
		final StringBuffer code = new StringBuffer();
		code.append(destination);
		// destination sur 10 chars
		while (code.length() < 10) {
			code.insert(0, "0");
		}

		// --creation trame
		final StringBuffer buf = new StringBuffer();
		// -- ajout [STX]
		buf.append((char) STX);
		// terminal A, commande VIS, taille fixe à 15 = F, message external = E,
		// XXXX caractères non utilisés
		buf.append("AVIS000FEXXXX");
		buf.append(code);
		buf.append((char) ETX);
		buf.append(checksum(buf.toString().getBytes()));
		return buf.toString().getBytes();
	}

	protected static String checksum(final byte[] buffer) {
		int cs = 0;

		cs = buffer[0];
		for (int i = 1; i < buffer.length; ++i) {
			cs = (cs + buffer[i]) % 65536;
		}
		final byte b[] = new byte[4];
		int b1 = cs / 256;
		int b2 = cs % 256;
		b[0] = getByte(b1 / 16);
		b[1] = getByte(b1 % 16);
		b[2] = getByte(b2 / 16);
		b[3] = getByte(b2 % 16);
		return new String(b);
	}

	private static byte getByte(int val) {
		return (byte) ((val >= 10) ? val + 0x37 : val + 0x30);
	}

}
