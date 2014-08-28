package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsStandbyTrig extends KmsMarshaller implements Kms {

	public static final String ROLE = "standby";

	private int _delay = -1;

	public int getDelay() {
		return _delay;
	}

	public void setDelay(int _delay) {
		this._delay = _delay;
	}

	public KmsStandbyTrig() {
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
		doc.append("<" + ROLE);

		if (_delay != -1) {
			doc.append(" delay=");
			doc.append("\"");
			doc.append(_delay);
			doc.append("\" ");
		}
		doc.append("/>");

		return doc.toString();
		// return "<smstrig name=\"sms\" />";
	}

	public static class DefaultKmsFactory extends KmsFactory {
		// public KmsMarshaller create() {
		// KmsAuth ko = new KmsAuth();
		// return ko;
		// }

		public Kms create() {
			KmsStandbyTrig ko = new KmsStandbyTrig();
			return ko;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
