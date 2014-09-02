/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsStandbyTrig;
import org.avm.device.knet.model.KmsStopReqTrig;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class StopReqTrigElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsElement _parentAsKms;
	private KmsStopReqTrig kmsStopReqTrig;

	private void initElementAttributes(Attributes attributes) {
		kmsStopReqTrig = new KmsStopReqTrig();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (key.equals(ParserConstants.CAUSE_ATTRIBUTE)) {
				kmsStopReqTrig.setCause(value);
				continue;
			}

			_root.logError("unrecognized <stopreq> element attribute: " + key);
		}
	}

	public StopReqTrigElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;

		initElementAttributes(attributes);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

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
		if (_parentAsKms != null) {
			KmsRoot k = _parentAsKms.getKmsRoot();
			k.addSubRoll(kmsStopReqTrig);
			_root.setHandler(_parentAsKms);
		}
		else{
			_root.setHandler(_root);
		}
	}

	public KmsStopReqTrig getKmsStopReqTrig() {
		return kmsStopReqTrig;
	}

}
