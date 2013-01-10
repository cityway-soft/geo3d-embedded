package org.avm.elementary.management.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author MERCUR
 * 
 */
public class SimpleFTPClient  {

	
	public SimpleFTPClient() {
	}
	
	public void get(URL remote, URL local){
		
	}
	

	public void put(URL url, StringBuffer buffer) {
		try {
			OutputStream os = null;
			String protocol = url.getProtocol();
			if (protocol.toLowerCase().equals("ftp")){
				URLConnection connection;
				connection = url.openConnection();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.out.println("writeFTPReport sleep interrupted !");
				}
				connection.setDoOutput(true);
				os = connection.getOutputStream();
			}else {
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
			System.out.println("Simple FTP Exception (URL="+url+"):"+ e.getMessage());
		}
	}




}
