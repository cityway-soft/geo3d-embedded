/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsInput;
import org.avm.device.knet.model.KmsList;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class InputElement extends DefaultHandler {
	private ParserHandler _root;

	private ListElement _parentAsList;

	private KmsInput _kmsinput;

	public InputElement(ParserHandler root, ListElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsList = parent;
		_kmsinput = new KmsInput();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_kmsinput.setAttName(value);
				continue;
			}
			if (key.equals(ParserConstants.DIGITAL_ATTRIBUTE)) {
				_kmsinput.setDigital(value);
				continue;
			}
			if (key.equals(ParserConstants.PERIOD_ATTRIBUTE)) {
				_kmsinput.setPeriod(value);
				continue;
			}
			if (key.equals(ParserConstants.ADDPOS_ATTRIBUTE)) {
				_kmsinput.setAddpos(value);
				continue;
			}
			if (key.equals(ParserConstants.LOG_ATTRIBUTE)) {
				_kmsinput.setLog(value);
				continue;
			}
			if (key.equals(ParserConstants.TEMPERATURE_ATTRIBUTE)) {
				_kmsinput.setTemperature(value);
				continue;
			}
			root.logError("unrecognized <Input> element attribute: " + key);
		}

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (localName.equals(ParserConstants.INPUTTRIG_ELEMENT)) {
			_root.setHandler(new InputtrigElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.STATS_ELEMENT)) {
			_root.setHandler(new StatsElement(_root, this, attributes));
			return;
		}

		_root.logError("unrecognized element of <Input> : " + localName);
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
			k.addInput(_kmsinput);
			_root.setHandler(_parentAsList);
		}
	}

	public KmsInput getInput() {
		return _kmsinput;
	}
}
