package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsConnect;
import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ConnectElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsElement _parentAsKms;

	private KmsConnect _kmsconnect;

	public ConnectElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		_kmsconnect = new KmsConnect();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			root.logError("unrecognized <connect> element attribute: " + key);
		}

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("unrecognized element of <connect> : " + localName);
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
		KmsRoot kmso = _parentAsKms.getKmsRoot();
		kmso.addSubRoll(_kmsconnect);

		_root.setHandler(_parentAsKms);
	}

	public KmsConnect getConnect() {
		return _kmsconnect;
	}
}
