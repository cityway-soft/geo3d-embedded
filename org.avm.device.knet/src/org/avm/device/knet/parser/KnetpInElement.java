package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsKnetpIn;
import org.avm.device.knet.model.KmsStats;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class KnetpInElement extends DefaultHandler {
	private ParserHandler _root;

	private StatsElement _parentAsStats;

	private KmsKnetpIn _knetpin;

	public KnetpInElement(ParserHandler root, StatsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsStats = parent;
		_knetpin = new KmsKnetpIn();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.KNETID_ATTRIBUTE)) {
				_knetpin.setKnetId(value);
				continue;
			}
			if (key.equals(ParserConstants.STATUS_ATTRIBUTE)) {
				_knetpin.setStatus(value);
				continue;
			}
			if (key.equals(ParserConstants.BEARER_ATTRIBUTE)) {
				_knetpin.setBearer(value);
				continue;
			}
			if (key.equals(ParserConstants.START_ATTRIBUTE)) {
				_knetpin.setStart(value);
				continue;
			}
			if (key.equals(ParserConstants.STOP_ATTRIBUTE)) {
				_knetpin.setStop(value);
				continue;
			}
			if (key.equals(ParserConstants.VOLTX_ATTRIBUTE)) {
				_knetpin.setVolTx(value);
				continue;
			}
			if (key.equals(ParserConstants.VOLRX_ATTRIBUTE)) {
				_knetpin.setVolRx(value);
				continue;
			}
			root.logError("unrecognized <knetpIn> element attribute: " + key);
		}

		if (_knetpin.getStatus() == null) {
			root.logError("Status not specified");
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("Unrecognized element of <knetpIn> : " + localName);
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
		KmsStats st = _parentAsStats.getKmsStats();
		st.addKnetpIn(_knetpin);

		_root.setHandler(_parentAsStats);
	}

	public KmsKnetpIn getKnetpIn() {
		return _knetpin;
	}
}
