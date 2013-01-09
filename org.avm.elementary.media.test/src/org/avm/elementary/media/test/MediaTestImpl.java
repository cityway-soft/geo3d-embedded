package org.avm.elementary.media.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.MediaListener;

public class MediaTestImpl implements MediaTest, ConfigurableService,
		ManageableService {

	private Logger _log;

	private MediaTestConfig _config;

	private MediaListener _messenger;
	
	private Thread _thread= null;

	public MediaTestImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
	}

	public void configure(Config config) {
		_config = (MediaTestConfig) config;
	}

	public void start() {
		_log.info("Start Media Test");
		if (_thread != null){
			_thread.interrupt();
			_thread=null;
		}
		
		_thread = new Thread(new MessengerServerSocket());
		_thread.start();
	}

	public void stop() {
		_log.info("Stop Media Test");
		if (_thread != null){
			_thread.interrupt();
		}
	}


	public void send(Dictionary header, byte[] data) throws Exception {
		throw new Exception("Method send(...) for media 'Test' not yet implemented !");
	}

	public static String toHexaString(byte[] data) {
		byte[] buffer = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			int rValue = data[i] & 0x0000000F;
			int lValue = (data[i] >> 4) & 0x0000000F;
			buffer[i * 2] = (byte) ((lValue > 9) ? lValue + 0x37
					: lValue + 0x30);
			buffer[i * 2 + 1] = (byte) ((rValue > 9) ? rValue + 0x37
					: rValue + 0x30);
		}
		return new String(buffer);
	}

	public void setMessenger(MediaListener messenger) {
		_log.debug("Initialisation du messenger " + messenger);
		_messenger = messenger;
	}

	private String toHex(int doublemot) {
		String b = Integer.toHexString(doublemot).toUpperCase();
		if (b.length() == 1) {
			b = "0" + b;
		}
		return b;
	}

	private byte[] fromHexaString(String hexaString) {
		byte[] buffer = hexaString.getBytes();
		byte[] data = new byte[buffer.length / 2];
		for (int i = 0; i < data.length; i++) {
			int index = i * 2;
			int rValue = (buffer[i * 2] > 0x39) ? buffer[index] - 0x37
					: buffer[index] - 0x30;
			int lValue = (buffer[i * 2 + 1] > 0x39) ? buffer[index + 1] - 0x37
					: buffer[index + 1] - 0x30;
			data[i] = (byte) (((rValue << 4) & 0xF0) | (lValue & 0x0F));
		}
		return data;
	}

	public int getPriority() {
		return 1;
	}

	class MessengerServerSocket implements Runnable {
		int i = 0;
		int maxConnections = 0;

		public void run() {
			try {
				ServerSocket listener = new ServerSocket(8888);
				Socket server;
				_log.debug("Socket server launched.");

				while ((i++ < maxConnections) || (maxConnections == 0) && (_thread != null && !_thread.isInterrupted()) ) {
					ClientSocket connection;

					server = listener.accept();
					ClientSocket conn_c = new ClientSocket(server);
					Thread t = new Thread(conn_c);
					t.start();
				}
			} catch (IOException ioe) {
				System.out.println("IOException on socket listen: " + ioe);
				ioe.printStackTrace();
			}
		}
	}

	class ClientSocket implements Runnable {
		private Socket server;
		private String line, input;

		ClientSocket(Socket server) {
			this.server = server;
		}

		public void run() {
			input = "";

			try {
				// Get input from the client
				BufferedInputStream in = new BufferedInputStream(server
						.getInputStream());
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				_log.info("getSendBufferSize=" + server.getSendBufferSize());
				int i=0;
				int c = -1;
				_log.info("available : " + in.available());
				do {
					c = in.read();
					o.write(c);	
					i++;
				}while (in.available() > 0);
				
				o.flush();
				Properties p = new Properties();
				p.put("origin", "media-test");
				_log.info("RECEIVE ("+i+") octets: " + toHexaString(o.toByteArray()));
				_messenger.receive(p, o.toByteArray());

				server.close();
			} catch (IOException ioe) {
				_log.error("IOException on socket listen: " + ioe);
				ioe.printStackTrace();
			}
		}
	}

	public String getMediaId() {
		return "media-test";
	}

}