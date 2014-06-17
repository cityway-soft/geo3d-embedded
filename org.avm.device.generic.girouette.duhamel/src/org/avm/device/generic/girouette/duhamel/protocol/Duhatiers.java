package org.avm.device.generic.girouette.duhamel.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.avm.device.girouette.GirouetteProtocol;
import org.avm.elementary.common.Util;

/**
 * DEVICE_CATEGORY : org.avm.device.girouette.Afficheur 
 * DEVICE_DESCRIPTION : Controleur de girouette Duhamel 
 * DEVICE_MANUFACTURER : duhamel 
 * DEVICE_MODEL : org.avm.device.girouette.duhamel 
 * DEVICE_NAME : org.avm.device.girouette.duhamel 
 * DEVICE_SERIAL : 4df3687a-9b67-46c5-b83f-b581c98feff2 
 * DEVICE_VERSION : 1.0.0 
 * url :  comm:4;baudrate=9600;stopbits=1;parity=none;bitsperchar=8;blocking=off
 * protocol : Duhatiers
 * 
 */
public class Duhatiers extends GirouetteProtocol {

	private static final byte DESTINATAIRE_ADR = 1; // -- Le pupitre duhamel
													// porte l'adresse 1
	private static final byte EMETTEUR_ADR = 0; // -- calculateur=0
	//
	private static final byte FONCTION_INTERROGATION = 1;
	private static final byte FONCTION_ACQUITTEMENT = 2;
	private static final byte FONCTION_GIROUETTE = 32;
	//
	private static final byte SOUS_FONCTION_DESTINATION = 10;
	private static final byte SOUS_FONCTION_VIDE = -1;
	//
	public static byte STX = 0x02;
	public static byte ETX = 0x03;

	private Logger _log = Logger.getInstance(this.getClass());
	static {
		GirouetteProtocolFactory.factory.put(Duhatiers.class, new Duhatiers());
	}

	public Duhatiers() {
	}

	public byte[] generateDestination(final String code) {
		byte[] result=null;
		try {
			int c = Integer.parseInt(code);
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			data.write(toBytes(c));
			result = generate(FONCTION_GIROUETTE, SOUS_FONCTION_DESTINATION,
					data.toByteArray(), formatDestAddress(DESTINATAIRE_ADR), formatEmitterAddress(EMETTEUR_ADR, false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	private byte[] toBytes(int i)
	{
	  byte[] result = new byte[2];
	  int c=0;
	  result[c++] = (byte) (i >> 8);
	  result[c++] = (byte) (i /*>> 0*/);

	  return result;
	}

	public byte[] generateStatus() {
		byte[] status = generateInterrogation(DESTINATAIRE_ADR);
		_log.debug("status request frame=" + toHexaAscii(status));
		return status;
	}

	public int checkStatus(String status) {
		byte[] expectedData = generateAcquittement(EMETTEUR_ADR,
				DESTINATAIRE_ADR, (byte) 0);
		String expected = toHexaAscii(expectedData);
		int result = status.compareTo(expected);
		_log.debug("status response frame=" + status + "; expected=" + expected
				+ " => " + result + " ("+new String(Util.fromHexaString(status))+")");
		return result;
	}

	/******************************************************************************************
	 * Fonctions communes Afficheur / Girourette pour le protocol DUHATIERS
	 ******************************************************************************************/

	/**
	 * generateur de trames selon les "fonctions"
	 * 
	 * @param fonction
	 * @param sousFonction
	 * @param data
	 * @param dest
	 * @return
	 */
	private byte[] generate(byte fonction, byte sousFonction, byte[] data,
			byte dest, byte emetteur) {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		ByteArrayOutputStream dataFrame = new ByteArrayOutputStream();

		ByteArrayOutputStream frameForChecksum = new ByteArrayOutputStream();

		try {
			buffer.write(STX);
//			frameForChecksum.write(formatDestAddress(dest));
//			frameForChecksum.write(formatEmitterAddress(EMETTEUR_ADR, false));
			frameForChecksum.write(dest);
			frameForChecksum.write(emetteur);
			frameForChecksum.write(fonction);
			if (sousFonction != -1) {
				dataFrame.write(sousFonction);
			}
			dataFrame.write(data);
			frameForChecksum.write(dataFrame.size());
			frameForChecksum.write(dataFrame.toByteArray());
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

	private byte[] generateInterrogation(byte destinataire) {
		byte[] result = null;

		ByteArrayOutputStream data = new ByteArrayOutputStream();
		result = generate(FONCTION_INTERROGATION, SOUS_FONCTION_VIDE,
				data.toByteArray(), formatDestAddress(destinataire), formatEmitterAddress(EMETTEUR_ADR, false));

		return result;
	}

	private byte[] generateAcquittement(byte emetteur, byte destinataire,
			byte status) {
		byte[] result = null;

		ByteArrayOutputStream data = new ByteArrayOutputStream();
		data.write(status);
		result = generate(FONCTION_ACQUITTEMENT, SOUS_FONCTION_VIDE,
				data.toByteArray(),  formatDestAddress(emetteur), formatEmitterAddress(destinataire, false));

		return result;
	}

}
