/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsBeep extends KmsMarshaller implements Kms {
	public static final String ROLE = "beep";
	private String _duration;

	public KmsBeep() {
		super();
	}

	public KmsBeep(int duration) {
		_duration = String.valueOf(duration);
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
		if (_duration != null) {
			doc.append(" duration=");
			doc.append("\"");
			doc.append(_duration);
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

	// public void marshal(OutputStream out) throws Exception {
	// marshal(toStringBuffer(), out);
	// }
	public static class DefaultKmsFactory extends KmsFactory {

		public KmsMarshaller create(int from, int duration) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			KmsBeep beep = new KmsBeep(duration);
			kro.addSubRoll(beep);
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
