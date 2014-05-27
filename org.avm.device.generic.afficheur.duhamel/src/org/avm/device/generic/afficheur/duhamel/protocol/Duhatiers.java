package org.avm.device.generic.afficheur.duhamel.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.afficheur.Utils;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur DEVICE_DESCRIPTION :
 * Controleur d'afficheur Mobitec DEVICE_MANUFACTURER : mobitec DEVICE_MODEL :
 * org.avm.device.afficheur.mobitec DEVICE_NAME :
 * org.avm.device.afficheur.mobitec DEVICE_SERIAL :
 * 4df3687a-9b67-46c5-b83f-b581c98feff2 DEVICE_VERSION : 1.0.0 url :
 * rs485:2;baudrate
 * =1200;stopbits=2;parity=even;bitsperchar=7;autocts=off;autorts=off protocol :
 * NSI
 * 
 */
public class Duhatiers extends AfficheurProtocol {

	private static final byte DESTINATAIRE_ADR = 15; // -- Les afficheurs
														// intérieurs portent
														// l’adresse : 15...20
	private static final byte EMETTEUR_ADR = 0; // -- calculateur=0
	private static final byte FONCTION_AFFICHEUR = 32;
	private static final byte SOUS_FONCTION_AFFICHEUR = 29;
	public static byte STX = 0x02;
	public static byte ETX = 0x03;

	private Logger _log = Logger.getInstance(this.getClass());
	static {
		AfficheurProtocolFactory.factory.put(Duhatiers.class, new Duhatiers());
	}

	public Duhatiers() {
	}

	private byte formatDestAddress(int addr) {
		// 5 bits de poids fort de l'octet + 3 bits de poids faibles à 0
		int a = addr << 3;
		return (byte) a;

	}

	private byte formatEmitterAddress(int addr, boolean moreThan128) {
		// 5 bits de poids fort de l'octet +
		int a = addr << 3;
		if (moreThan128) {
			// 1 bit B2 (4 en valeur), d’indication de poursuite d’échange (*).
			// * Ce bit Sert à transmettre plus de 128 octets de données pour
			// une fonction donnée. Ce bit doit être à 1 de la 1ere à
			// l’avant dernière trame. La dernière trame doit avoir ce bit à 0
			// pour indiquer que l’échange est terminé. ATTENTION : Répéter
			// systématiquement le numéro de sous fonction Data[0] dans chaque
			// trame avec
			a = a & 0x01;
		}
		// 2 bits de poids faibles à 0

		return (byte) a;

	}

	public void print(String message) {

		_log.debug("print " + "[" + this + "] " + message);
		byte[] buffer = generateMessage(Utils.format(message));
		_log.debug("hexa duhatiers encoded :" + toHexaString(buffer));

		try {
			purge();
			send(buffer);
			_log.debug("Request sent.");
			byte[] result = receive();
			String response = AfficheurProtocol.toHexaAscii(result);
			_log.debug("Response:" + response);

		} catch (IOException e) {
			// retry one
			try {
				send(buffer);
			} catch (IOException e1) {
				_log.error(e.getMessage(), e);
			}
		}
	}

	public byte[] generateMessage(String message) {
		
		byte[] buffer = generateMessageSousFonction29(message, 1);
		
		return buffer;
	}
	
	
	private byte[] generateMessageSousFonction29(String message, int msgRank) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		ByteArrayOutputStream frameForChecksum = new ByteArrayOutputStream();

		try {
			buffer.write(STX);
			frameForChecksum.write(formatDestAddress(DESTINATAIRE_ADR));
			frameForChecksum.write(formatEmitterAddress(EMETTEUR_ADR, false));
			frameForChecksum.write(FONCTION_AFFICHEUR);
			byte size = (byte) message.length();
			frameForChecksum.write(size + 2);
			frameForChecksum.write(SOUS_FONCTION_AFFICHEUR);
			frameForChecksum.write(msgRank);//message 1
			frameForChecksum.write(message.getBytes());
			byte crc = (byte) (checksum(frameForChecksum.toByteArray()));
			buffer.write(frameForChecksum.toByteArray());
			buffer.write(crc);
			buffer.write(ETX);
		} catch (IOException e) {
			_log.error(e);
		}

		return buffer.toByteArray();
	}

	private byte checksum(byte[] data) {
		byte result = 0;
		for (int i = 0; i < data.length; i++) {
			result = (byte) (result ^ data[i]);
		}
		return result;
	}

	private static String toHexaString(byte[] data) {
		byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

}
