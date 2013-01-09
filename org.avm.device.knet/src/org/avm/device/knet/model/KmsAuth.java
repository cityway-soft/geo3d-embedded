package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsAuth extends KmsMarshaller implements Kms {

	public static final String ROLE = "auth";

	private String _login;

	private String _passwd;

	private String _result;

	public String getResult() {
		return _result;
	}

	public void setResult(String result) {
		_result = result;
	}

	public KmsAuth(String login, String passwd) {
		this();
		_login = login;
		_passwd = passwd;
	}

	public KmsAuth() {
	}

	public String getLogin() {
		return _login;
	}

	public void setLogin(String login) {
		_login = login;
	}

	public String getPasswd() {
		return _passwd;
	}

	public void setPasswd(String passwd) {
		_passwd = passwd;
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

	// public void marshal(OutputStream out) throws Exception {
	// marshal(toStringBuffer(), out);
	// }
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<auth ");
		if (_login != null) {
			doc.append("login=");
			doc.append("\"" + _login + "\" ");
		}
		if (_passwd != null) {
			doc.append("passwd=");
			doc.append("\"" + _passwd + "\" ");
		}
		if (_result != null) {
			doc.append("res=");
			doc.append("\"" + _result + "\" ");
		}
		doc.append("/>");
		return doc.toString();
	}

	public static class DefaultKmsFactory extends KmsFactory {
		// public KmsMarshaller create() {
		// KmsAuth ko = new KmsAuth();
		// return ko;
		// }

		public Kms create(String login, String passwd, String kmsApp) {
			KmsAuth ko = new KmsAuth(login, passwd);
			return ko;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
