package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class XmlError extends KmsMarshaller implements Kms {

	public static final String ROLE = "xmlerror";

	private String _description;

	public XmlError() {
		super();
	}

	public XmlError(String description) {
		super();
		_description = description;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public String getRole() {
		return ROLE;
	}

	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		doc.append(_description);
		doc.append("</xmlerror>");
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
	//
	public static class DefaultKmsFactory extends KmsFactory {
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
