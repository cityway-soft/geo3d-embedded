package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsSendSms extends KmsMarshaller implements Kms {

	public static final String ROLE = "sendSms";

	private String _text;

	private String _dest;

	private String _result;

	public String getResult() {
		return _result;
	}

	public void setResult(String result) {
		_result = result;
	}

	public KmsSendSms(String dest, String text) {
		this();
		_dest = dest;
		_text = text;
	}

	public KmsSendSms() {
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	public String getDest() {
		return _dest;
	}

	public void setDest(String dest) {
		_dest = dest;
	}

	public String getRole() {
		return ROLE;
	}

	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			marshal(out);
			out.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return ROLE + " : " + out.toString();
	}

	// public void marshal(OutputStream out) throws Exception {
	// marshal(toStringBuffer(), out);
	// }
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<sms ");
		if (_dest != null) {
			doc.append("ident=");
			doc.append("\"" + _dest + "\" ");
		}
		if (_text != null) {
			doc.append("msg=");
			doc.append("\"" + _text + "\" ");
		}
		doc.append("/>");
		return doc.toString();
	}

	public static class DefaultKmsFactory extends KmsFactory {
		// public KmsMarshaller create() {
		// KmsAuth ko = new KmsAuth();
		// return ko;
		// }

		public Kms create(String dest, String text) {
			KmsSendSms ko = new KmsSendSms(dest, text);
			return ko;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
