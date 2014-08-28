package org.avm.device.knet.parser;

import org.avm.device.knet.model.KmsRoot;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KmsElement extends DefaultHandler {
	private ParserHandler _root;
	private KmsElement _parentAsKms;

	private KmsRoot _kmsRoot;

	public KmsElement(ParserHandler root, Attributes attributes) {
		this._root = root;
		this._parentAsKms = null;
		this._kmsRoot = root.getKmsAsKmsRoot();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.FROM_ATTRIBUTE)) {
				_kmsRoot.setAttFrom(value);
				continue;
			}
			if (key.equals(ParserConstants.TO_ATTRIBUTE)) {
				_kmsRoot.setAttTo(value);
				continue;
			}
			if (key.equals(ParserConstants.KNETID_ATTRIBUTE)) {
				_kmsRoot.setAttKnetId(value);
				continue;
			}
			if (key.equals(ParserConstants.RTO_ATTRIBUTE)) {
				_kmsRoot.setAttRto(value);
				continue;
			}
			if (key.equals(ParserConstants.RKNETID_ATTRIBUTE)) {
				_kmsRoot.setAttRknetId(value);
				continue;
			}
			if (key.equals(ParserConstants.REF_ATTRIBUTE)) {
				_kmsRoot.setAttRef(value);
				continue;
			}
			if (key.equals(ParserConstants.CONF_ATTRIBUTE)) {
				_kmsRoot.setAttConf(value);
				continue;
			}
			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				_kmsRoot.setName(value);
				continue;
			}
			if (key.equals(ParserConstants.DATE_ATTRIBUTE)) {
				_kmsRoot.setAttDate(value);
				continue;
			}
			if (key.equals(ParserConstants.STATUS_ATTRIBUTE)) {
				_kmsRoot.setStatus(value);
				continue;
			}
			_root.logError("unrecognized <kms> element attribute: " + key);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals(ParserConstants.CONNECT_ELEMENT)) {
			_root.setHandler(new ConnectElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.STATS_ELEMENT)) {
			_root.setHandler(new StatsElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.POSITION_ELEMENT)) {
			_root.setHandler(new PositionElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.MSG_ELEMENT)) {
			_root.setHandler(new MsgElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.CONF_ELEMENT)) {
			_root.setHandler(new ConfElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.CALLTRIG_ELEMENT)) {
			_root.setHandler(new CalltrigElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.INPUTTRIG_ELEMENT)) {
			_root.setHandler(new InputtrigElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.LIST_ELEMENT)) {
			_root.setHandler(new ListElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.SYSTEM_ELEMENT)) {
			_root.setHandler(new SystemElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.POWEROFF_ELEMENT)) {
			_root.setHandler(new PoweroffElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.SMS_ELEMENT)) {
			_root.setHandler(new SendSmsElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.SMSTRIG_ELEMENT)) {
			_root.setHandler(new SmstrigElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.APCIND_ELEMENT)) {
			_root.setHandler(new APCTrigElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.STANDBY_ELEMENT)) {
			_root.setHandler(new StandbyTrigElement(_root, this, attributes));
			return;
		}
		if (localName.equals(ParserConstants.STOPREQ_ELEMENT)) {
			_root.setHandler(new StopReqTrigElement(_root, this, attributes));
			return;
		}
		System.out.println("unrecognized element of <kms> : " + localName);
		_root.logError("unrecognized element of <kms> : " + localName);
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
		if (_kmsRoot.getIdentifiant() == null) {
			_root.logError("no name ...");
		}
		if (_root.isError()) {
			_root.setError(false);
		}
		if (_parentAsKms != null)
			_root.setHandler(_parentAsKms);
		else
			_root.setHandler(_root);
	}

	public KmsRoot getKmsRoot() {
		return _kmsRoot;
	}

}