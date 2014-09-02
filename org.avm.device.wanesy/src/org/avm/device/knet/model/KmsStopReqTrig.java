package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsStopReqTrig extends KmsMarshaller implements Kms {

	public static final String ROLE = "stopreq";

	private String cause = "";

	
	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public KmsStopReqTrig() {
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

		if (cause != null) {
			doc.append(" cause=");
			doc.append("\"");
			doc.append(cause);
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
			KmsStopReqTrig ko = new KmsStopReqTrig();
			return ko;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
