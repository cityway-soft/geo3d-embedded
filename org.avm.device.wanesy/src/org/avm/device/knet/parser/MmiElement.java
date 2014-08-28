/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsMmi;
import org.avm.device.knet.model.KmsMsg;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class MmiElement extends DefaultHandler {
	private ParserHandler _root;

	private MsgElement _parentAsMsg;

	private KmsMmi _mmi;

	public MmiElement(ParserHandler root, MsgElement element,
			Attributes attributes) {
		this._root = root;
		this._parentAsMsg = element;
		_mmi = new KmsMmi();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			root.logError("unrecognized <mmi> element attribute: " + key);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (localName.equals(ParserConstants.NEW_ELEMENT)) {
			_root.setHandler(new NewElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.LABEL_ELEMENT)) {
			_root.setHandler(new LabelElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.UPDATE_ELEMENT)) {
			_root.setHandler(new UpdateElement(_root, this, attributes));
			return;
		}
		// if (localName.equals(ParserConstants.RSP_ELEMENT)) {
		// _root.setHandler(new RspElement(_root, this, attributes));
		// return;
		// }
		// if (localName.equals(ParserConstants.TEXT_ELEMENT)) {
		// _root.setHandler(new TextElement(_root, this, attributes));
		// return;
		// }

		_root.logError("Unrecognized element of <mmi> : " + localName);
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
		KmsMsg st = _parentAsMsg.getKmsMsg();
		st.addMmi(_mmi);
		_root.setHandler(_parentAsMsg);
	}

	public KmsMmi getKmsMmi() {
		return _mmi;
	}

}
