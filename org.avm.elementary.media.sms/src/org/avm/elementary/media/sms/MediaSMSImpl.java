package org.avm.elementary.media.sms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmEvent;
import org.avm.device.gsm.GsmException;
import org.avm.device.gsm.GsmInjector;
import org.avm.device.gsm.GsmRequest;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.MediaListener;

public class MediaSMSImpl implements MediaSMS, ConfigurableService,
		ManageableService, GsmInjector, Constant, ConsumerService {

	private Logger _log;

	private MediaSMSConfig _config;

	private MediaListener _messenger;

	private Gsm _gsm;

	private int _simMemorySize = -1;

	public MediaSMSImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	public void configure(Config config) {
		_config = (MediaSMSConfig) config;
	}

	public void start() {
		_log.info("Start Media SMS");
		initialize();
	}

	public void stop() {
		_log.info("Stop Media SMS");

	}

	public String getMediaId() {
		return _config.getMediaId();
	}

	public void send(Dictionary header, byte[] data) throws Exception {
		boolean binary = Boolean.valueOf((String) header.get(BINARY))
				.booleanValue();
		String id = (String) header.get(MESSAGE_DEST_ID);
		if (binary) {
			sendBinary(data, id);
		} else {
			sendTexto(data, id);
		}
	}

	public void sendBinary(byte[] msg, String number) throws Exception {
		_log.debug("sending sms binary to " + number);
		GsmRequest reqPDUMode = new GsmRequest(AT_SMS_MODE_BIN);

		byte[] hexaascii = toHexaString(msg).getBytes();

		byte[] dataToSend = buildBinarySMS(hexaascii, number);
		int sizeToSend = ((dataToSend.length - 16) / 2);

		GsmRequest reqSetMsgSize = new GsmRequest(AT_SMS_SET_RECEIVER
				+ sizeToSend + "\r", ">", ERROR, 1000);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(dataToSend);
		out.write(CTRL_Z);

		byte[] data = out.toByteArray();

		GsmRequest reqSendMessageWithPhoneDest = new GsmRequest(data, OK,
				ERROR, 10000, GsmRequest.DEFAUT_PRIORITY);

		reqPDUMode.add(reqSetMsgSize);
		reqPDUMode.add(reqSendMessageWithPhoneDest);

		_gsm.send(reqPDUMode);
		_log.info("sms binary sent to " + number);
	}

	public static String toHexaString(byte[] data) {
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

	public void sendTexto(byte[] msg, String number) throws Exception {
		_log.debug("sending sms texto to " + number);
		GsmRequest reqTextoMode = new GsmRequest(AT_SMS_MODE_TEXT);

		GsmRequest reqSetPhoneDest = new GsmRequest(AT_SMS_SET_RECEIVER
				+ number + "\r", ">", ERROR, 1000);

		String texto = new String(msg) + (char) (CTRL_Z);

		_log.debug("texto = " + texto);
		GsmRequest reqSendMessage = new GsmRequest(texto, OK, ERROR, 5000);

		reqTextoMode.add(reqSetPhoneDest);
		reqTextoMode.add(reqSendMessage);
		_gsm.send(reqTextoMode);
		_log.info("sms texto sent to " + number);
	}

	public void setMessenger(MediaListener messenger) {
		_log.debug("Initialisation du messenger " + messenger);
		_messenger = messenger;
		if (_messenger != null){
			_messenger.setMedia(this);
		}
	}

	public void setGsm(Gsm gsm) {
		_gsm = gsm;
	}

	public void unsetGsm(Gsm gsm) {
		_gsm = null;
	}

	private void initialize() {
		try {
			String response;
			// -- request sms notification
			GsmRequest command = new GsmRequest(AT_AT);
			command.add(new GsmRequest(AT_SMS_INCOMMING));
			_gsm.send(command);
			response = command.result.value;

			// -- request memory size
			GsmRequest reqUseSIMMemory = new GsmRequest(AT_SMS_USE_SIM_MEMORY);
			GsmRequest reqGetTotalMemory = new GsmRequest(
					AT_SMS_GET_TOTAL_MEMORY);
			reqUseSIMMemory.add(reqGetTotalMemory);
			_gsm.send(reqUseSIMMemory);
			response = reqGetTotalMemory.result.value;
			if (response != null) {
				StringTokenizer t = new StringTokenizer(response, ",");
				t.nextElement();
				String stotal = t.nextToken();
				_simMemorySize = Integer.parseInt(stotal);
				_log.info("SIM Memory size = " + _simMemorySize);
			}

			// -- read and notify all
			for (int i = 0; i < _simMemorySize; i++) {
				try {
					readAndNotify(i);
				} catch (Exception e) {
					_log.warn("nothing in bank " + i + " ?");
				}
			}

		} catch (GsmException e) {
			_log.error("GsmException", e);
		} catch (InterruptedException e) {
			_log.error(e);
		} catch (IOException e) {
			_log.error(e);
		} catch (Throwable t) {
			_log.error("Exception : ", t);
		}
	}

	public void delete(int bank) throws GsmException, InterruptedException,
			IOException {
		GsmRequest reqDeleteBank = new GsmRequest(AT_SMS_DELETE_BANK + bank
				+ "\r");
		_log.debug("delete sms bank " + bank);
		_gsm.send(reqDeleteBank);
		_log.debug("delete sms ok");
	}

	private void readAndNotify(int bank) throws Exception {
		SMS sms = readSMS(bank);
		_log.info("Receive : " + sms);
		Properties p = new Properties();
		p.put("origin", sms.originPhoneNumber);
		_messenger.receive(p, sms.getMessage());
		delete(bank);
	}

	public void notify(Object obj) {
		GsmEvent event = (GsmEvent) obj;
		_log.debug("GsmEvent " + event);
		switch (event.type) {
		case GsmEvent.INCOMMING_SMS: {
			_log.debug("Receiving SMS...");
			final int bank;
			String eventData = event.value;
			int idx = eventData.indexOf(",");
			if (idx != -1) {
				String sbank = eventData.substring(idx + 1);
				bank = Integer.parseInt(sbank);

				if (_messenger != null) {
					try {
						readAndNotify(bank);
					} catch (Exception e) {
						_log.error("Error on notify sms:", e);
					}

				} else {
					_log.warn("Messenger non initialise");
				}

			}
		}
			break;

		case GsmEvent.MODEM_CLOSED: {
			_log.warn(" event MODEM_CLOSED");
		}
			break;
		case GsmEvent.MODEM_OPENED: {
			_log.info(" event MODEM_OPENED");
			initialize();
		}
			break;

		}
		_log.debug("End notify...");
	}

	private SMS readSMS(int bank) throws Exception {
		GsmRequest reqPDUMode = new GsmRequest(AT_SMS_MODE_BIN);
		GsmRequest reqReadBank = new GsmRequest(AT_SMS_READ_BANK + bank + "\r");

		reqPDUMode.add(reqReadBank);
		_log.debug("read sms bank " + bank);
		_gsm.send(reqPDUMode);
		_log.debug("read sms ok");

		String response = reqReadBank.result.value;
		_log.debug("read response :" + response);
		SMS sms = null;
		if (response.startsWith("+CMGR")) {
			int idx = response.indexOf("\n");
			String data = response.substring(idx);
			sms = readBinaryMessage(data.trim());
		}
		return sms;
	}

	protected byte[] buildBinarySMS(byte[] data, String phone)
			throws IOException {
		StringBuffer buf = new StringBuffer();
		buf.append("0791");

		_log.debug("SMSC : " + _config.getSMSCenter());

		buf.append(formatPhone(_config.getSMSCenter()));// "0609001390"));
		buf.append("11");
		buf.append("00");

		String phonedestswp = formatPhone(phone);
		int phoneSize = phonedestswp.length();
		phoneSize = (phoneSize > 4) ? (phoneSize - 1) : phoneSize; // pour
		// numero court
		buf.append(toHex(phoneSize));
		buf.append("91");
		buf.append(phonedestswp);
		buf.append("00F7FF");

		int size = (data.length / 2);
		buf.append(toHex(size));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(buf.toString().getBytes());
		for (int i = 0; i < data.length; i++) {
			out.write(data[i]);
		}
		return out.toByteArray();
	}

	protected String formatPhone(String phone) {
		StringBuffer buf = new StringBuffer();
		StringBuffer b = new StringBuffer();
		if (phone.startsWith("+")) {
			phone = phone.substring(1);
		}
		if (phone.startsWith("06")) {
			phone = phone.substring(1);
			phone = "33" + phone;
		}
		if ((phone.length() % 2) == 1) {
			phone += "F";
		}

		for (int i = 0; i < phone.length(); i += 2) {
			b.append(phone.substring(i, i + 2));
			buf.append(b.reverse().toString());
			b.delete(0, 2);
		}
		return buf.toString();
	}

	private String toHex(int doublemot) {
		String b = Integer.toHexString(doublemot).toUpperCase();
		if (b.length() == 1) {
			b = "0" + b;
		}
		return b;
	}

	private SMS readBinaryMessage(String databuf) {
		Dictionary p = new Properties();

		int offset = 0;
		int scaLength = Integer.parseInt(databuf.substring(offset, offset + 2),
				16);
		_log.debug("[SMSImpl] SCALength = " + scaLength);

		offset += 2;
		String SCA = databuf.substring(offset, offset + (scaLength * 2));
		_log.debug("[SMSImpl] SCA = " + SCA);

		offset += (scaLength * 2);
		String FO = databuf.substring(offset, offset + 2);
		_log.debug("[SMSImpl] First Octet = " + FO);

		offset += 2;
		int AdLength = Integer.parseInt(databuf.substring(offset, offset + 2),
				16);
		_log.debug("[SMSImpl] Address Length = " + AdLength + " ("
				+ Integer.toHexString(AdLength) + "h)");

		offset += 2;
		String typeAd = databuf.substring(offset, offset + 2);
		_log.debug("[SMSImpl] Type Address = " + typeAd);

		offset += 2;
		int off = (AdLength % 2);
		String address = databuf.substring(offset, offset + (AdLength + off));
		address = decodePhone(address);
		_log.debug("[SMSImpl] Origin Address = " + address);
		p.put("OriginPhoneNumber", address);

		offset += (AdLength + off);
		String PID = databuf.substring(offset, offset + 2);
		_log.debug("[SMSImpl] PID = " + PID);

		offset += 2;
		String DSC = databuf.substring(offset, offset + 2);
		_log.debug("[SMSImpl] DSC = " + DSC);

		offset += 2;
		String cdate = databuf.substring(offset, offset + 14);
		Date date = parseDate(cdate);
		p.put("Delivery", date);
		_log.debug("[SMSImpl] date = " + cdate + "=>" + date);

		offset += 14;
		int uDataLength = Integer.parseInt(databuf
				.substring(offset, offset + 2), 16);
		_log.debug("[SMSImpl] uDataLength = " + uDataLength);

		offset += 2;
		String strdata = databuf.substring(offset);
		_log.debug("[SMSImpl] DATA = " + strdata);
		byte[] data = null;

		SMS msg = new SMS();
		msg.originPhoneNumber = address;
		msg.deliveryDate = cdate;
		if (!DSC.equals("F7")) {
			data = decodePDU7bit(strdata);
			msg.binary = false;
			msg.setMessage(data);
			_log.debug("[SMSImpl] msg=" + new String(data));
		} else {
			data = fromHexaString(strdata);
			msg.binary = true;
			msg.setMessage(data);
		}

		return msg;
	}

	private String decodePhone(String phone) {
		StringBuffer buf = new StringBuffer();
		StringBuffer b = new StringBuffer(2);
		for (int i = 0; i < phone.length(); i += 2) {
			b.append(phone.substring(i, i + 2));
			buf.append(b.reverse().toString());
			b.delete(0, 2);
		}
		String result = buf.toString();
		if (result.endsWith("F")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	private byte[] decodePDU7bit(String hexa) {
		int size = hexa.length() / 2;
		byte[] data = new byte[size + (size - (size % 7)) / 7];
		byte oct;
		byte mask = (byte) 0xFF;
		byte rm, aa = 0, bb;
		int cpt = 1;
		int lcpt = 0;
		int j = 0;
		for (int i = 0; i < hexa.length(); i += 2) {
			String hex = hexa.substring(i, i + 2);
			oct = (byte) (Integer.parseInt(hex, 16));
			mask = (byte) (0xFF << (8 - cpt));
			if (cpt > 1) {
				bb = (byte) (oct << lcpt);
				data[j] = (byte) ((bb | aa) & ~0x80);
			} else {
				data[j] = (byte) (oct & ~0x80);
			}

			rm = (byte) (oct & mask);
			aa = (byte) ((rm >> (8 - cpt)) & ~(0xFF << cpt));

			lcpt = cpt;

			cpt++;
			if (cpt > 7) {
				j++;
				data[j] = aa;
				cpt = 1;
			}
			j++;
		}
		return data;
	}

	private byte[] fromHexaString(String hexaString) {
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

	private Date parseDate(String date) {
		Calendar cal;
		cal = Calendar.getInstance();

		String tag;
		int i = 0;
		tag = date.substring(i + 1, i + 2) + date.substring(i, i + 1);
		cal.set(Calendar.YEAR, 2000 + Integer.parseInt(tag));

		i += 2;
		tag = date.substring(i + 1, i + 2) + date.substring(i, i + 1);
		cal.set(Calendar.MONTH, Integer.parseInt(tag));

		i += 2;
		tag = date.substring(i + 1, i + 2) + date.substring(i, i + 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tag));

		i += 2;
		tag = date.substring(i + 1, i + 2) + date.substring(i, i + 1);
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tag));

		i += 2;
		tag = date.substring(i + 1, i + 2) + date.substring(i, i + 1);
		cal.set(Calendar.MINUTE, Integer.parseInt(tag));

		i += 2;
		tag = date.substring(i + 1, i + 2) + date.substring(i, i + 1);
		cal.set(Calendar.SECOND, Integer.parseInt(tag));

		i += 2;
		tag = date.substring(i + 1, i + 2) + date.substring(i, i + 1);

		return cal.getTime();
	}

	public int getPriority() {
		return 1;
	}

}