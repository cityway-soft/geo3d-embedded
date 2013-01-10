/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsPoweroff;
import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class PoweroffElement extends DefaultHandler {
	private ParserHandler _root;
	private KmsElement _parentAsKms;

	private KmsPoweroff _kmsPoweroff;

	public PoweroffElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		_kmsPoweroff = new KmsPoweroff();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			root.logError("unrecognized <poweroff> element attribute: " + key);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("Unrecognized element of <poweroff> : " + localName);
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
		kmso.addPoweroff(_kmsPoweroff);

		_root.setHandler(_parentAsKms);
	}

	public KmsPoweroff getKmsPoweroff() {
		return _kmsPoweroff;
	}

}
