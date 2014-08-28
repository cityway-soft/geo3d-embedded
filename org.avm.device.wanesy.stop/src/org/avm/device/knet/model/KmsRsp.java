/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsRsp extends KmsMarshaller implements Kms {
	public static final String ROLE = "rsp";
	private String _id;
	private String _action;
	private KmsText _text;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.knet.model.KmsMarshaller#toStringBuffer()
	 */
	public void setAttId(String value) {
		_id = value;
	}

	public void setAttAction(String value) {
		_action = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.knet.model.KmsMarshaller#getRole()
	 */
	public String getRole() {
		return ROLE;
	}

	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		doc.append(" id=");
		doc.append(_id);
		doc.append(" action=");
		doc.append(_action);
		doc.append(" />");
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

	public String getAppId() {
		return _id;
	}

	public String getAction() {
		return _action;
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

}
