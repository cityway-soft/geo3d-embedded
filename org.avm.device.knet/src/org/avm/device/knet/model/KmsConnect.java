package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsConnect extends KmsMarshaller implements Kms {

	public static final String ROLE = "connect";

	// public static final int ID = 3;

	public KmsConnect() {
		super();
	}

	public String getRole() {
		return ROLE;
	}

	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<connect/>");
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
		public KmsMarshaller create(String from, String to, String id) {
			KmsRoot kro = new KmsRoot(ROLE, from, to, id);
			KmsConnect con = new KmsConnect();
			kro.addConnect(con);
			return kro;
		}

	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
