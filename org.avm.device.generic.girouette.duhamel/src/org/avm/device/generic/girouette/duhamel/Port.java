package org.avm.device.generic.girouette.duhamel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class Port {

	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int READ_WRITE = 3;

	private Logger _log = Logger.getInstance(this.getClass());

	private String _url;

	private boolean _opened;

	private URLConnection _conn;

	private InputStream _in;

	private OutputStream _out;

	public Port(String url) {
		_url = url;
	}
	public void open() throws IOException {
		open(READ_WRITE);
	}

	public void open(int access) throws IOException {
		_log.debug("[DSU] port opening");
		if (!_opened) {
			try {

				URL url = new URL(_url);
				_conn = url.openConnection();
				if(access == READ_WRITE){
					_conn.connect();
				}				
				_log.debug("[DSU] open " + this.toString() + " / "
						+ _conn.toString());
			} catch (IOException e) {
				_log.error(e);
				throw e;
			} catch (Exception e) {
				_log.error(e);
				throw new IOException(e.toString());
			}
			_opened = true;

		}
		_log.debug("[DSU] port opened");
	}

	public void close() {
		_log.debug("[DSU] port closing");
		if (_opened) {
			try {
				if (_conn != null) {
					if (_in != null) {
						_log.debug("[DSU] close " + this.toString() + " / "
								+ _in.toString());
						_in.close();
						_in = null;
					}
					if (_out != null) {
						_log.debug("[DSU] close " + this.toString() + " / "
								+ _out.toString());
						_out.close();
						_out = null;
					}
					_log.debug("[DSU] close " + this.toString() + " / "
							+ _conn.toString());
					_conn = null;
				}
			} catch (IOException e) {
				_log.error(e);
			}
			_opened = false;
		}
		_log.debug("[DSU] port closed");
	}

	public boolean isOpen() {
		return _opened;
	}

	public InputStream getInputStream() throws IOException {
		if (!_opened) {
			open(READ);
		}
		if (_in == null) {
			
			_in = _conn.getInputStream();
			_log
					.debug("[DSU] open " + this.toString() + " / "
							+ _in.toString());
		}
		return _in;
	}

	public OutputStream getOutputStream() throws IOException {
		if (!_opened) {
			open(WRITE);
		}
		if (_out == null) {
			_out = _conn.getOutputStream();
			_log.debug("[DSU] open " + this.toString() + " / "
					+ _out.toString());
		}
		return _out;
	}
}
