package org.avm.elementary.management.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DataUploadClient implements IRemoteClient {

	private IRemoteClient client;

	public DataUploadClient(URL url) {
		if (url.getProtocol().equals("ftp")) {
			client = new SimpleFTPClient(url);
		} else if (url.getProtocol().equals("http")) {
			client = new SimpleHTTPClient(url);
		}

	}

	public String put(StringBuffer buffer, String remoteFilename) throws IOException {
		String result=null;
		if (client != null) {
			result = client.put(buffer, remoteFilename);
		}
		return result;
	}

	public String put(File file, String remoteFilename) throws IOException {
		String result=null;
		if (client != null) {
			result = client.put(file, remoteFilename);
		}
		return result;
	}

	public void setMime(String mime) {
		if (client != null) {
			client.setMime(mime);
		}
	}

	public void setEncoding(String encoding) {
		if (client != null) {
			client.setEncoding(encoding);
		}
	}

	public static void main(String[] args) {
		try {
			DataUploadClient client = new DataUploadClient(
					new URL(
							"http://avm:avm++@localhost:8080/frontal.terminal-manager/rest/terminal/01002477032C5390/upload"));
//			client.put(new File(
//					"/home/didier/Projets/REPOGIT/common-elementary.git/org.avm.elementary.management.core/src/org/avm/elementary/management/core/BundleList.java"), "BundleList.java");
//			client.put(new File(
//					"/tmp/38_1907_01002477032C5390_update.log.gz"), "1_1109_updateauto.log");
//			client.put(new File(
//					"/tmp/38_1907_01002477032C5390_update.log.gz"), "1_1109_updateauto.log");			
//			client.put(new File(
//					"/home/avm/exploitants/1/upload/1_5209_M2011-02-08-210232R2011-11-29_recette.txt"), "1_5209_M2011-02-08-210232R2011-11-29_recette.txt");
			client.put(new File(
					"/home/avm/exploitants/1/upload/1_1109_M2011-11-29-231141R2011-11-30_jdb.gz.mar"), "1_1109_M2011-11-29-231141R2011-11-30_jdb.gz.mar");
			
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
