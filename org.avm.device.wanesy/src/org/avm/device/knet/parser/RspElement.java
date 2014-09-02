/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsMsg;
import org.avm.device.knet.model.KmsRsp;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class RspElement extends DefaultHandler {
	private ParserHandler _root;
	private MsgElement _parentAsMsg;

	private KmsRsp _kmsrsp;

	public RspElement(ParserHandler root, MsgElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsMsg = parent;
		_kmsrsp = new KmsRsp();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.ID_ATTRIBUTE)) {
				_kmsrsp.setAttId(value);
				continue;
			}
			if (key.equals(ParserConstants.ACTION_ATTRIBUTE)) {
				_kmsrsp.setAttAction(value);
				continue;
			}

			root.logError("unrecognized <rsp> element attribute: " + key);
		}

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		_root.logError("unrecognized element of <rsp> : " + localName);
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
		KmsMsg msg = _parentAsMsg.getKmsMsg();
		msg.addRsp(_kmsrsp);

		_root.setHandler(_parentAsMsg);
	}

	public KmsRsp getRsp() {
		return _kmsrsp;
	}
}
