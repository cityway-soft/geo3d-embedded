/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsLabel;
import org.avm.device.knet.model.KmsMmi;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class LabelElement extends DefaultHandler {
	private ParserHandler _root;

	private MmiElement _parentAsMmi;

	private KmsLabel _kmslabel;

	public LabelElement(ParserHandler root, MmiElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsMmi = parent;
		_kmslabel = new KmsLabel();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_kmslabel.setName(value);
				continue;
			}
			if (key.equals(ParserConstants.TEXT_ELEMENT)) {
				_kmslabel.setAttText(value);
				continue;
			}

			root.logError("unrecognized <label> element attribute: " + key);
		}

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		_root.logError("unrecognized element of <update> : " + localName);
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
		KmsMmi mmi = _parentAsMmi.getKmsMmi();
		mmi.addLabel(_kmslabel);

		_root.setHandler(_parentAsMmi);
	}

	public KmsLabel getLabel() {
		return _kmslabel;
	}

}
