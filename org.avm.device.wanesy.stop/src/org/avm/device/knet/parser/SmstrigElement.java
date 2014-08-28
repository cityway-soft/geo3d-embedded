/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsSmsTrig;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SmstrigElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsElement _parentAsKms;
	private KmsSmsTrig _kmsSmstrig;

	private void initElementAttributes(Attributes attributes) {
		_kmsSmstrig = new KmsSmsTrig();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (key.equals(ParserConstants.IDENT_ATTRIBUTE)) {
				_kmsSmstrig.setIdent(value);
				continue;
			}
			if (key.equals(ParserConstants.DATE_ATTRIBUTE)) {
				_kmsSmstrig.setDate(value);
				continue;
			}
			if (key.equals(ParserConstants.MSG_ATTRIBUTE)) {
				_kmsSmstrig.setMessage(value);
				continue;
			}
			if (key.equals(ParserConstants.REPORT_ATTRIBUTE)) {
				_kmsSmstrig.setResult(value);
				continue;
			}

			_root.logError("unrecognized <smstrig> element attribute: " + key);
		}
	}


	public SmstrigElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;

		initElementAttributes(attributes);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("unrecognized element of <calltrig> : " + localName);
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
		if (_parentAsKms != null) {
			KmsRoot k = _parentAsKms.getKmsRoot();
			k.addSubRoll(_kmsSmstrig);
			_root.setHandler(_parentAsKms);
		}
	}

	public KmsSmsTrig getCalltrig() {
		return _kmsSmstrig;
	}
}
