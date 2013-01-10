package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsKnetpOut;
import org.avm.device.knet.model.KmsStats;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class KnetpOutElement extends DefaultHandler {
	private ParserHandler _root;

	private StatsElement _parentAsStats;

	private KmsKnetpOut _knetpout;

	public KnetpOutElement(ParserHandler root, StatsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsStats = parent;
		_knetpout = new KmsKnetpOut();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.KNETID_ATTRIBUTE)) {
				_knetpout.setKnetId(value);
				continue;
			}
			if (key.equals(ParserConstants.STATUS_ATTRIBUTE)) {
				_knetpout.setStatus(value);
				continue;
			}
			if (key.equals(ParserConstants.START_ATTRIBUTE)) {
				_knetpout.setStart(value);
				continue;
			}
			if (key.equals(ParserConstants.STOP_ATTRIBUTE)) {
				_knetpout.setStop(value);
				continue;
			}
			if (key.equals(ParserConstants.VOLTX_ATTRIBUTE)) {
				_knetpout.setVolTx(value);
				continue;
			}
			if (key.equals(ParserConstants.VOLRX_ATTRIBUTE)) {
				_knetpout.setVolRx(value);
				continue;
			}
			if (key.equals(ParserConstants.ID_ATTRIBUTE)) {
				_knetpout.setId(value);
				continue;
			}
			if (key.equals(ParserConstants.CLASS_ATTRIBUTE)) {
				_knetpout.setAttClass(value);
				continue;
			}
			root.logError("unrecognized <knetpOUT> element attribute: " + key);
		}

		if (_knetpout.getStatus() == null) {
			root.logError("Status not specified");
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		_root.logError("Unrecognized element of <KnetpOUT> : " + localName);
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
		st.addKnetpOut(_knetpout);

		_root.setHandler(_parentAsStats);
	}

	public KmsKnetpOut getKnetpOut() {
		return _knetpout;
	}
}
