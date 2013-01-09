package org.avm.device.generic.comptage.elinap;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.ContentHandlerFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class Port extends Thread{

	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int READ_WRITE = 3;
	public List _listeners = new ArrayList();

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
	
	public void addListener (PortListener listener){
		_listeners.add(listener);
	}
	
	public void removeListener (PortListener listener){
		_listeners.remove(listener);
	}

	
	public void open(int access) throws IOException {
		if (!_opened) {
			try {
				URL url = new URL(_url);
				_conn = url.openConnection();
				if(access == READ_WRITE){
					_conn.connect();
				}				
			} catch (IOException e) {
				_log.error(e);
				throw e;
			} catch (Exception e) {
				_log.error(e);
				throw new IOException(e.toString());
			}
			_opened = true;

		}
	}

	public void close() {
		if (_opened) {
			try {
				if (_conn != null) {
					if (_in != null) {
						_in.close();
						_in = null;
					}
					if (_out != null) {
						_out.close();
						_out = null;
					}
					_conn = null;
				}
			} catch (IOException e) {
				_log.error(e);
			}
			_opened = false;
		}
		}

	public boolean isOpen() {
		return _opened;
	}

	public InputStream getInputStream() throws IOException {
		if (!_opened) {
			open(READ);
		}
		if (_in == null) {
			
			try {
				InputStream in = _conn.getInputStream();
				_in = new DataInputStream(new PushbackInputStream(in));
			} catch (Throwable e) {
				throw new IOException(e.getMessage());
			}
		}
		return _in;
	}

	public OutputStream getOutputStream() throws IOException {
		if (!_opened) {
			open(WRITE);
		}
		if (_out == null) {
			_out = _conn.getOutputStream();
		}
		return _out;
	}
	
	public void run (){
		try {
			
			DataInputStream in = (DataInputStream)getInputStream();
			
			while (_opened){
				int nb = in.available();
				//System.out.println("ap available");
				if (nb > 0){
					int val = in.read();
					if (val != -1){
					notifyListeners (val);
					}
				}
				else{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
		} catch (IOException e) {
			_log.error(e);
		}
		
	}
	private void notifyListeners(int val) {
		Iterator it = _listeners.iterator();
		while (it.hasNext()){
			PortListener listener = (PortListener)it.next();
			listener.onChar((char)val);
		}
	}
}
