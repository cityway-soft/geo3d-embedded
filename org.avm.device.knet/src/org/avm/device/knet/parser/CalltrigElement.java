/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsCalltrig;
import org.avm.device.knet.model.KmsList;
import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class CalltrigElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsElement _parentAsKms;
	private ListElement _parentAsList;

	private KmsCalltrig _kmscalltrig;

	private void initElementAttributes(Attributes attributes) {
		_kmscalltrig = new KmsCalltrig();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_kmscalltrig.setAttName(value);
				continue;
			}
			if (key.equals(ParserConstants.STATUS_ATTRIBUTE)) {
				_kmscalltrig.setAttStatus(value);
				continue;
			}
			if (key.equals(ParserConstants.IDENT_ATTRIBUTE)) {
				_kmscalltrig.setAttIdent(value);
				continue;
			}
			if (key.equals(ParserConstants.DATE_ATTRIBUTE)) {
				_kmscalltrig.setAttDate(value);
				continue;
			}

			_root.logError("unrecognized <calltrig> element attribute: " + key);
		}
	}

	// Appele depuis list
	public CalltrigElement(ParserHandler rootlist, ListElement parent,
			Attributes attributes) {
		this._root = rootlist;
		this._parentAsList = parent;

		initElementAttributes(attributes);
	}

	public CalltrigElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;

		initElementAttributes(attributes);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("unrecognized element of <calltrig> : " + localName);
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
		if (_parentAsList != null) {
			KmsList k = _parentAsList.getKmsList();
			k.addCalltrig(_kmscalltrig);
			_root.setHandler(_parentAsList);
		} else if (_parentAsKms != null) {
			KmsRoot k = _parentAsKms.getKmsRoot();
			k.addCalltrig(_kmscalltrig);
			_root.setHandler(_parentAsKms);
		}
	}

	public KmsCalltrig getCalltrig() {
		return _kmscalltrig;
	}
}
