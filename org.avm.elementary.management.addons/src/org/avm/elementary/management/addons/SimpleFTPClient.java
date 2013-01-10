package org.avm.elementary.management.addons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

/**
 * @author MERCUR
 * 
 */
public class SimpleFTPClient {

	private Logger _log;

	public SimpleFTPClient() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
	}

	public void get(URL remote, URL local) {

	}

	public void put(URL url, StringBuffer buffer) {
		try {
			OutputStream os = null;
			String protocol = url.getProtocol();
			if (protocol.toLowerCase().equals("ftp")) {
				URLConnection connection;
				connection = url.openConnection();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.out.println("writeFTPReport sleep interrupted !");
				}
				connection.setDoOutput(true);
				os = connection.getOutputStream();
			} else {
				String filename = url.getFile();
				File file = new File(filename);
				file = file.getParentFile();
				if (!file.exists()) {
					file.mkdirs();
				}
				os = new FileOutputStream(filename, false);
			}
			OutputStreamWriter writer = new OutputStreamWriter(os);

			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			_log.error("Simple FTP Exception :", e);
		}
	}

}
