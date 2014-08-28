package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsStats;
import org.avm.device.knet.model.KmsSystem;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SystemElement extends DefaultHandler {
	private ParserHandler _root;

	private StatsElement _parentAsStats;
	private KmsElement _parentAsKms;

	private KmsSystem _kmsSystem;

	private void initElementAttributes(Attributes attributes) {
		_kmsSystem = new KmsSystem();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.CPU_ATTRIBUTE)) {
				_kmsSystem.setAttCpu(value);
				continue;
			}
			if (key.equals(ParserConstants.MEM_ATTRIBUTE)) {
				_kmsSystem.setAttMem(value);
				continue;
			}
			if (key.equals(ParserConstants.FLASH_ATTRIBUTE)) {
				_kmsSystem.setAttFlash(value);
				continue;
			}
			if (key.equals(ParserConstants.ACT_ATTRIBUTE)) {
				_kmsSystem.setAttAct(value);
				continue;
			}
			if (key.equals(ParserConstants.CLASS_ATTRIBUTE)) {
				_kmsSystem.setAttClass(value);
				continue;
			}
			if (key.equals(ParserConstants.REPORT_ATTRIBUTE)) {
				_kmsSystem.setAttReport(value);
				continue;
			}
			if (key.equals(ParserConstants.STATUS_ATTRIBUTE)){
				_kmsSystem.setStatus(value);
				continue;
			}
			if (key.equals(ParserConstants.CAUSE_ATTRIBUTE)){
				_kmsSystem.setCause(value);
				continue;
			}
			_root.logError("unrecognized <system> element attribute: " + key);
		}
	}

	public SystemElement(ParserHandler root, StatsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsStats = parent;
		initElementAttributes(attributes);
	}

	public SystemElement(ParserHandler root, KmsElement parent,
			Attributes attributes) {
		this._root = root;
		this._parentAsKms = parent;
		initElementAttributes(attributes);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		_root.logError("<system> does not support nested elements");
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
		if (_parentAsStats != null) {
			KmsStats stats = _parentAsStats.getKmsStats();
			stats.addSystem(_kmsSystem);
			_root.setHandler(_parentAsStats);
		} else if (_parentAsKms != null) {
			KmsRoot kms = _parentAsKms.getKmsRoot();
			kms.addSubRoll(_kmsSystem);
			_root.setHandler(_parentAsKms);
		}
	}

	public KmsSystem getKmsSystem() {
		return _kmsSystem;
	}

}
