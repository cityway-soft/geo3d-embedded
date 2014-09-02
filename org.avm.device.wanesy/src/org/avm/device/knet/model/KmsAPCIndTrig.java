package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsAPCIndTrig extends KmsMarshaller implements Kms {

	public static final String ROLE = "APCIndTrig";

	private String _state;
	
	private boolean activateMode = false;

	
	
	public String getState() {
		return _state;
	}

	public void setState(String state) {
		_state = state;
	}
	
	
	public KmsAPCIndTrig() {
	}

	public String getRole() {
		return ROLE;
	}

	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			marshal(out);
			out.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return ROLE + " : " + out.toString();
	}
	
	public void setActivateMode (boolean activate){
		activateMode = activate;
	}

	// public void marshal(OutputStream out) throws Exception {
	// marshal(toStringBuffer(), out);
	// }
	public String toXMLString() {
		if (activateMode){
			return "<apcindtrig name=\"apcind\" />";
		}
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		
		if (_state != null) {
			doc.append(" state=");
			doc.append("\"");
			doc.append(_state);
			doc.append("\" ");
		}
		doc.append("/>");
		
		return doc.toString();
		//return "<smstrig name=\"sms\" />";
	}

	public static class DefaultKmsFactory extends KmsFactory {
		// public KmsMarshaller create() {
		// KmsAuth ko = new KmsAuth();
		// return ko;
		// }

		public Kms create() {
			KmsAPCIndTrig ko = new KmsAPCIndTrig();
			return ko;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
