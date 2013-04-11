
package org.avm.device.generic.girouette.duhamel.protocol;

import java.util.Hashtable;

import org.avm.device.girouette.GirouetteProtocol;

public class GirouetteProtocolFactory {
	
	public static Hashtable factory;
	static {
		GirouetteProtocolFactory.factory = new Hashtable();
	}
	
	public static GirouetteProtocol create(final String protocolName) {
	
		Class clazz = null;
		if (!factory.containsKey(protocolName)) {
			String classname = GirouetteProtocolFactory.class.getPackage().getName() + "." + protocolName;
			try {
				clazz = Class.forName(classname);
			}
			catch (ClassNotFoundException e) {
				// e.printStackTrace();
			}
		}
		if (clazz != null) return (GirouetteProtocol) GirouetteProtocolFactory.factory.get(clazz);
		return null;
	}
}