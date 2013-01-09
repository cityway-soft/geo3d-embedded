package org.avm.device.protocol.rs485;

import gnu.io.CommPortIdentifier;
import gnu.io.RXTXCommDriver;
import gnu.io.SerialPort;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

public class Connection extends org.avm.device.protocol.comm.Connection {

	static {
		try {
			registerSpecifiedPorts();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection(URL url) {
		super(url);

	}

	private static boolean registerSpecifiedPorts() throws Exception {

		String ext_dir = System.getProperty("java.ext.dirs")
				+ System.getProperty("file.separator");
		FileInputStream rxtx_prop = new FileInputStream(ext_dir
				+ "gnu.io.rxtx.properties");
		Properties p = new Properties();
		p.load(rxtx_prop);
		System.setProperties(p);
		for (Iterator it = p.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			System.setProperty(key, p.getProperty(key));
		}
		String val = null;
		if ((val = System.getProperty("gnu.io.rxtx.RS485Ports")) == null)
			val = System.getProperty("gnu.io.RS485Ports");

		if (val != null) {
			final String pathSep = System.getProperty("path.separator", ":");
			final StringTokenizer tok = new StringTokenizer(val, pathSep);

			while (tok.hasMoreElements()) {
				String PortName = tok.nextToken();
				CommPortIdentifier.addPortName(PortName,
						CommPortIdentifier.PORT_RS485, new RXTXCommDriver());
			}
			return true;
		} else
			return false;
	}

	protected int openImpl(int portNum, int baudrate, int bitsPerChar,
			int stopBits, int parity, boolean autoRTS, boolean autoCTS,
			int timeout) throws IOException {

		try {

			CommPortIdentifier identifier = CommPortIdentifier
					.getPortIdentifier("/dev/ttyS" + portNum);
			if (identifier.getPortType() != CommPortIdentifier.PORT_RS485) {
				throw new IllegalArgumentException();
			}
			_port = identifier.open(this.getClass().getName(), 2000);
			((SerialPort) _port).setSerialPortParams(baudrate, bitsPerChar,
					stopBits, parity);
			_port.enableReceiveTimeout(timeout);
			return 0;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
}
