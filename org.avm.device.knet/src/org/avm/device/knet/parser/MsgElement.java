/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsMsg;
import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class MsgElement extends DefaultHandler {
	private ParserHandler _root;
	private KmsElement _parentAsKms;

	private KmsMsg _kmsMsg;

	public MsgElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		_kmsMsg = new KmsMsg();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			root.logError("unrecognized <msg> element attribute: " + key);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (localName.equals(ParserConstants.MMI_ELEMENT)) {
			_root.setHandler(new MmiElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.RSP_ELEMENT)) {
			_root.setHandler(new RspElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.TEXT_ELEMENT)) {
			_root.setHandler(new TextElement(_root, this, attributes));
			return;
		}

		_root.logError("Unrecognized element of <msg> : " + localName);
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
		_kmsMsg.setContent(new String(desc));
	}

	public void endElement(String uri, String localName, String qName) {
		KmsRoot kmso = _parentAsKms.getKmsRoot();
		kmso.addMsg(_kmsMsg);

		_root.setHandler(_parentAsKms);
	}

	public KmsMsg getKmsMsg() {
		return _kmsMsg;
	}

}
