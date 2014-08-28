/**
 * 
 */
package org.avm.device.knet.parser;

import org.avm.device.knet.model.XmlError;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author lbr
 * 
 */
public class XmlErrorElement extends DefaultHandler {
	private ParserHandler _root;
	private AuthElement _parentAsAuth;

	private XmlError _xmlerror;

	public XmlErrorElement(ParserHandler root, Attributes attributes) {
		this._root = root;
		this._parentAsAuth = null;
		this._xmlerror = root.getKmsAsKmsXmlError();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			_root.logError("unrecognized <xmlerror> element attribute: " + key);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		_root.logError("unrecognized element of <xmlerror> : " + localName);
	}

	public void characters(char[] ch, int start, int length) {
		// System.out.println("start="+start+" length="+length+"["+new
		// String(ch)+"]");
		int end = start + length;
		for (int i = start; i < end; i++) {
			if (!Character.isWhitespace(ch[i])) {
				break;
			}
		}
		char[] desc = new char[length];
		System.arraycopy(ch, start, desc, 0, length);
		_xmlerror.setDescription(new String(desc));
	}

	public void endElement(String uri, String localName, String qName) {
		if (_root.isError()) {
			_root.setError(false);
		}
		if (_parentAsAuth != null)
			_root.setHandler(_parentAsAuth);
		else
			_root.setHandler(_root);
	}

}