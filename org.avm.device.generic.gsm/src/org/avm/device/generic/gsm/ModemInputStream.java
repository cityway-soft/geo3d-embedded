package org.avm.device.generic.gsm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.log4j.Logger;
import org.avm.device.gsm.Constant;

class ModemInputStream extends InputStream implements Constant {

	private GsmInputStream _in;

	private InputStream _peer;

	private Thread _reader;

	private boolean _running;

	private ModemListener _listener;

	private Logger _log = Logger.getInstance(this.getClass());

	public ModemInputStream(InputStream in, ModemListener listener)
			throws IOException {
		_peer = in;
		_listener = listener;
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream(pos);
		_in = new GsmInputStream(pis, 10);
		ReaderTask task = new ReaderTask(_peer, pos);
		_reader = new Thread(task);
		_reader.setName("[AVM] modem gsm ");
		_running = true;
		_reader.start();
	}

	public String readln() throws IOException {
		if (!_reader.isAlive())
			throw new IOException("reader stoped");
		return _in.readln();
	}

	public void close() throws IOException {
		_running = false;
		_peer.close();
	}

	public void mark(int readlimit) {
		_in.mark(readlimit);
	}

	public boolean markSupported() {
		return _in.markSupported();
	}

	public int read() throws IOException {
		return _in.read();
	}

	public int read(byte[] b, int offset, int length) throws IOException {
		return _in.read(b, offset, length);
	}

	public int read(byte[] b) throws IOException {
		return _in.read(b, 0, b.length);
	}

	public synchronized void reset() throws IOException {
		_in.reset();
	}

	public long skip(long n) throws IOException {
		return _in.skip(n);
	}

	public int available() throws IOException {
		return _in.available();
	}

	class ReaderTask implements Runnable {

		private GsmInputStream in;

		private GsmOuputStream out;

		public ReaderTask(InputStream in, OutputStream out) {
			this.in = new GsmInputStream(in, 80);
			this.out = new GsmOuputStream(out);
		}

		public void run() {
			try {
				while (_running) {

					String text;

					text = in.readln();

					if (text == null) {
						out.writeln("");
					} else if (text.length() == 0) {
						// _log.debug("[DSU] empty line");
					} else if (GsmImpl.USE_WIND_MODE
							&& (text.indexOf(WIND_NO_CARRIER) >= 0)) {
						_log.debug("notify Hangup : " + text);
						_listener.modemHangup();
					}else if (text.indexOf(NO_CARRIER) >= 0) {
						_log.debug("notify Hangup : " + text);
						_listener.modemHangup();
					}
					else if (text.indexOf(RING) >= 0) {
						_log.debug("reception : " + text);
						// if(GsmImpl.RTC_MODEM){
						_log.debug("notify RING : " + text);
						_listener.modemRinging("");
						// }
					} else if (text.indexOf(CALLING_LINE_PRESENTATION) >= 0) {
						_log.debug("notify RING : " + text);
						_listener.modemRinging(text);
					} else if (text.indexOf(SMS_INCOMMING) >= 0) {
						_log.debug("notify SMS INCOMMING : " + text);
						_listener.modemSmsIncomming(text);
					} else {
						out.writeln(text);
					}
				}
			} catch (Exception e) {
				_log.error(e);
				try {
					_in.close();
					_in = null;
					in.close();
				} catch (IOException t) {
				}
			}
		}
	}
}
