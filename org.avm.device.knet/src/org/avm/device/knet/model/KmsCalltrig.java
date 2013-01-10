/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsCalltrig extends KmsMarshaller implements Kms {
	public static final String ROLE = "calltrig";
	private String _name;
	private String _status;
	private String _ident;
	private String _date;

	public KmsCalltrig() {
		super();
	}

	public KmsCalltrig(String name) {
		setAttName(name);
	}

	public String getRole() {
		return ROLE;
	}

	public void setAttName(String value) {
		_name = value;
	}

	public void setAttStatus(String value) {
		_status = value;
	}

	public void setAttIdent(String value) {
		_ident = value;
	}

	public void setAttDate(String value) {
		_date = value;
	}

	public String getStatus() {
		return _status;
	}

	public String getIdent() {
		return _ident;
	}

	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_name != null) {
			doc.append(" name=");
			doc.append("\"");
			doc.append(_name);
			doc.append("\" ");
		}
		if (_status != null) {
			doc.append(" status=");
			doc.append("\"");
			doc.append(_status);
			doc.append("\" ");
		}
		if (_ident != null) {
			doc.append(" ident=");
			doc.append("\"");
			doc.append(_ident);
			doc.append("\" ");
		}
		if (_date != null) {
			doc.append(" date=");
			doc.append("\"");
			doc.append(_date);
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

		public KmsMarshaller create(int from, String name) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			kro.addCalltrig(new KmsCalltrig(name));
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}
}
