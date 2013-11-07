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

/**
 * @author didier.lallemand@cityway.fr
 * 
 */
class SimpleLocalClient implements IRemoteClient {
	URL url;

	public SimpleLocalClient(URL url) {
		this.url = url;
	}

	public String put(StringBuffer buffer, String remoteFilename)
			throws IOException {
		OutputStream os = null;

		URL urlRemotefile = new URL(url.toExternalForm() + "/" + remoteFilename);
		String filename = urlRemotefile.getFile();
		File file = new File(filename);
		file = file.getParentFile();
		if (!file.exists()) {
			file.mkdirs();
		}
		os = new FileOutputStream(filename, false);

		OutputStreamWriter writer = new OutputStreamWriter(os);

		writer.write(buffer.toString());
		writer.flush();
		writer.close();

		return "put buffer to " + remoteFilename;

	}

	public String put(File file, String remoteFilename) throws IOException {
		String filepath = file.getAbsolutePath();
		InputStream in = new BufferedInputStream(new FileInputStream(filepath));

		OutputStream os;

		URL urlRemotefile = new URL(url.toExternalForm() + "/" + remoteFilename);
		String filename = urlRemotefile.getFile();

		File localfile = new File(filename);
		file = localfile.getParentFile();
		if (!localfile.exists()) {
			localfile.mkdirs();
		}
		os = new FileOutputStream(filename, false);

		BufferedOutputStream out = new BufferedOutputStream(os);

		int c;
		while ((c = in.read()) > -1) {
			out.write(c);
		}

		out.flush();
		out.close();

		return "put " + filepath + " to " + remoteFilename;
	}

	public void setMime(String mime) {

	}

	public void setEncoding(String encoding) {

	}

	public InputStream get() throws IOException {
		URL urlRemotefile = new URL(url.toExternalForm());
		String filename = urlRemotefile.getFile();

		return new FileInputStream(filename);

	}

}
