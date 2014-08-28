package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsCall extends KmsMarshaller implements Kms {

	public static final String ROLE = "call";
	private String _act;

	public KmsCall() {
		// super("_"+ROLE);
	}

	public KmsCall(String ordre) {
		_act = ordre;
	}

	public String getRole() {
		return ROLE;
	}

	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_act != null) {
			doc.append(" act=");
			doc.append("\"");
			doc.append(_act);
			doc.append("\" ");
		}
		doc.append("/>");
		return doc.toString();
	}

	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			marshal(out);
			out.close();
		} catch (Exception e) {

		}

		return ROLE + " : " + out.toString();
	}

	// public void marshal(OutputStream out) throws Exception {
	// marshal(toStringBuffer(), out);
	// }

	public static class DefaultKmsFactory extends KmsFactory {

		public KmsMarshaller create(int from, int knetid, String ordre) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			kro.addSubRoll(new KmsCall(ordre));
			return kro;
		}

	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}
}
