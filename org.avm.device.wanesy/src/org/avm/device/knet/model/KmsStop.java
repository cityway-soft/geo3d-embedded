/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsStop extends KmsMarshaller implements Kms {
	public static final String ROLE = "stop";
	private String _name;

	public KmsStop() {
		super();
	}

	public KmsStop(String name) {
		setName(name);
	}

	private void setName(String name) {
		_name = name;
	}

	public String getRole() {
		return ROLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.knet.model.KmsMarshaller#toStringBuffer()
	 */
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_name != null) {
			doc.append(" name=");
			doc.append("\"");
			doc.append(_name);
			doc.append("\"");
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

	public static class DefaultKmsFactory extends KmsFactory {
		public KmsMarshaller create(int from, String name) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			KmsStop stop = new KmsStop(name);
			kro.addSubRoll(stop);
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
