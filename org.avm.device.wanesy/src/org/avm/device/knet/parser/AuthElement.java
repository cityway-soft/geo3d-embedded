package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsAuth;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AuthElement extends DefaultHandler {
	private ParserHandler _root;
	private AuthElement _parentAsAuth;
	private KmsAuth _kmsAuth;

	public AuthElement(ParserHandler root, Attributes attributes) {
		this._root = root;
		this._parentAsAuth = null;
		this._kmsAuth = root.getKmsAsKmsAuth();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.RES_ATTRIBUTE)) {
				_kmsAuth.setResult(value);
				continue;
			}
			if (key.equals(ParserConstants.KNET_ATTRIBUTE)) {
				_kmsAuth.setKnet(value);
				continue;
			}
			if (key.equals(ParserConstants.KNETIP_ATTRIBUTE)) {
				_kmsAuth.setKnetip(value);
				continue;
			}
			if (key.equals(ParserConstants.KNETPORT_ATTRIBUTE)) {
				_kmsAuth.setKnetport(value);
				continue;
			}
			if (key.equals(ParserConstants.ATTACHDIR_ATTRIBUTE)) {
				_kmsAuth.setAttachdir(value);
				continue;
			}
			if (key.equals(ParserConstants.VERSION_ATTRIBUTE)) {
				_kmsAuth.setVersion(value);
				continue;
			}
			_root.logError("unrecognized <auth> element attribute: " + key);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		_root.logError("unrecognized element of <auth> : " + localName);
	}

	public void characters(char[] ch, int start, int length) {
		int end = start + length;
		for (int i = start; i < end; i++) {
			if (!Character.isWhitespace(ch[i])) {
				_root.logError("element body must be empty");
			}
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (_kmsAuth.getIdentifiant() == null) {
			_root.logError("no name ...");
		}
		if (_root.isError()) {
			_root.setError(false);
		}
		if (_parentAsAuth != null)
			_root.setHandler(_parentAsAuth);
		else
			_root.setHandler(_root);
	}

}