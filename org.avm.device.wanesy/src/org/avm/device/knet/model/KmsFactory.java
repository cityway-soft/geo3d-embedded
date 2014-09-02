package org.avm.device.knet.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.avm.device.knet.parser.ParserHandler;
import org.xml.sax.SAXException;

public abstract class KmsFactory {
	
	private static Logger _log = Logger.getInstance(KmsFactory.class.getClass());

	public static java.util.Map factories = new java.util.HashMap();

	// protected abstract KmsMarshaller create(DocumentKMS document) throws
	// Exception;
	// protected abstract String createXMLString() throws Exception;
	// public static KmsMarshaller create(int role) {
	// KmsMarshaller kms = null;
	// switch (role) {
	// case KmsConnect.ID:
	// kms = KmsConnect.DefaultKmsFactory.create();
	// break;
	// }
	// return kms;
	// }

	/**
	 * @param in
	 *            input stream ie. le flux lu dans la socket.
	 * @return l'objet KMS lu dans le flux
	 * @throws Exception
	 */
	public static final Kms unmarshal(InputStream in) throws Exception {
		Kms kms = null;
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			parserFactory.setValidating(false);
			SAXParser saxParser = parserFactory.newSAXParser();
			// System.out.println("[KNET] Debut du parsing ...");
			ParserHandler ph = new ParserHandler();
			saxParser.parse(in, ph);
			kms = ph.getKms();
			// if (kms instanceof KmsRoot){
			// if (!KmsPosition.ROLE.equals(((KmsRoot)kms).getSubRole())){
			// // System.out.println("[KNET] Fin du parsing :"+kms);
			// }
			// }
			// System.out.println("[KNET] Fin du parsing :"+kms);
		} catch (IOException e) {
			_log.error (e);
			System.err
					.println("[KNET] IOException attempting to parse KMS message.\n"
							+ e.getMessage());
		} catch (SAXException e) {
			_log.error (e);
			System.err
					.println("[KNET] SAXException attempting to parse KMS message.\n"
							+ e.getMessage());
			ByteArrayInputStream bais = (ByteArrayInputStream) in;
			byte[] buffer = new byte[8192];
			bais.read(buffer, 0, buffer.length);
			System.err.println("[KNET] " + new String(buffer));
		} catch (ParserConfigurationException e) {
			_log.error (e);
			System.err
					.println("[KNET] ParserConfigurationException attempting to parse KMS message.\n"
							+ e.getMessage());
		}
		// return ((auth == null)?(KmsMarshaller)kro:(KmsMarshaller)auth);
		return kms;
	}

	static {
		try {
			Class.forName(KmsAuth.class.getName());
			Class.forName(KmsBeep.class.getName());
			Class.forName(KmsCall.class.getName());
			Class.forName(KmsCalltrig.class.getName());
			Class.forName(KmsConnect.class.getName());
			Class.forName(XmlError.class.getName());
			Class.forName(KmsInputtrig.class.getName());
			Class.forName(KmsList.class.getName());
			Class.forName(KmsMmi.class.getName());
			Class.forName(KmsMsg.class.getName());
			Class.forName(KmsPosition.class.getName());
			Class.forName(KmsPoweroff.class.getName());
			Class.forName(KmsStats.class.getName());
			Class.forName(KmsStop.class.getName());
			Class.forName(KmsSystem.class.getName());
			Class.forName(KmsSendSms.class.getName());
			Class.forName(KmsSmsTrig.class.getName());
			Class.forName(KmsStopReqTrig.class.getName());
			Class.forName(KmsAPCIndTrig.class.getName());
			Class.forName(KmsStandbyTrig.class.getName());
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
