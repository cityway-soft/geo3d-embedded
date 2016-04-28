package org.avm.device.generic.gsm;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.gsm.Constant;
import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmEvent;
import org.avm.device.gsm.GsmException;
import org.avm.device.gsm.GsmRequest;
import org.avm.device.gsm.GsmResponse;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

import EDU.oswego.cs.dl.util.concurrent.BoundedPriorityQueue;
import EDU.oswego.cs.dl.util.concurrent.Takable;

public class GsmImpl implements Gsm, ModemListener, ConfigurableService,
		ProducerService, ManageableService, Constant {

	public static boolean USE_WIND_MODE = false;

	protected Modem _modem;

	protected BoundedPriorityQueue _queue;

	protected Thread _consumer;

	private ProducerManager _producer;

	private GsmConfig _config;

	private volatile boolean _started;

	private Logger _log = Logger.getInstance(this.getClass());

	static boolean RTC_MODEM = false;

	public GsmImpl() {

	}

	public void modemHangup() {
		_producer.publish(new GsmEvent(this, GsmEvent.HANGUP));
	}

	public void modemRinging(String origin) {
		_producer.publish(new GsmEvent(this, GsmEvent.RING, origin));
	}

	public void modemSmsIncomming(String bank) {
		_producer.publish(new GsmEvent(this, GsmEvent.INCOMMING_SMS, bank));
	}

	public void configure(Config config) {
		_config = (GsmConfig) config;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void start() {
		_queue = new BoundedPriorityQueue(10);
		_consumer = new Thread(new Consumer(_queue));
		_consumer.setName("[AVM] device gsm");
		_started = true;
		_consumer.start();

		sleep(1000);
		GsmRequest command = new GsmRequest(AT_CHECK_PIN, Constant.CPIN_READY,
				Constant.ERROR, 0);
		try {
			send(command, true);
		} catch (GsmException e) {
			if (command.result.status == GsmResponse.KO) {
				RTC_MODEM = true;
			}
		} catch (IOException e) {
			_log.error(e);
		}

	}

	public void stop() {
		_started = false;
		sleep(2000);
	}

	public void send(GsmRequest command) throws GsmException, IOException {
		send(command, true);
	}

	public void send(GsmRequest command, boolean blocking) throws GsmException,
			IOException {

		try {
			synchronized (command) {
				_queue.put(command);
				if (blocking) {
					command.wait();
				}
			}
		} catch (InterruptedException e) {
			_log.error("Send InterruptedException  : " + e.getMessage());
		}

		if (command.result != null && command.result.status == GsmResponse.KO) {
			throw new GsmException(command.result.value);
		}
	}

	private void initialize() {
		try {
			_modem = new ModemImpl(this);
			_modem.open(_config.getUrlConnection());

			GsmRequest command = new GsmRequest("ATH\r");
			send(command, false);
			command = new GsmRequest(AT_AT);
			command.add(new GsmRequest(AT_ECHO_OFF));
			if (USE_WIND_MODE) {
				command.add(new GsmRequest(AT_WIND));
			}
			send(command, false);
		} catch (Exception e) {
			_log.error("Initialize error", e);
			sleep(5000);
		}
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void dispose() {
		GsmRequest command = null;
		try {
			while ((command = (GsmRequest) _queue.poll(0)) != null) {
				GsmRequest[] requests = command.getRequests();
				GsmResponse ko = new GsmResponse(GsmResponse.KO, "");
				for (int i = 0; i < requests.length; i++) {
					requests[i].result = ko;
				}
				command.result = ko;
				synchronized (command) {
					command.notify();
				}
			}
		} catch (InterruptedException e) {
		} finally {
			if (_modem != null && _modem.isOpen())
				_modem.close();
		}
	}

	class Consumer implements Runnable {
		final Takable _channel;

		Consumer(Takable channel) {
			_channel = channel;
		}

		public void run() {
			GsmRequest command = null;
			while (_started) {
				if (_modem == null || !_modem.isOpen()) {
					initialize();
				}
				try {
					command = (GsmRequest) _channel.poll(1000);
					if (command != null && !command.isCanceled()) {
						execute(command);
						synchronized (command) {
							command.notify();
						}
					} else {
						_modem.flush();
					}
				} catch (Exception e) {
					_log.error(e);
					if (command != null) {
						synchronized (command) {
							command.notify();
						}
					}
					dispose();
				}
			}
			dispose();
		}

		void execute(GsmRequest request) throws IOException {
			String result = null;
			GsmRequest[] requests = request.getRequests();
			for (int i = 0; i < requests.length; i++) {
				try {
					result = _modem.at(requests[i]);
				} catch (GsmException e) {
					GsmResponse ko = new GsmResponse(GsmResponse.KO,
							e.getMessage());
					requests[i].result = ko;
					request.result = ko;
					break;
				} catch (IOException e) {
					GsmResponse ko = new GsmResponse(GsmResponse.KO,
							e.getMessage());
					requests[i].result = ko;
					request.result = ko;
					throw e;
				}

				GsmResponse ok = new GsmResponse(GsmResponse.OK, result);
				requests[i].result = ok;
			}
		}
	}

	public void modemClosed() {
		_log.info("Modem closed");
		if (_producer != null) {
			_producer.publish(new GsmEvent(this, GsmEvent.MODEM_CLOSED));
		}
	}

	public void modemOpened() {
		_log.info("Modem opened");
		_producer.publish(new GsmEvent(this, GsmEvent.MODEM_OPENED));
	}

	public int getSignalQuality() {
		int quality = 0;
		try {

			GsmRequest command = new GsmRequest(AT_SIGNAL_QUALITY);

			send(command);

			if (command.result != null
					&& command.result.status == GsmResponse.OK) {
				String result = command.result.value;

				String rssi = result.substring(result.indexOf(':') + 1,
						result.indexOf(',')).trim();

				int value = Integer.parseInt(rssi);
				if (value < 5) {
					quality = 1;
				} else if (value < 12) {
					quality = 2;
				} else if (value < 20) {
					quality = 3;
				} else if (value < 26) {
					quality = 4;
				} else if (value <= 31) {
					quality = 5;
				} else if (value == 99) {
					quality = 0;
				}
			} else {
				quality = 0;
			}
		} catch (Exception e) {
			_log.error(e);
		}
		return quality;
	}

	public boolean isGprsAttached() {
		boolean attached = false;
		GsmRequest command = new GsmRequest(AT_GPRS_ATTACHED);
		try {
			send(command);
			if (command.result != null
					&& command.result.status == GsmResponse.OK) {
				String result = command.result.value;
				String text = result.substring(result.indexOf(',') + 1).trim();

				int value = Integer.parseInt(text);
				if (value == 1) {
					attached = true;
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}
		return attached;
	}

	public boolean isGsmAttached() {
		boolean attached = false;
		try {
			GsmRequest command = new GsmRequest(AT_GSM_ATTACHED);
			send(command);
			if (command.result != null
					&& command.result.status == GsmResponse.OK) {
				String result = command.result.value;
				String text = result.substring(result.indexOf(',') + 1).trim();
				// _log.debug(AT_GSM_ATTACHED+"=>"+text);
				int value = Integer.parseInt(text);
				if (value == 1) {
					attached = true;
				}
			}
		} catch (Exception e) {
			_log.error(e);
		}
		return attached;
	}
}
