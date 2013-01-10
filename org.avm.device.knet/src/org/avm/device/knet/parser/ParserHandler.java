/*******************************************************************************
 * 
 ********************************************************************************/
package org.avm.device.knet.parser;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsAuth;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.XmlError;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ParserHandler implements the methods for the DefaultHandler of the SAX
 * parser.
 */

public class ParserHandler extends DefaultHandler {

	/* set this to true to compile in debug messages */
	private static final boolean DEBUG = false;
	private Logger _log = null;
	private DefaultHandler _handler = null;

	private Kms _kms;

	private int depth;

	private boolean error;

	public ParserHandler() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		_kms = null;
	}

	public void setHandler(DefaultHandler handler) {
		this._handler = handler;
	}

	public Kms getKms() {
		return _kms;
	}

	public KmsRoot getKmsAsKmsRoot() {
		if (_kms == null)
			_kms = new KmsRoot();
		return (KmsRoot) _kms;
	}

	public KmsAuth getKmsAsKmsAuth() {
		if (_kms == null)
			_kms = new KmsAuth();
		return (KmsAuth) _kms;
	}

	public XmlError getKmsAsKmsXmlError() {
		if (_kms == null)
			_kms = new XmlError();
		return (XmlError) _kms;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isError() {
		return error;
	}

	public void logError(String msg) {
		error = true;
		_log.error("[KMS] Parser Error. ", new SAXException(msg));
	}

	public void startDocument() throws SAXException {
		_handler = this;
		depth = 0;
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {

		if (DEBUG) {
			System.out.println("[startPrefixMapping:prefix]" + prefix);
			System.out.println("[startPrefixMapping:uri]" + uri);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		depth++;

		if (DEBUG) {
			System.out.println("[startElement:begin]");
			System.out.println(" [uri]" + uri);
			System.out.println(" [localName]" + localName);
			System.out.println(" [qName]" + qName);

			int size = attributes.getLength();
			for (int i = 0; i < size; i++) {
				String key = attributes.getQName(i);
				String value = attributes.getValue(i);
				System.out.println(" [attr:" + i + ":localName]"
						+ attributes.getLocalName(i));
				System.out.println(" [attr:" + i + ":qName]"
						+ attributes.getQName(i));
				System.out.println(" [attr:" + i + ":type]"
						+ attributes.getType(i));
				System.out.println(" [attr:" + i + ":URI]"
						+ attributes.getURI(i));
				System.out.println(" [attr:" + i + ":value]"
						+ attributes.getValue(i));
			}
			System.out.println("[startElement:end]");
		}

		if (_handler != this) {
			_handler.startElement(uri, localName, qName, attributes);
			return;
		}
		if (localName.equals(ParserConstants.AUTH_ELEMENT)) {
			if (((depth == 1) && (uri.length() == 0))
					|| uri.equals(ParserConstants.SM_NAMESPACE)) {
				setHandler(new AuthElement(this, attributes));
				return;
			}
		}
		if (localName.equals(ParserConstants.KMS_ELEMENT)) {
			if (((depth == 1) && (uri.length() == 0))
					|| uri.equals(ParserConstants.SM_NAMESPACE)) {
				setHandler(new KmsElement(this, attributes));
				return;
			}
		}
		if (localName.equals(ParserConstants.XMLERROR_ELEMENT)) {
			if (((depth == 1) && (uri.length() == 0))
					|| uri.equals(ParserConstants.SM_NAMESPACE)) {
				setHandler(new XmlErrorElement(this, attributes));
				return;
			}
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (DEBUG) {
			System.out.print("[characters:begin]");
			System.out.print(new String(ch, start, length));
			System.out.println("[characters:end]");
		}

		if (_handler != this) {
			_handler.characters(ch, start, length);
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (DEBUG) {
			System.out.println("[endElement:uri]" + uri);
			System.out.println("[endElement:localName]" + localName);
			System.out.println("[endElement:qName]" + qName);
		}

		if (_handler != this) {
			_handler.endElement(uri, localName, qName);
		}
		depth--;
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		if (DEBUG) {
			System.out.println("[endPrefixMapping:prefix]" + prefix);
		}
	}

	public void endDocument() throws SAXException {
		if (DEBUG) {
			System.out.println("[endDocument]");
		}
		_log.debug("[" + _kms + "] cree...");
	}

	public void warning(SAXParseException e) throws SAXException {
		// if (DEBUG) {
		_log.warn("[warning]", e);
		// e.printStackTrace();
		// }
	}

	public void error(SAXParseException e) throws SAXException {
		// if (DEBUG) {
		_log.error("[error]", e);
		// e.printStackTrace();
		// }
	}

	public void fatalError(SAXParseException e) throws SAXException {
		// if (DEBUG) {
		_log.error("[fatalError]", e);
		// e.printStackTrace();
		// }
		throw e;
	}

}
