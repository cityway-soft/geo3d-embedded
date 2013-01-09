package org.avm.elementary.directory.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * 
 * @author flabourot
 *
 * <cm_data>
 * 		<configuration>
 * 			<property>
 * 				<value>
 * 				</value>
 * 			</property>
 * 		</configuration>
 * </cm_data>
 *
 */
public class CMDataXMLParser {

	private static final String CONFIGURATION = "configuration";
	private static final String PROPERTY = "property";
	private static final String VALUE = "value";
	private static final String PROP_SEPARATOR = "=";
	private static final String LINE_SEPARATOR = "\n";
	private static final String DEFAULT = "<default>";
	private static final String NAME = "name";

	private static Logger _log = Logger.getInstance(CMDataXMLParser.class
			.getClass());

	/**
	 * Split a string in n string using tag as separator.
	 * @param str string to split
	 * @param tag to use as separator
	 * @return string array
	 */
	private static String[] split(final String str, final String tag) {
		final Vector v = new Vector();
		boolean ok = true;
		String tmp = str;
		String token;
		int index = 0;
		while (ok) {
			index = tmp.indexOf(tag);
			if (index != -1) {
				token = tmp.substring(0, index);
				v.addElement(token);
				tmp = tmp.substring(index + tag.length());
			} else {
				v.addElement(tmp);
				ok = false;
			}
		}
		final String[] result = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			result[i] = (String) v.elementAt(i);
		}
		return result;
	}

	/**
	 * parse the data part of the xml
	 * if the string contains properties in the 
	 * key=value format, it create a properties table
	 * otherwise, it return the string
	 * @param data String to parse as data
	 * @return Object representing the date, it can be a String or a Properties
	 */
	private static Object parseData(final String data) {
		if (data.indexOf(PROP_SEPARATOR) != -1) {
			final String[] elts = split(data, LINE_SEPARATOR);
			final Properties props = new Properties();
			for (int i = 0; i < elts.length; ++i) {
				if (elts[i].length() > 0) {
					final int pos = elts[i].indexOf(PROP_SEPARATOR);
					if (pos != -1) {
						final String[] keyval = split(elts[i], PROP_SEPARATOR);
						props.put(keyval[0], keyval[1]);
					} else {
						props.put(DEFAULT, elts[i]);
					}
				}
			}
			return props;
		} else {
			return data;
		}
	}

	/**
	 * Check if there are value node
	 * call the parseValue method on it
	 * and set a property associated on it.
	 * @param property
	 * @param props
	 */
	private static void parseProperty(final Element property,
			final Properties props) {
		final NodeList nodeList = property.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); ++i) {
			if (nodeList.item(i) instanceof Element) {
				final Element elt = (Element) nodeList.item(i);
				if (VALUE.equals(elt.getNodeName())) {
					final NodeList nl = elt.getChildNodes();
					if (nl.item(0) != null && nl.item(0) instanceof Text) {
						final Text text = (Text) nl.item(0);
						//final Object local = parseData(text.getData());
						final String name = property.getAttribute(NAME);
						if (name != null) {
							//							final Properties prop = new Properties();
							//							try {
							//								final InputStream is = new ByteArrayInputStream(
							//										text.getData().getBytes("UTF-8"));
							//								prop.load(is);
							//								props.put(name, prop);
							//							} catch (final UnsupportedEncodingException e) {
							//								e.printStackTrace();
							//							} catch (final IOException e) {
							//								// TODO Auto-generated catch block
							//								e.printStackTrace();
							//							}
							props.put(name, text.getData());
						}
					}
				}
			}
		}
	}

	private static Properties parseConfiguration(final Element configuration) {
		final NodeList nodeList = configuration.getChildNodes();
		final Properties props = new Properties();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			if (nodeList.item(i) instanceof Element) {
				final Element elt = (Element) nodeList.item(i);
				if (PROPERTY.equals(elt.getNodeName())) {
					parseProperty(elt, props);
				}
			}
		}
		return props;
	}

	public static Properties parseXML(final String filename) {
		final DocumentBuilder documentBuilder;
		Properties props = null;

		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setValidating(false);
		try {
			documentBuilder = factory.newDocumentBuilder();
			final Element _root = documentBuilder.parse(filename)
					.getDocumentElement();
			final NodeList children = _root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i) instanceof Element) {
					final Element childElement = (Element) children.item(i);
					final String nodeName = childElement.getNodeName();
					if (CONFIGURATION.equals(nodeName)) {
						props = parseConfiguration(childElement);
					}
				}
			}
		} catch (final ParserConfigurationException e) {
			_log.error(e.getMessage());
			e.printStackTrace();
		} catch (final SAXException e) {
			_log.error(e.getMessage());
			e.printStackTrace();
		} catch (final IOException e) {
			_log.error(e.getMessage());
			e.printStackTrace();
		}
		return props;
	}

	private static void printProperties(final Properties pp) {
		final Enumeration en = pp.keys();

		while (en.hasMoreElements()) {
			final String key = (String) en.nextElement();
			final Object obj = pp.get(key);
			if (obj instanceof Properties) {
				final Properties props = (Properties) pp.get(key);
				System.out.println("[" + key + "]");
				final Enumeration en2 = props.keys();
				while (en2.hasMoreElements()) {
					final String inKey = (String) en2.nextElement();
					final String value = (String) props.get(inKey);
					System.out.println(inKey + "=" + value);
				}
			} else {
				System.out.println("[" + key + "]=" + (String) obj);
			}

		}

	}

	//	public static void main(final String[] args) {
	//		final Properties pp = parseXML("file:///home/flabourot/Bureau/config.xml");
	//		printProperties(pp);
	//	}
}
