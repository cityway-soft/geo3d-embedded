/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsNew extends KmsMarshaller implements Kms {
	public static final String ROLE = "new";
	private String _id;
	private String _name;
	private int _timeOut;
	private String _menuId;

	public KmsNew() {
		super();
	}

	// Appele depuis DialogIn
	public KmsNew(String name, String appid, String menuid, int timeout) {
		setName(name);
		setAttId(appid);
		setAttMenuId(menuid);
		setAttTimeout(timeout);
	}

	private void setAttTimeout(int timeout) {
		_timeOut = timeout;
	}

	private void setAttMenuId(String menuid) {
		_menuId = menuid;
	}

	public String getRole() {
		return ROLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.kerlink.kms.KmsMarshaller#toStringBuffer() <new
	 *      name="SAISIE_VALEUR" id="attente saisie authentification"/>
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

	public void setMenuId(String value) {
		_menuId = value;
	}

	public void setTimeout(String value) {
		_timeOut = Integer.parseInt(value);
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

		protected String createXMLString() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
