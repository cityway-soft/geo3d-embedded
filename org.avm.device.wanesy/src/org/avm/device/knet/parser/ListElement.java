/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsList;
import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class ListElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsElement _parentAsKms;

	private KmsList _list;

	public ListElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		_list = new KmsList();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			root.logError("unrecognized <list> element attribute: " + key);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (localName.equals(ParserConstants.POSITION_ELEMENT)) {
			_root.setHandler(new PositionElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.INPUTTRIG_ELEMENT)) {
			_root.setHandler(new InputtrigElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.INPUT_ELEMENT)) {
			_root.setHandler(new InputElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.STATS_ELEMENT)) {
			_root.setHandler(new StatsElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.CALLTRIG_ELEMENT)) {
			_root.setHandler(new CalltrigElement(_root, this, attributes));
			return;
		}

		_root.logError("Unrecognized element of <list> : " + localName);
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
		KmsRoot rt = _parentAsKms.getKmsRoot();
		rt.addSubRoll(_list);
		_root.setHandler(_parentAsKms);
	}

	public KmsList getKmsList() {
		return _list;
	}

}
