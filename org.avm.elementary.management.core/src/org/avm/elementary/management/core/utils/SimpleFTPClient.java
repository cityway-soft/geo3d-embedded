package org.avm.elementary.management.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author MERCUR
 * 
 */
class SimpleFTPClient implements IRemoteClient {
	URL url;

	public SimpleFTPClient(URL url) {
		this.url = url;
	}

	public String put(StringBuffer buffer, String remoteFilename) throws IOException {
		OutputStream os = null;

		URLConnection connection;
		URL urlRemotefile = new URL(url.toExternalForm()+"/"+remoteFilename);
		connection = urlRemotefile.openConnection();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("writeFTPReport sleep interrupted !");
		}
		connection.setDoOutput(true);
		os = connection.getOutputStream();

		OutputStreamWriter writer = new OutputStreamWriter(os);

		writer.write(buffer.toString());
		writer.flush();
		writer.close();
		return "put buffer to " + remoteFilename;

	}

	public String put(File file,String remoteFilename) throws IOException {
		String filepath = file.getAbsolutePath();
		InputStream in = new BufferedInputStream(new FileInputStream(filepath));

		OutputStream os;
		URL urlRemotefile = new URL(url.toExternalForm()+"/"+remoteFilename);

		URLConnection connection = urlRemotefile.openConnection();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("writeFTPReport sleep interrupted !");
		}

		connection.setDoOutput(true);

		os = connection.getOutputStream();

		BufferedOutputStream out = new BufferedOutputStream(os);

		int c;
		while ((c = in.read()) > -1) {
			out.write(c);
		}

		out.flush();
		out.close();
		
		return "put "+filepath+" to " + remoteFilename;
	}

	public void setMime(String mime) {
		
	}

	public void setEncoding(String encoding) {
		
	}

}
