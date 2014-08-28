/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsText extends KmsMarshaller implements Kms {
	public static final String ROLE = "text";

	private String _name;
	private String _text;

	public void setAttName(String value) {
		_name = value;
	}

	public void setValue(String value) {
		_text = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.knet.model.KmsMarshaller#getRole()
	 */
	public String getRole() {
		return ROLE;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.knet.model.KmsMarshaller#toStringBuffer()
	 */
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		doc.append(" name=");
		doc.append(_name);
		doc.append(" >");
		doc.append(_text);
		doc.append("</" + ROLE + ">");
		return doc.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.knet.model.KmsMarshaller#marshal(java.io.OutputStream)
	 */
	// public void marshal(OutputStream out) throws Exception {
	// marshal(toStringBuffer(), out);
	// }
	public static class DefaultKmsFactory extends KmsFactory {
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

	public String getName() {
		return _name;
	}

	public String getTextValue() {
		return _text;
	}

}
