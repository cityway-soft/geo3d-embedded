package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsInput;
import org.avm.device.knet.model.KmsList;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsStats;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class StatsElement extends DefaultHandler {
	private ParserHandler _root;

	private KmsElement _parentAsKms;
	private InputElement _parentAsInput;
	private ListElement _parentAsList;

	private KmsStats _stats;

	private void initElementAttributes(Attributes attributes) {
		_stats = new KmsStats();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.DATE_ATTRIBUTE)) {
				_stats.setDate(value);
				continue;
			}
			if (key.equals(ParserConstants.PERIOD_ATTRIBUTE)) {
				_stats.setAttPeriod(value);
				continue;
			}
			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_stats.setAttName(value);
				continue;
			}
			if (key.equals(ParserConstants.LOG_ATTRIBUTE)) {
				_stats.setAttLog(value);
				continue;
			}

			_root.logError("unrecognized <stats> element attribute: " + key);
		}
	}

	// depuis kms
	public StatsElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		initElementAttributes(attributes);
	}

	// depuis input
	public StatsElement(ParserHandler rootInput, InputElement parent,
			Attributes attributes) {
		this._root = rootInput;
		this._parentAsInput = parent;
		initElementAttributes(attributes);
	}

	// depuis list
	public StatsElement(ParserHandler rootlist, ListElement parent,
			Attributes attributes) {
		this._root = rootlist;
		this._parentAsList = parent;
		initElementAttributes(attributes);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (localName.equals(ParserConstants.SYSTEM_ELEMENT)) {
			_root.setHandler(new SystemElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.KNETPOUT_ELEMENT)) {
			_root.setHandler(new KnetpOutElement(_root, this, attributes));
			return;
		}

		_root.logError("Unrecognized element of <Stats> : " + localName);
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
		if (_parentAsKms != null) {
			KmsRoot st = _parentAsKms.getKmsRoot();
			st.addStats(_stats);
			_root.setHandler(_parentAsKms);
		} else if (_parentAsInput != null) {
			KmsInput k = _parentAsInput.getInput();
			k.addStats(_stats);
			_root.setHandler(_parentAsInput);
		} else if (_parentAsList != null) {
			KmsList kl = _parentAsList.getKmsList();
			kl.addStats(_stats);
			_root.setHandler(_parentAsList);
		}
	}

	public KmsStats getKmsStats() {
		return _stats;
	}

}
