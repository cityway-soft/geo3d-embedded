/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsMmi;
import org.avm.device.knet.model.KmsUpdate;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class UpdateElement extends DefaultHandler {
	private ParserHandler _root;

	private MmiElement _parentAsMmi;

	private KmsUpdate _kmsupdate;

	public UpdateElement(ParserHandler root, MmiElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsMmi = parent;
		_kmsupdate = new KmsUpdate();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_kmsupdate.setName(value);
				continue;
			}
			if (key.equals(ParserConstants.ID_ATTRIBUTE)) {
				_kmsupdate.setAttId(value);
				continue;
			}
			if (key.equals(ParserConstants.MENUID_ATTRIBUTE)) {
				_kmsupdate.setAttMenuId(value);
				continue;
			}
			if (key.equals(ParserConstants.TIMEOUT_ATTRIBUTE)) {
				_kmsupdate.setAttTimeout(value);
				continue;
			}

			root.logError("unrecognized <update> element attribute: " + key);
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
		mmi.addUpdate(_kmsupdate);

		_root.setHandler(_parentAsMmi);
	}

	public KmsUpdate getUpdate() {
		return _kmsupdate;
	}
}
