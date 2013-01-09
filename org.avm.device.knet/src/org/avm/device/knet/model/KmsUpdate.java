/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsUpdate extends KmsMarshaller implements Kms {
	public static final String ROLE = "update";
	private String _id;
	private String _name;
	private int _timeOut;
	private String _menuId;

	public KmsUpdate() {
		super();
	}

	// appele depuis dialogIn
	public KmsUpdate(String appid, String menuid, int timeout) {
		setAttId(appid);
		setAttMenuId(menuid);
		setAttTimeout(timeout);
	}

	private void setAttTimeout(int value) {
		_timeOut = value;
	}

	public void setAttTimeout(String value) {
		_timeOut = Integer.parseInt(value);
	}

	public void setAttMenuId(String menuid) {
		_menuId = menuid;
	}

	public String getRole() {
		return ROLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.kerlink.kms.KmsMarshaller#toStringBuffer()
	 */
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_name != null) {
			doc.append(" name=");
			doc.append("\"");
			doc.append(_name);
			doc.append("\" ");
		}
		if (_id != null) {
			doc.append(" id=");
			doc.append("\"");
			doc.append(_id);
			doc.append("\" ");
		}
		if (_menuId != null) {
			doc.append(" menuid=");
			doc.append("\"");
			doc.append(_menuId);
			doc.append("\" ");
		}
		if (_timeOut > 0) {
			doc.append(" timeout=");
			doc.append("\"");
			doc.append(_timeOut);
			doc.append("\" ");
		}
		doc.append("/>");
		return doc.toString();
	}

	public void setAttId(String value) {
		_id = value;
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
