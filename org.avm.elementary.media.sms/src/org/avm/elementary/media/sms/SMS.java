package org.avm.elementary.media.sms;

import java.util.Date;

public class SMS {

	/** Type du SMS */
	public boolean binary = false;

	/** Type entete du SMS */
	public boolean binaryHeader = false;

	/** Numeros de telephone de destinataire du SMS */
	public String destPhoneNumber;

	/** Numeros de telephone de l'emmeteur du SMS */
	public String originPhoneNumber;

	/** Date d'envoie ou reception du SMS */
	public String deliveryDate;

	/** Date de validite du SMS */
	public Date validityDate;

	/** Message * */
	private byte[] _data = new byte[0];

	public byte[] getMessage() {
		return _data;
	}

	public void setMessage(byte[] msg) {
		_data = msg;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		String message = new String();
		if (_data != null) {
			if (binary)
				message = MediaSMSImpl.toHexaString(_data);
			else
				message = new String(_data);
		}
		buf.append("SMS ");
		buf.append((binary ? "binary" : "texto"));
		buf.append(" : ");
		buf.append(message);
		buf.append(" from " + originPhoneNumber);
		buf.append(" at " + deliveryDate);

		return buf.toString();

	}

}
