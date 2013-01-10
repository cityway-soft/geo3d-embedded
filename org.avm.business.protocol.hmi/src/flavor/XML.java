/*
 * Copyright (c) 1997-2004 Alexandros Eleftheriadis, Danny Hong and 
 * Yuntai Kyong.
 *
 * This file is part of Flavor, developed at Columbia University
 * (http://flavor.sourceforge.net).
 *
 * Flavor is free software; you can redistribute it and/or modify
 * it under the terms of the Flavor Artistic License as described in
 * the file COPYING.txt. 
 *
 */

/*
 * Authors:
 * Danny Hong <danny@ee.columbia.edu>
 *
 */

// XML functions
package flavor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class XML {

	static int m_nIndent = 0; // Current indentation level

	static File m_fXML;

	static FileWriter m_fXMLWriter;

	/**
	 * Put right amount of spaces for indentation according to m_nIndent.
	 */
	public static void PutIndents() {
		for (int i = 0; i <= m_nIndent; i++) {
			try {
				m_fXMLWriter.write("    ");
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Output the header for the XML file.
	 * 
	 * @param strFileName
	 *            The XML file name
	 * @param strName
	 *            The XML root tag name
	 */
	public static void CreateXmlHeader(String strFileName, String strName) {
		try {
			m_fXML = new File(strFileName);
			m_fXMLWriter = new FileWriter(m_fXML);

			// Dump header
			m_fXMLWriter
					.write("<?xml version=\"1.0\"?>\n"
							+ "<"
							+ strName
							+ "\n"
							+ "    xmlns=\"http://www.ee.columbia.edu/flavor\"\n"
							+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
							+ ">\n");
		} catch (IOException e) {
		}
	}

	/**
	 * Output the header for the XML file.
	 * 
	 * @param strFileName
	 *            The XML file name
	 * @param strName
	 *            The XML root tag name
	 * @param strSchema
	 *            The XML schema name, if existing
	 */
	public static void CreateXmlHeader(String strFileName, String strName,
			String strSchema) {
		try {
			m_fXML = new File(strFileName);
			m_fXMLWriter = new FileWriter(strFileName);

			// Dump header
			m_fXMLWriter
					.write("<?xml version=\"1.0\"?>\n"
							+ "<"
							+ strName
							+ "\n"
							+ "    xmlns=\"http://www.ee.columbia.edu/flavor\"\n"
							+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
							+ "    xsi:schemaLocation=\"http://www.ee.columbia.edu/flavor\n"
							+ "                        " + strSchema + "\"\n"
							+ ">\n");
		} catch (IOException e) {
		}
	}

	/**
	 * Finish up the XML file.
	 * 
	 * @param strName
	 *            The XML root tag name
	 */
	public static void EndXml(String strName) {
		// Dump header
		try {
			m_fXMLWriter.write("</" + strName + ">");
			m_fXMLWriter.flush();
			m_fXMLWriter.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Output the XML element for the given parsable variable
	 * 
	 * @param strElement
	 *            The element to be output
	 */
	public static void WriteXmlElement(String strElement) {
		PutIndents();
		try {
			m_fXMLWriter.write(strElement + "\n");
		} catch (IOException e) {
		}
	}

	/**
	 * Indicate that we are entering a class; keeps track of indentations and
	 * makes sure to put the begin tag for the class.
	 * 
	 * @param strName
	 *            The name of the class
	 * @param nAlign
	 *            The aligment value (if exists)
	 */
	public static void IntoAClass(String strName, int nAlign) {
		PutIndents();

		try {
			if (nAlign > 0)
				m_fXMLWriter.write("<" + strName
						+ " aligned=\" + nAlign + \">\n");
			else
				m_fXMLWriter.write("<" + strName + ">\n");
		} catch (IOException e) {
		}

		m_nIndent++;
	}

	/**
	 * Indicate that we are leaving a class; keeps track of indentations and
	 * makes sure to put the end tag for the class.
	 * 
	 * @param strName
	 *            The name of the class
	 */
	public static void OutOfClass(String strName) {
		m_nIndent--;
		PutIndents();
		try {
			m_fXMLWriter.write(strName + "\n");
		} catch (IOException e) {
		}
	}
}
