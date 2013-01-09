package org.avm.device.generic.can;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.avm.device.can.Can;
import org.avm.device.generic.can.filter.Filter;
import org.avm.device.generic.can.filter.FilterFactory;
import org.avm.elementary.can.parser.CANParser;
import org.avm.elementary.can.parser.CANParserInjector;
import org.avm.elementary.can.parser.PGN;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

public class CanImpl implements Can, CANParserInjector, ConfigurableService,
		ManageableService, ProducerService, Runnable {

	private Logger _log = Logger.getInstance(this.getClass());

	private CanConfig _config;

	private ProducerManager _producer;

	private Thread _thread;

	private boolean _running = true;

	private URLConnection _conn;

	private CANParser _parser;

	private Filter _filter;

	public void send(PGN pgn) throws Exception {

		// parsing
		if (_parser == null) {
			return;
		}
		byte[] buffer = new byte[14];
		_parser.put(pgn, buffer);

		// send frame
		OutputStream out = _conn.getOutputStream();
		out.write(buffer);

	}

	public void configure(Config config) {
		_config = (CanConfig) config;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void start() {

		// initialisation connector
		try {
			URL url = new URL(_config.getUrlConnection());
			_conn = url.openConnection();
		} catch (IOException e) {
			_log.error(e);
			return;
		}

		// demarage acquisition
		if (_config.getMode().equals("r") || _config.getMode().equals("rw")) {
			_running = true;
			_thread = new Thread(this);
			_thread.setName("[AVM] device can");
			_thread.setPriority(Thread.MAX_PRIORITY);
			_thread.start();
		}
	}

	public void stop() {
		_running = false;
		if (_thread != null)
			_thread.interrupt();
		closeConnection();
	}
	
	private void closeConnection() {
		try {
			if(_conn != null){
			_conn.getInputStream().close();
			_conn.getOutputStream().close();
			}
		} catch (IOException e) {
			
		}
	}

	public void run() {
		long sleep = _config.getSleepTime();
		long now = System.currentTimeMillis();
		while (_running) {
			try {
				// acquisition
				byte[] buffer = (byte[]) _conn.getContent();
				if (buffer == null) {
					Thread.sleep(sleep);
					continue;
				}

				// filtrage
				if (_filter != null) {
					if (!_filter.execute(buffer))
						continue;
				}

				// parsing
				if (_parser != null) {
					PGN pgn = null;
					try {
						pgn = _parser.get(buffer);
					} catch (ClassNotFoundException e) {
						if (_log.isDebugEnabled()) {

							final String PATTERN = "({0,number,.000}) can0 {1}#{2}";
							byte[] b = new byte[4];
							for (int i = 0; i < b.length; i++) {
								b[3 - i] = buffer[i];
							}
							Object[] objects = {
									new Double(
											(System.currentTimeMillis() - now) / 1000d),
									PGN.toHexaString(b),
									PGN.toHexaString(buffer, 6, 8) };
							System.out.println(MessageFormat.format(PATTERN,
									objects));
						}
					}

					// notification
					if (pgn != null) {
						if (_log.isDebugEnabled()) {
							_log.debug("[DSU] notification " + pgn.toString());
						}
						_producer.publish(pgn);
					}
				}
			} catch (Exception e) {
				_log.error(e);
			}
		}
		closeConnection();
	}

	public void setCANParser(CANParser parser) {
		_parser = parser;

		// initialisation filtre
		try {
			String name = _config.getFilter();
			if (name != null) {
				_filter = FilterFactory.create(name, _parser);
			}
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public CANParser getCanParser() {
		return _parser;
	}

	public void unsetCANParser(CANParser parser) {
		_parser = null;
		_filter = null;
	}
}
