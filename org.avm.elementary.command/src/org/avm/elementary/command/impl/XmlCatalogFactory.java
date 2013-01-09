package org.avm.elementary.command.impl;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.impl.CatalogFactoryBase;
import org.apache.commons.chain.impl.ChainBase;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlCatalogFactory extends CatalogFactoryBase {

	private final static String CATALOG = "catalog";

	private final static String CHAIN = "chain";

	private final static String COMMAND = "command";

	private final static String CLASS = "class";

	private final static String NAME = "name";

	private Element _root;

	public XmlCatalogFactory(Reader xml) throws ParserConfigurationException,
			SAXException, IOException, ClassNotFoundException {
		super();
		DocumentBuilder documentBuilder;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		documentBuilder = factory.newDocumentBuilder();
		documentBuilder.setErrorHandler(new DefaultHandler() {
			public void error(SAXParseException e) throws SAXException {
				super.error(e);
				throw e;
			}
		});
		parse(documentBuilder, new InputSource(xml));

	}

	private void parse(DocumentBuilder documentBuilder, InputSource inputSource)
			throws SAXException, IOException, ClassNotFoundException {

		_root = documentBuilder.parse(inputSource).getDocumentElement();

		NodeList children = _root.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element childElement = (Element) children.item(i);
				String nodeName = childElement.getNodeName();
				if (CATALOG.equals(nodeName)) {
					String name = childElement.getAttribute(NAME);
					Catalog catalog = new CatalogBase();
					populateCatalog(catalog, null, childElement);
					addCatalog(name, catalog);
				}
			}
		}

	}

	private void populateCatalog(Catalog catalog, Chain parent, Element element) {
		NodeList children = element.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element childElement = (Element) children.item(i);
				String nodeName = childElement.getNodeName();
				if (CHAIN.equals(nodeName)) {
					String name = childElement.getAttribute(NAME);
					Chain chain = new ChainBase();
					if (parent != null)
						parent.addCommand(chain);
					populateCatalog(catalog, chain, childElement);
					catalog.addCommand(name, chain);
				}
				if (COMMAND.equals(nodeName)) {
					String name = childElement.getAttribute(NAME);
					String clazz = childElement.getAttribute(CLASS);
					Command command;
					try {
						command = CommandFactory.createCommand(clazz);
						if (parent != null)
							parent.addCommand(command);
						catalog.addCommand(name, command);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
	}

}
