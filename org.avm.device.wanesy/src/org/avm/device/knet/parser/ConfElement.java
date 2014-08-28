/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsConf;
import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class ConfElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsElement _parentAsKms;

	private KmsConf _kmsconf;

	public ConfElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		_kmsconf = new KmsConf();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (key.equals(ParserConstants.RES_ATTRIBUTE)) {
				_kmsconf.setResult(value);
				continue;
			}

			root.logError("unrecognized <conf> element attribute: " + key);
		}

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("unrecognized element of <conf> : " + localName);
	}

	public void characters(char[] ch, int start, int length) {
		int end = start + length;
		for (int i = start; i < end; i++) {
			if (!Character.isWhitespace(ch[i])) {
				break;
			}
		}
		char[] desc = new char[length];
		System.arraycopy(ch, start, desc, 0, length);
		_kmsconf.setDescription(new String(desc));
	}

	public void endElement(String uri, String localName, String qName) {
		KmsRoot kmso = _parentAsKms.getKmsRoot();
		kmso.addSubRoll(_kmsconf);

		_root.setHandler(_parentAsKms);
	}

	public KmsConf getConf() {
		return _kmsconf;
	}
}
