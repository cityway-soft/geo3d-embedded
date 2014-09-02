package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsList;
import org.avm.device.knet.model.KmsPosition;
import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class PositionElement extends DefaultHandler {
	private ParserHandler _root;
	private KmsElement _parentAsKms = null;
	private ListElement _parentAsList = null;

	private KmsPosition _kmsPosition;

	private void initElementAttributes(Attributes attributes) {
		_kmsPosition = new KmsPosition();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.DATE_ATTRIBUTE)) {
				_kmsPosition.setDate(value);
				continue;
			}
			if (key.equals(ParserConstants.FIX_ATTRIBUTE)) {
				_kmsPosition.setFix(value);
				continue;
			}
			if (key.equals(ParserConstants.LAT_ATTRIBUTE)) {
				_kmsPosition.setLat(value);
				continue;
			}
			if (key.equals(ParserConstants.LONG_ATTRIBUTE)) {
				_kmsPosition.setLong(value);
				continue;
			}
			if (key.equals(ParserConstants.ALT_ATTRIBUTE)) {
				_kmsPosition.setAlt(value);
				continue;
			}
			if (key.equals(ParserConstants.COURSE_ATTRIBUTE)) {
				_kmsPosition.setCourse(value);
				continue;
			}
			if (key.equals(ParserConstants.SPEED_ATTRIBUTE)) {
				_kmsPosition.setSpeed(value);
				continue;
			}
			if (key.equals(ParserConstants.NSAT_ATTRIBUTE)) {
				_kmsPosition.setAttNbSat(value);
				continue;
			}
			if (key.equals(ParserConstants.HDOP_ATTRIBUTE)) {
				_kmsPosition.setAttHdop(value);
				continue;
			}
			if (key.equals(ParserConstants.PERIOD_ATTRIBUTE)) {
				_kmsPosition.setPeriod(value);
				continue;
			}
			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_kmsPosition.setName(value);
				continue;
			}
			if (key.equals(ParserConstants.LOG_ATTRIBUTE)) {
				_kmsPosition.setLog(value);
				continue;
			}
			if (key.equals(ParserConstants.ADDPOS_ATTRIBUTE)) {
				_kmsPosition.setAddPos(value);
				continue;
			}
			_root.logError("unrecognized <position> element attribute: " + key);
		}
	}

	public PositionElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		initElementAttributes(attributes);
	}

	// Appel� depuis list
	public PositionElement(ParserHandler rootlist, ListElement parent,
			Attributes attributes) {
		this._root = rootlist;
		this._parentAsList = parent;
		initElementAttributes(attributes);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("Unrecognized element of <position> : " + localName);
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
			k.addPosition(_kmsPosition);
			_root.setHandler(_parentAsList);
		} else {
			KmsRoot kmso = _parentAsKms.getKmsRoot();
			kmso.addSubRoll(_kmsPosition);
			_root.setHandler(_parentAsKms);
		}
	}
}
