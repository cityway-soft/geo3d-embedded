package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsMsg extends KmsMarshaller implements Kms {

	public static final String ROLE = "msg";
	private KmsMmi _mmi;
	private String _content;
	private KmsRsp _rsp;
	private KmsText _text;

	public KmsMsg() {
		super();
	}

	protected KmsMsg(String msg) {
		_content = msg;
	}

	public String getRole() {
		return ROLE;
	}

	public void setContent(String msg) {
		_content = msg;
	}

	public String getMsgContent() {
		return _content;
	}

	public String getAppId() {
		if (_rsp != null)
			return _rsp.getAppId();
		return null;
	}

	public String getAction() {
		if (_rsp != null)
			return _rsp.getAction();
		return null;
	}

	public String getTextName() {
		if (_text != null)
			return _text.getName();
		return null;
	}

	public String getTextValue() {
		if (_text != null)
			return _text.getTextValue();
		return null;
	}

	public void addMmi(KmsMmi mmi) {
		_mmi = mmi;
	}

	public void addRsp(KmsRsp rsp) {
		_rsp = rsp;
	}

	public void addText(KmsText text) {
		_text = text;
	}

	/*
	 * <msg><mmi><new name="SAISIE_VALEUR" id="attente saisie
	 * authentification"/><label name="msg1" text=""/><label name="msg2"
	 * text=""/><label name="saisie" text="Matricule:"/></mmi></msg>
	 */
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE + ">");
		if (_content != null) {
			doc.append(_content);
		}
		if (_mmi != null) {
			doc.append(_mmi.toXMLString());
		}
		if (_rsp != null) {
			doc.append(_rsp.toXMLString());
		}
		if (_text != null) {
			doc.append(_text.toXMLString());
		}
		doc.append("</" + ROLE + ">");
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
	//
	public static class DefaultKmsFactory extends KmsFactory {
		// depuis MediaKnetImpl
		public KmsMarshaller create(int from, int to, String knetId, String msg) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			kro.setAttTo(String.valueOf(to));
			kro.setAttKnetId(knetId);
			kro.addMsg(new KmsMsg(msg));
			return kro;
		}

		// Pour le code girouette
		public KmsMarshaller create(int from, int to, int knetid, String strCode) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			kro.setAttTo(String.valueOf(to));
			kro.setAttKnetId(String.valueOf(knetid));
			kro.addMsg(new KmsMsg(strCode));
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
