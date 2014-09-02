package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsSendSms;
import org.avm.device.knet.model.KmsStats;
import org.avm.device.knet.model.KmsSystem;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SendSmsElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsSendSms _smsElement;
	private KmsElement _parentAsKms;


	private void initElementAttributes(Attributes attributes) {
		_smsElement = new KmsSendSms();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.IDENT_ATTRIBUTE)) {
				_smsElement.setIdentifiant(value);
				continue;
			}
			if (key.equals(ParserConstants.REPORT_ATTRIBUTE)) {
				_smsElement.setResult(value);
				continue;
			}
			_root.logError("unrecognized <sms> element attribute: " + key);
		}
	}

	public SendSmsElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		initElementAttributes(attributes);
	}


	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		_root.logError("<sms> does not support nested elements");
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
		kmso.addSubRoll(_smsElement);

		_root.setHandler(_parentAsKms);
	}

	public KmsSendSms getKmsSendSms() {
		return _smsElement;
	}

}
