/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsLabel extends KmsMarshaller {
	public static final String ROLE = "label";
	private String _text;
	private String _textId;
	private String _name;

	public KmsLabel() {
		super();
	}

	public KmsLabel(String inName) {
		setName(inName);
	}

	public String getRole() {
		return ROLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.kerlink.kms.KmsMarshaller#toStringBuffer() <label name="msg1"
	 *      text=""/>
	 */
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		doc.append(" name=");
		doc.append("\"");
		doc.append(_name);
		doc.append("\" ");
		if (_text != null) {
			doc.append(" text=");
			doc.append("\"");
			doc.append(_text);
			doc.append("\" ");
		}
		if (_textId != null) {
			doc.append(" textid=");
			doc.append("\"");
			doc.append(_textId);
			doc.append("\" ");
		}
		doc.append("/>");
		return doc.toString();
	}

	public void setAttText(String value) {
		_text = value;
	}

	public void setAttTextId(String value) {
		_textId = value;
	}

	public void setName(String value) {
		_name = value;
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
