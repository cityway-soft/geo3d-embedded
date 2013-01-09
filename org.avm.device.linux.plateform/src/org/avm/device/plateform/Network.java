package org.avm.device.plateform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Network {

	private static final String IFCONFIG = "ifconfig | grep 'inet' | grep -v ' inet6'  | cut -d ':' -f 2 | cut -d  ' ' -f1";

	public static InetAddress[] getInetAddresses() {
		InetAddress[] result = null;
		List list = new ArrayList();
		try {
			Runtime runtime = Runtime.getRuntime();

			String[] array = { "sh", "-c", IFCONFIG };
			Process process = runtime.exec(array);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			String line;
			// while ((line = reader.readLine()) != null)
			// java.lang.System.out.println(line);

			while ((line = reader.readLine()) != null) {
				list.add(InetAddress.getByName(line));
			}
			result = new InetAddress[list.size()];
			int i = 0;
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				InetAddress address = (InetAddress) iterator.next();
				result[i++] = address;
			}

		} catch (IOException e) {
			return null;
		}

		return result;
	}

	public static void main(String[] args) {
		InetAddress[] array = Network.getInetAddresses();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				java.lang.System.out.println(array[i].getHostAddress());
			}
		}

	}
}
