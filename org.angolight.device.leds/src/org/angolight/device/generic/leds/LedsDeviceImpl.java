package org.angolight.device.generic.leds;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.log4j.Logger;

public class LedsDeviceImpl implements LedsDevice {

	public static final String OK = "OK";
	public static final int EOF = -1;

	private Logger _log;
	private boolean _opened;
	protected Port _port;
	protected LedsDeviceInputStream _in;
	protected LedsDeviceOutputStream _out;
	protected LedsListener _listener;

	private volatile boolean _accept = false;

	public LedsDeviceImpl(LedsListener listener) {
		_log = Logger.getInstance(this.getClass());
		_listener = listener;
	}

	public void open(String url) throws IOException {
		_port = new Port(url);
		_port.open();
		_in = new LedsDeviceInputStream(_port.getInputStream());
		_out = new LedsDeviceOutputStream(_port.getOutputStream());
		_opened = true;
		_listener.ledsOpened();
	}

	public boolean isOpen() {
		return _opened;
	}

	public void close() {
		_opened = false;
		try {
			if (_port.isOpen()) {
				_in.close();
				_in = null;
				_out.close();
				_out = null;
				_port.close();
				_port = null;
			}
		} catch (IOException e) {
			_log.error(e);
		} finally {
			_listener.ledsClosed();
		}
	}

	public String send(String command) throws LedsException, IOException {
		flush();
		_accept = true;
		_out.writeln(command);
		String text = _in.readln();
		_accept = false;
		if (text == null) {
			throw new LedsException(LedsException.ERROR);
		} else if (text.indexOf(LedsException.BUSY) >= 0) {
			throw new LedsException(LedsException.BUSY);
		}
		return text;
	}

	public void flush() {
		try {
			int available = _in.available();
			_in.skip(available);
		} catch (IOException e) {
			_log.error(e);
		}
	}

	class LedsDeviceInputStream extends InputStream {

		private LedsInputStream _in;

		private Thread _reader;

		private boolean _running;

		public LedsDeviceInputStream(InputStream in) throws IOException {
			PipedOutputStream pos = new PipedOutputStream();
			PipedInputStream pis = new PipedInputStream(pos);
			_in = new LedsInputStream(pis);
			ReaderTask task = new ReaderTask(in, pos);
			_reader = new Thread(task);
			_reader.setName("[AVM] device leds");
			_running = true;
			_reader.start();
		}

		public String readln() throws IOException {
			if (!_reader.isAlive())
				throw new IOException("reader stoped");
			return _in.readln();
		}

		public void close() throws IOException {
			_in.close();
			_running = false;
			_reader.interrupt();
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

			private LedsInputStream _in;
			private LedsOuputStream _out;
			private Logger _log;

			public ReaderTask(InputStream in, OutputStream out) {
				_in = new LedsInputStream(in);
				_out = new LedsOuputStream(out);
				_log = Logger.getInstance(this.getClass());
			}

			public void run() {

				try {
					while (_running) {
						String text = _in.readln();
						if (text == null) {
							// timeout or error
							if (_accept) {
								_out.write(LedsInputStream.BELL);
								_out.flush();
							}
						} else if (text.indexOf(OK) >= 0) {
							_listener.sequenceStopped();
						} else {
							_out.writeln(text);
						}
					}
				} catch (Exception e) {
					_log.error(e);
					try {
						close();
					} catch (IOException t) {
					}

				}
			}
		}

	}

	class LedsDeviceOutputStream extends LedsOuputStream {
		public LedsDeviceOutputStream(OutputStream out) {
			super(out);
		}
	}

}
