package org.avm.elementary.management.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.params.HttpMethodParams;

class SimpleHTTPClient implements IRemoteClient {

	private static final String REMOTE_FILE_PARAM = "uploadedFile";

	private URL url;
	private HttpClient client;
	private String mime = "text/plain";
	private String encoding = "ISO-8859-1";

	public void setMime(String mime) {
		this.mime = mime;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public SimpleHTTPClient(URL url) {
		client = new HttpClient();
		
		HttpMethodRetryHandler handler = new HttpMethodRetryHandler() {
			
			public boolean retryMethod(HttpMethod arg0, IOException arg1, int arg2) {
					return false;
			}
		};
		System.err.println("HTTPPPPPPPPPP retry set to 0");
		client.getParams()
				.setParameter(HttpMethodParams.RETRY_HANDLER, handler);
		
		
		this.url = url;
		String userinfo = url.getUserInfo();
		String host = url.getHost();
		int port = url.getPort();
		if (userinfo != null) {
			int idx = userinfo.indexOf(":");
			String password = userinfo.substring(idx + 1);
			String user = userinfo.substring(0, idx);
			Credentials defaultcreds = new UsernamePasswordCredentials(user,
					password);

			client.getState().setCredentials(
					new AuthScope(host, port, AuthScope.ANY_REALM),
					defaultcreds);
		}
	}

	public String put(StringBuffer buffer, String remoteFilename)
			throws IOException {
		OutputStream os;

		File file = File.createTempFile("tmp", remoteFilename);

		os = new FileOutputStream(file.getAbsolutePath(), false);

		OutputStreamWriter writer = new OutputStreamWriter(os);

		writer.write(buffer.toString());
		writer.flush();
		writer.close();

		String result = put(file, remoteFilename);

		file.delete();

		return result;

	}

	public String put(File file, String remoteFilename) throws IOException {
		MultipartPostMethod method = new MultipartPostMethod(
				url.toExternalForm());
		method.addParameter(remoteFilename, file);
		method.addPart(new FilePart(REMOTE_FILE_PARAM, file, mime, encoding));
		// Execute and print response
		client.executeMethod(method);
		String response = method.getResponseBodyAsString();
		System.out.println(response);
		method.releaseConnection();

		return response;
	}

	public InputStream get() throws IOException {
		HttpMethod method = new GetMethod(url.toExternalForm());
		
		
		
		client.executeMethod(method);
		return method.getResponseBodyAsStream();
	}
}
