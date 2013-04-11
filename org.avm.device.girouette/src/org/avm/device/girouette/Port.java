
package org.avm.device.girouette;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class Port {
	
	public static final int READ       = 1;
	
	public static final int WRITE      = 2;
	
	public static final int READ_WRITE = 3;
	
	private final Logger    log       = Logger.getInstance(this.getClass());
	
	private final String    url;
	
	private boolean         opened;
	
	private URLConnection   conn;
	
	private InputStream     in;
	
	private OutputStream    out;
	
	public Port(final String url) {
	
		this.url = url;
	}
	
	public void close() {
	
		this.log.debug("port closing");
		if (this.opened) {
			try {
				if (this.conn != null) {
					if (this.in != null) {
						this.log.debug("close " + this.toString() + " / " + this.in.toString());
						this.in.close();
						this.in = null;
					}
					if (this.out != null) {
						this.log.debug("close " + this.toString() + " / " + this.out.toString());
						this.out.close();
						this.out = null;
					}
					this.log.debug("close " + this.toString() + " / " + this.conn.toString());
					this.conn = null;
				}
			}
			catch (final IOException e) {
				this.log.error(e);
			}
			this.opened = false;
		}
		this.log.debug("port closed");
	}
	
	public InputStream getInputStream() throws IOException {
	
		if (!this.opened) {
			this.open(Port.READ);
		}
		if (this.in == null) {
			this.in = this.conn.getInputStream();
			this.log.debug("open " + this.toString() + " / " + this.in.toString());
		}
		return this.in;
	}
	
	public OutputStream getOutputStream() throws IOException {
	
		if (!this.opened) {
			this.open(Port.WRITE);
		}
		if (this.out == null) {
			this.out = this.conn.getOutputStream();
			this.log.debug("open " + this.toString() + " / " + this.out.toString());
		}
		return this.out;
	}
	
	public boolean isOpen() {
	
		return this.opened;
	}
	
	public void open() throws IOException {
	
		this.open(Port.READ_WRITE);
	}
	
	public void open(final int access) throws IOException {
	
		this.log.debug("port opening");
		if (!this.opened) {
			try {
				final URL url = new URL(this.url);
				this.conn = url.openConnection();
				if (access == Port.READ_WRITE) {
					this.conn.connect();
				}
				this.log.debug("open " + this.toString() + " / " + this.conn.toString());
			}
			catch (final IOException e) {
				this.log.error(e);
				throw e;
			}
			catch (final Exception e) {
				this.log.error(e);
				throw new IOException(e.toString());
			}
			this.opened = true;
		}
		this.log.debug("port opened");
	}
}
