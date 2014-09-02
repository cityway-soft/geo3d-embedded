/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsInput;
import org.avm.device.knet.model.KmsInputtrig;
import org.avm.device.knet.model.KmsList;
import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class InputtrigElement extends DefaultHandler {
	private ParserHandler _root;

	private ListElement _parentAsList;
	private InputElement _parentAsInput;
	private KmsElement _parentAsKms;

	private KmsInputtrig _kmsinputtrig;

	private void initElementAttributes(Attributes attributes) {
		_kmsinputtrig = new KmsInputtrig();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (key.equals(ParserConstants.DIGITAL_ATTRIBUTE)) {
				_kmsinputtrig.setDigital(value);
				continue;
			}
			if (key.equals(ParserConstants.WAY_ATTRIBUTE)) {
				_kmsinputtrig.setWay(value);
				continue;
			}
			if (key.equals(ParserConstants.LOG_ATTRIBUTE)) {
				_kmsinputtrig.setLog(value);
				continue;
			}
			if (key.equals(ParserConstants.ADDPOS_ATTRIBUTE)) {
				_kmsinputtrig.setAddpos(value);
				continue;
			}
			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_kmsinputtrig.setAttName(value);
				continue;
			}
			if (key.equals(ParserConstants.DATE_ATTRIBUTE)) {
				_kmsinputtrig.setAttDate(value);
				continue;
			}
			if (key.equals(ParserConstants.VALUE_ATTRIBUTE)) {
				_kmsinputtrig.setAttValue(value);
				continue;
			}
			_root
					.logError("unrecognized <Inputtrig> element attribute: "
							+ key);
		}
	}

	// Appel� depuis list
	public InputtrigElement(ParserHandler rootlist, ListElement parent,
			Attributes attributes) {
		this._root = rootlist;
		this._parentAsList = parent;
		initElementAttributes(attributes);
	}

	// Appele depuis input
	public InputtrigElement(ParserHandler rootInput, InputElement parent,
			Attributes attributes) {
		this._root = rootInput;
		this._parentAsInput = parent;
		initElementAttributes(attributes);
	}

	// appele depuis kms
	public InputtrigElement(ParserHandler rootkms, KmsElement parentkmsElement,
			Attributes attributes) {
		this._root = rootkms;
		this._parentAsKms = parentkmsElement;
		initElementAttributes(attributes);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("unrecognized element of <Inputtrig> : " + localName);
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
			k.addInputtrig(_kmsinputtrig);
			_root.setHandler(_parentAsList);
		} else if (_parentAsInput != null) {
			KmsInput k = _parentAsInput.getInput();
			k.addInputtrig(_kmsinputtrig);
			_root.setHandler(_parentAsInput);
		} else if (_parentAsKms != null) {
			KmsRoot k = _parentAsKms.getKmsRoot();
			k.addSubRoll(_kmsinputtrig);
			_root.setHandler(_parentAsKms);
		}
	}

	public KmsInputtrig getInputtrig() {
		return _kmsinputtrig;
	}
}
