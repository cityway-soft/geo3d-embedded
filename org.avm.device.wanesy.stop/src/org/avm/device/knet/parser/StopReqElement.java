package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsAuth;
import org.avm.device.knet.model.KmsStopReqTrig;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StopReqElement extends DefaultHandler {
	private ParserHandler _root;
	private StopReqElement _parentAsAuth;
	private KmsStopReqTrig _kmsStopReq;

	public StopReqElement(ParserHandler root, Attributes attributes) {
		this._root = root;
		this._parentAsAuth = null;
		this._kmsStopReq = root.getKmsAsStopReq();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.CAUSE_ATTRIBUTE)) {
				_kmsStopReq.setCause(value);
				continue;
			}
			_root.logError("unrecognized <stopreq> element attribute: " + key);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		_root.logError("unrecognized element of <stopreq> : " + localName);
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
		if (_root.isError()) {
			_root.setError(false);
		}
		if (_parentAsAuth != null)
			_root.setHandler(_parentAsAuth);
		else
			_root.setHandler(_root);
	}

}