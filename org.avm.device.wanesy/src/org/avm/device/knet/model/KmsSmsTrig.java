package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsSmsTrig extends KmsMarshaller implements Kms {

	public static final String ROLE = "smsTrig";

	private String _result;
	
	private String _ident;
	
	private String _message;
	
	private String _date;
	
	private boolean activateMode = false;

	public String getResult() {
		return _result;
	}

	public void setResult(String result) {
		_result = result;
	}
	
	public String getIdent() {
		return _ident;
	}

	public void setIdent(String ident) {
		_ident = ident;
	}
	
	public String getMessage() {
		return _message;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public String getDate() {
		return _date;
	}

	public void setDate(String date) {
		_date = date;
	}
	
	
	public KmsSmsTrig() {
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
			return "<smstrig name=\"sms\" />";
		}
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		
		if (_ident != null) {
			doc.append(" ident=");
			doc.append("\"");
			doc.append(_ident);
			doc.append("\" ");
		}
		if (_message != null) {
			doc.append(" message=");
			doc.append("\"");
			doc.append(_message);
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
		//return "<smstrig name=\"sms\" />";
	}

	public static class DefaultKmsFactory extends KmsFactory {
		// public KmsMarshaller create() {
		// KmsAuth ko = new KmsAuth();
		// return ko;
		// }

		public Kms create() {
			KmsSmsTrig ko = new KmsSmsTrig();
			return ko;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
