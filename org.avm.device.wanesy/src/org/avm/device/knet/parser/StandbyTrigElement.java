/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsAPCIndTrig;
import org.avm.device.knet.model.KmsCalltrig;
import org.avm.device.knet.model.KmsList;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsSmsTrig;
import org.avm.device.knet.model.KmsStandbyTrig;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class StandbyTrigElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsElement _parentAsKms;
	private KmsStandbyTrig _kmsStandbyTrig;

	private void initElementAttributes(Attributes attributes) {
		_kmsStandbyTrig = new KmsStandbyTrig();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (key.equals(ParserConstants.DELAY_ATTRIBUTE)) {
				_kmsStandbyTrig.setDelay(getInt(value, 10));
				continue;
			}

			_root.logError("unrecognized <standby> element attribute: " + key);
		}
	}

	public StandbyTrigElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;

		initElementAttributes(attributes);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("unrecognized element of <apcindtrig> : " + localName);
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
			k.addSubRoll(_kmsStandbyTrig);
			_root.setHandler(_parentAsKms);
		}
	}

	public KmsStandbyTrig getKmsStandbyTrig() {
		return _kmsStandbyTrig;
	}

	private int getInt(String value, int def) {
		int ret = def;
		try {
			ret = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {

		}
		return ret;
	}
}
