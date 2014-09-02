package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

public class KmsPoweroff extends KmsMarshaller implements Kms {

	public static final String ROLE = "poweroff";

	public KmsPoweroff() {
		super();
	}

	public String getRole() {
		return ROLE;
	}

	/*
	 * <msg><mmi><new name="SAISIE_VALEUR" id="attente saisie
	 * authentification"/><label name="msg1" text=""/><label name="msg2"
	 * text=""/><label name="saisie" text="Matricule:"/></mmi></msg>
	 */
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE + ">");
		doc.append("</" + ROLE + ">");
		return doc.toString();
	}

	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			marshal(out);
			out.close();
		} catch (Exception e) {

		}

		return ROLE + " : " + out.toString();
	}

	public static class DefaultKmsFactory extends KmsFactory {
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
