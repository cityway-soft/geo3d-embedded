package org.avm.device.generic.gsm;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.avm.device.gsm.Constant;
import org.avm.device.gsm.GsmException;
import org.avm.device.gsm.GsmRequest;

public class ModemImpl implements Modem, Constant {

	public static final int CANCELED = -2;

	public static final int TIMEOUT = -1;

	public static final int GOOD_MATCH = 0;

	public static final int BAD_MATCH = 1;

	private Logger _log = Logger.getInstance(this.getClass());

	private StringBuffer _buffer = new StringBuffer();

	private boolean _opened;

	protected Port _port;

	protected ModemInputStream _in;

	protected ModemOutputStream _out;

	protected ModemListener _listener;

	public ModemImpl(ModemListener listener) {
		_listener = listener;
	}

	public boolean isOpen() {
		return _opened;
	}

	public void open(String url) throws IOException {
		_port = new Port(url);
		_port.open();
		_in = new ModemInputStream(_port.getInputStream(), _listener);
		_out = new ModemOutputStream(_port.getOutputStream());
		_opened = true;
		_listener.modemOpened();
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
			_listener.modemClosed();
		}
	}

	public String at(GsmRequest request) throws GsmException, IOException {

		String result = null;

		if (_log.isDebugEnabled())
			_log.debug("[DSU] write : " + new String(request.command));
		flush();
		_out.write(request.command);

		long to = System.currentTimeMillis();
		int key = match(request);
		long now = System.currentTimeMillis();
		switch (key) {
		case GOOD_MATCH:
			result = getMatch();
			if (_log.isDebugEnabled())
				_log.debug("GOOD_MATCH (" + (now - to) + "/" + request.timeout
						+ ") : " + new String(request.command) + " {" + result
						+ "}");
			break;
		case BAD_MATCH:
			result = getMatch();
			if (_log.isDebugEnabled())
				_log.debug("BAD_MATCH : (" + (now - to) + "/" + request.timeout
						+ ") : " + new String(request.command) + " {" + result
						+ "}");
			throw new GsmException(result);
		case CANCELED:
			result = "CANCELED";
			if (_log.isDebugEnabled())
				_log.debug("CANCELED : (" + (now - to) + "/" + request.timeout
						+ ") : " + new String(request.command) + " {" + result
						+ "}");
			throw new GsmException(result);
		case TIMEOUT:
			result = "TIMEOUT";
			if (_log.isDebugEnabled())
				_log.debug("TIMEOUT : : (" + (now - to) + "/" + request.timeout
						+ ") : " + new String(request.command) + " {" + result
						+ "}");
			throw new IOException(result);
		}

		return result;
	}

	public void flush() {
		try {
			int available = _in.available();
			_in.skip(available);
		} catch (IOException e) {
			_log.error(e);
		}
	}

	private int match(GsmRequest command) throws IOException {
		int result = TIMEOUT;
		long startTime = System.currentTimeMillis();
		boolean matched = false;
		_buffer.setLength(0);

		while (matched == false) {
			String line = _in.readln();

			if (line != null) {

				if (_log.isDebugEnabled())
					_log.debug("readln : " + line);

				if (line.indexOf(command.goodMatch) > -1) {
					result = GOOD_MATCH;
					matched = true;
					break;
				}

				for (int i = 0; i < command.badMatchs.length
						&& matched == false; i++) {
					String badMatch = command.badMatchs[i];
					if (line.indexOf(badMatch) > -1) {
						result = BAD_MATCH;
						matched = true;
						break;
					}
				}
				_buffer.append(line + "\n");
			}

			// canceled
			if (matched == false && command.isCanceled()) {
				_buffer.setLength(0);
				result = CANCELED;
				break;
			}

			// time out
			if (matched == false
					&& (System.currentTimeMillis() - startTime > command.timeout)) {
				_buffer.setLength(0);
				result = TIMEOUT;
				break;
			}
		}

		return result;
	}

	private String getMatch() {
		return _buffer.toString();
	}

}
