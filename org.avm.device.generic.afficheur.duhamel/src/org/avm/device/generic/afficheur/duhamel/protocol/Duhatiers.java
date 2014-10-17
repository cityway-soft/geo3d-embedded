package org.avm.device.generic.afficheur.duhamel.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.avm.device.afficheur.AfficheurProtocol;

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
	private static final byte FONCTION_INTERROGATION = 1;
	private static final byte FONCTION_ACQUITTEMENT = 2;
	private static final byte FONCTION_AFFICHEUR = 32;
	private static final byte SOUS_FONCTION_AFFICHEUR = 29;
	private static final byte SOUS_FONCTION_NOM_ARRET = 22;
	private static final byte SOUS_FONCTION_VIDE = -1;
	private static final byte ESC_EXTENDED_CHAR = 0x1b;
	public static byte STX = 0x02;
	public static byte ETX = 0x03;

	private Logger _log = Logger.getInstance(this.getClass());
	static {
		AfficheurProtocolFactory.factory.put(Duhatiers.class, new Duhatiers());
	}

	public Duhatiers() {
	}

	// public void print(String message) {
	//
	// _log.debug("print " + "[" + this + "] " + message);
	// byte[] buffer = generateMessage(Utils.format(message));
	// _log.debug("hexa duhatiers encoded :" + Util.toHexaString(buffer));
	//
	// try {
	// purge();
	// send(buffer);
	// _log.debug("Request sent.");
	// byte[] result = receive();
	// String response = AfficheurProtocol.toHexaAscii(result);
	// _log.debug("Response:" + response);
	//
	// } catch (IOException e) {
	// // retry one
	// try {
	// send(buffer);
	// } catch (IOException e1) {
	// _log.error(e.getMessage(), e);
	// }
	// }
	// }

	public byte[] generateMessage(String message) {

		byte[] buffer = generateMessageLibre(message, (byte) 1,
				DESTINATAIRE_ADR);
		// byte[] buffer = generateArret(message, DESTINATAIRE_ADR);
		return buffer;
	}

	protected byte[] escapeCharWithAccent(String message) {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		// -- specs
		// 1Bh + 82h: é
		// 1Bh + 8Ah: è
		// 1Bh + 88h: ê
		// 1Bh + 89h: ë
		// 1Bh + 85h: à
		// 1Bh + 83h: â
		// 1Bh + 8Ch: î
		// 1Bh + 93h: ô
		// 1Bh + 97h: ù
		// 1Bh + 96h: û
		// 1Bh + 81h: ü
		// 1Bh + 87h: ç

		for (int i = 0; i < message.length(); i++) {
			final char c = message.charAt(i);

			switch (c) {
			case 'é': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x82);
			}
				break;
			case 'è': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x8A);
			}
				break;
			case 'ê': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x88);
			}
				break;
			case 'ë': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x89);
			}
				break;

			case 'à': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x85);
			}
				break;
			case 'â': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x83);
			}
				break;
			case 'î': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x8C);
			}
				break;
			case 'ï': {
				// data.write(ESC_EXTENDED_CHAR);
				// data.write(0x??);
				data.write('i');
			}
				break;
			case 'ô': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x93);
			}
				break;
			case 'ù': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x97);
			}
				break;
			case 'û': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x96);
			}
				break;
			case 'ü': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x81);
			}
				break;
			case 'ç': {
				data.write(ESC_EXTENDED_CHAR);
				data.write(0x87);
			}
				break;
			default:
				data.write(c);
			}
		}

		return data.toByteArray();
	}

	private byte[] generateMessageLibre(String message, byte msgRank,
			byte destinataire) {
		byte[] result = null;

		try {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			data.write(msgRank);

			// --test byte[] dataWithAccents =
			// escapeCharWithAccent("éèêëàâîïôùûüç");
			byte[] dataWithAccents = escapeCharWithAccent(message);
			data.write(dataWithAccents);
			result = generate(FONCTION_AFFICHEUR, SOUS_FONCTION_AFFICHEUR,
					data.toByteArray(), formatDestAddress(destinataire),
					formatEmitterAddress(EMETTEUR_ADR, false));
		} catch (IOException e) {
			_log.error(e);
		}

		return result;
	}

	private byte[] generateArret(String arret, byte destinataire) {
		byte[] result = null;

		try {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			data.write(arret.getBytes());
			result = generate(FONCTION_AFFICHEUR, SOUS_FONCTION_NOM_ARRET,
					data.toByteArray(), formatDestAddress(destinataire),
					formatEmitterAddress(EMETTEUR_ADR, false));
		} catch (IOException e) {
			_log.error(e);
		}

		return result;
	}

	// public byte[] generateStatus() {
	// byte[] status = generateInterrogation(DESTINATAIRE_ADR);
	// _log.debug("status request frame=" + toHexaAscii(status));
	// return status;
	// }
	//
	// public int checkStatus(String status) {
	// byte[] expectedData = generateAcquittement(DESTINATAIRE_ADR,
	// EMETTEUR_ADR, (byte)0);
	// String expected=toHexaAscii(expectedData);
	// int result = status.compareTo(expected);
	// _log.debug("status response frame=" + status + "; expected="+expected +
	// " => " + result);
	// return result;
	// }

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
			// frameForChecksum.write(formatDestAddress(dest));
			// frameForChecksum.write(formatEmitterAddress(EMETTEUR_ADR,
			// false));
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
				data.toByteArray(), formatDestAddress(destinataire),
				formatEmitterAddress(EMETTEUR_ADR, false));

		return result;
	}

	private byte[] generateAcquittement(byte emetteur, byte destinataire,
			byte status) {
		byte[] result = null;

		ByteArrayOutputStream data = new ByteArrayOutputStream();
		data.write(status);
		result = generate(FONCTION_ACQUITTEMENT, SOUS_FONCTION_VIDE,
				data.toByteArray(), formatDestAddress(emetteur),
				formatEmitterAddress(destinataire, false));

		return result;
	}

}
