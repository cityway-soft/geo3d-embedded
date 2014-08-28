package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsConf extends KmsMarshaller implements Kms {

	public static final String ROLE = "conf";

	private static final String ALREADYCONNECTED = "Application already connected";

	protected String _result;

	protected String _description;

	public KmsConf() {
		super();
	}

	// //Contructeur priv� au package.
	// KmsConf(String name) {
	// super();
	// }

	public String getResult() {
		return _result;
	}

	public void setResult(String result) {
		_result = result;
	}

	public String getRole() {
		return ROLE;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		doc.append(" res=\"" + _result + "\"");
		doc.append(">");
		doc.append(_description);
		doc.append("</" + ROLE + ">");
		// System.out.println("KmsConf.doc="+doc.toString());
		return doc.toString();
	}

	public boolean IsAlreadyConnected() {
		if (_description != null) {
			if (_description.equalsIgnoreCase(ALREADYCONNECTED))
				return true;
		}
		return false;
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

	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
