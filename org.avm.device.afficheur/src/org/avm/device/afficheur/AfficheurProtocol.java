package org.avm.device.afficheur;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public abstract class AfficheurProtocol {

	public static final int STATUS_NOT_AVAILABLE = 100;
	public static final int STATUS_OK = 0;
	public static final int STATUS_ERR_NO_RESPONSE = -1;
	public static final int STATUS_ERR_INCORRECT_REPONSE = -2;
	public static final int STATUS_ERR_CANNOT_OPEN_PORT = -3;

	private OutputStream out;

	private InputStream in;

	public abstract byte[] generateMessage(String code);

	private Logger logger = Logger.getInstance(this.getClass());

	protected void send(byte[] buffer) throws IOException {
		logger.debug("Sending :" + toHexaAscii(buffer));
		out.write(buffer);
		out.flush();
	}

	protected byte[] receive() throws IOException {
		ByteArrayOutputStream out = null;
		int c = -1;
		if (in.available() > 0) {
			out = new ByteArrayOutputStream();
			while ((c = in.read()) != -1) {
				out.write(c);
			}
		} else {
			logger.warn("Nothing to read on port (?)...");
		}
		return (out != null) ? out.toByteArray() : null;
	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	public final void sendMessage(String code) throws IOException {
		byte[] buffer = generateMessage(code);
		send(buffer);
	}

	private void purge() {
		try {
			if (in.available() > 0) {
				out = new ByteArrayOutputStream();
				while (in.read() != -1)
					;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized final int getStatus() throws Exception {
		int result = STATUS_NOT_AVAILABLE;

		byte[] request = generateStatus();
		if (request != null) {
			logger.debug("Request:" + new String(request));
			try {
				byte[] response = null;
				purge();
				send(request);
				Thread.sleep(300);
				response = receive();
				if (response != null) {
					logger.debug("Response:" + toHexaAscii(response));
					result = checkStatus(toHexaAscii(response));
				} else {
					result = STATUS_ERR_NO_RESPONSE;
					logger.debug("No Response");
				}

			} catch (IOException e) {
				logger.error(e);
			}
		} else {
			result = checkStatus(null);
		}

		return result;
	}

	public static String toHexaAscii(byte[] tab) {

		StringBuffer res = new StringBuffer();
		byte b;
		for (int i = 0; i < tab.length; i++) {
			b = tab[i];
			if (b < 16 && b >= 0) {
				res.append(0);
			}
			res.append(Integer.toHexString(b & 0xFF).toUpperCase());
			// res.append(" ");
		}
		return res.toString().trim();
	}

	public static byte[] fromHexaString(String hexaString) {
		byte[] buffer = hexaString.getBytes();
		byte[] data = new byte[buffer.length / 2];
		for (int i = 0; i < data.length; i++) {
			int index = i * 2;
			int rValue = (buffer[i * 2] > 0x39) ? buffer[index] - 0x37
					: buffer[index] - 0x30;
			int lValue = (buffer[i * 2 + 1] > 0x39) ? buffer[index + 1] - 0x37
					: buffer[index + 1] - 0x30;
			data[i] = (byte) (((rValue << 4) & 0xF0) | (lValue & 0x0F));

		}
		return data;
	}
	
	protected String removeCharWithAccent(String message) {
		final StringBuffer buffer = new StringBuffer();
		message = message.toLowerCase();

		for (int i = 0; i < message.length(); i++) {
			final char c = message.charAt(i);

			switch (c) {
			case 'é':
			case 'è':
			case 'ê':
			case 'ë':
				buffer.append('e');
				break;
			case 'à':
			case 'â':
				buffer.append('a');
				break;
			case 'î':
			case 'ï':
				buffer.append('i');
				break;
			case 'ô':
				buffer.append('o');
				break;
			case 'ù':
			case 'û':
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

	public boolean isStatusAvailable() {
		return generateStatus() != null;
	}

	public byte[] generateStatus() {
		// -- pas implémenté
		return null;
	}

	public int checkStatus(String status) {
		// -- pas implémenté
		return STATUS_NOT_AVAILABLE;
	}

}
