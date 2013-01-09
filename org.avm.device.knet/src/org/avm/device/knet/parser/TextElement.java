/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsMsg;
import org.avm.device.knet.model.KmsText;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class TextElement extends DefaultHandler {
	private ParserHandler _root;

	private MsgElement _parentAsMsg;

	private KmsText _kmstext;

	public TextElement(ParserHandler root, MsgElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsMsg = parent;
		_kmstext = new KmsText();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_kmstext.setAttName(value);
				continue;
			}

			root.logError("unrecognized <text> element attribute: " + key);
		}

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		_root.logError("unrecognized element of <text> : " + localName);
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
		_kmstext.setValue(new String(desc));
	}

	public void endElement(String uri, String localName, String qName) {
		KmsMsg msg = _parentAsMsg.getKmsMsg();
		msg.addText(_kmstext);

		_root.setHandler(_parentAsMsg);
	}

	public KmsText getText() {
		return _kmstext;
	}
}
