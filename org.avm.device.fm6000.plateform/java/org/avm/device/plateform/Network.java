package org.avm.device.plateform;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.avm.device.fm6000.network.jni.COMVS_NETCONFIG;
import org.avm.device.fm6000.network.jni.COMVS_NETCONFIG_IP;

public class Network {

	public static InetAddress[] getInetAddresses() {
		java.lang.System.out.println("Network.getInetAddresses()");
		InetAddress[] result = null;
		COMVS_NETCONFIG_IP[] array = null;
		List list = new ArrayList();
		try {
			array = COMVS_NETCONFIG.Comvs_ListIPAddresses("PRISM1");
			if (array != null) {
				for (int i = 0; i < array.length; i++) {
					list.add(InetAddress.getByName((array[i]).toString()));
				}
			}
			java.lang.System.out.println("Network.getInetAddresses()");
			array = COMVS_NETCONFIG.Comvs_ListIPAddresses("ZD1201C1");
			if (array != null) {
				for (int i = 0; i < array.length; i++) {
					list.add(InetAddress.getByName((array[i]).toString()));
				}
			}
			java.lang.System.out.println("Network.getInetAddresses()");
			result = new InetAddress[list.size()];
			int i = 0;
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				InetAddress address = (InetAddress) iterator.next();
				result[i++] = address;
			}
			java.lang.System.out.println("Network.getInetAddresses()");

		} catch (Exception e) {

		}
		return result;

	}
}
