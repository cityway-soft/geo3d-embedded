package org.avm.device.generic.afficheur.mobitec.protocol;

import java.util.Hashtable;

import org.avm.device.afficheur.AfficheurProtocol;

public class AfficheurProtocolFactory {

	public static Hashtable factory;
	static {
		AfficheurProtocolFactory.factory = new Hashtable();
	}

	public static AfficheurProtocol create(final String protocolName) {

		Class clazz = null;
		if (!factory.containsKey(protocolName)) {
			String classname = AfficheurProtocolFactory.class.getPackage()
					.getName() + "." + protocolName;
			try {
				clazz = Class.forName(classname);
			} catch (ClassNotFoundException e) {
				// e.printStackTrace();
			}
		}
		if (clazz != null)
			return (AfficheurProtocol) AfficheurProtocolFactory.factory
					.get(clazz);
		return null;
	}
}