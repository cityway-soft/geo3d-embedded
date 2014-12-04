package org.avm.device.generic.phony.impl;

import org.apache.log4j.Logger;
import org.avm.device.generic.phony.bundle.ConfigImpl;
import org.avm.device.gsm.Gsm;
import org.avm.device.gsm.GsmEvent;
import org.avm.device.gsm.GsmInjector;
import org.avm.device.gsm.GsmRequest;
import org.avm.device.phony.PhoneEvent;
import org.avm.device.phony.PhoneRingEvent;
import org.avm.device.phony.Phony;
import org.avm.device.sound.Sound;
import org.avm.device.sound.SoundInjector;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.PooledQueuedExecutor;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.variable.Variable;
import org.avm.elementary.variable.VariableInjector;
import org.osgi.util.measurement.Measurement;

public class PhonyImpl implements Phony, ConfigurableService,
		ManageableService, ProducerService, ConsumerService, GsmInjector,
		SoundInjector, VariableInjector, JDBInjector, Constant {

	private Logger _log;
	private Scheduler _scheduler = null;
	private PhonyServiceStateMachineContext _fsm;

	private ConfigImpl _config;
	private ProducerManager _producer;

	private Gsm _gsm;

	private JDB _jdb;
	private Sound _sound;
	private Variable _cioaudio;

	private DialTask _dialingTask;
	private Object _ringingTask;
	private int _volume;
	private boolean _started;

	private PooledQueuedExecutor _executor;

	public PhonyImpl() {
		_log = Logger.getInstance(this.getClass());
		_scheduler = new Scheduler();
		_executor = new PooledQueuedExecutor();
		_fsm = new PhonyServiceStateMachineContext(
				new PhonyStateMachineCallback());
		_fsm.setDebugFlag(true);
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
		if (_config != null) {
			_volume = _config.getDefaultSoundVolume();
		}
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void setVariable(Variable var) {
		_cioaudio = var;
	}

	public void unsetVariable(Variable var) {
		_cioaudio = null;
	}

	public void setGsm(Gsm gsm) {
		_gsm = gsm;
		_open();
	}

	public void unsetGsm(Gsm gsm) {
		_gsm = null;
		_close();
	}

	public void setSound(Sound sound) {
		_sound = sound;
	}

	public void unsetSound(Sound sound) {
		_sound = null;
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	public void start() {
		_started = true;
		_open();
	}

	public void stop() {
		_started = false;
		_close();
	}

	public void hangup() throws Exception {
		_hangup();
	}

	public void dial(String phone) throws Exception {
		_dial(phone);
	}

	public void dialListenMode(String phone) throws Exception {
		_dial(phone, true);
	}

	public void answer() throws Exception {
		_answer();
	}

	public void setVolume(int volume) {
		_volume = volume;
		setSpeakerVolume(volume);
	}

	public int getDefaultSoundVolume() {
		return _config.getDefaultSoundVolume();
	}

	public String at(String cmd) {
		String result = "ok";
		GsmRequest request = new GsmRequest(cmd);
		try {
			_gsm.send(request);
		} catch (Exception e) {
			result = e.getMessage();
		}
		return result;
	}



	public void notify(Object obj) {
		_log.debug(obj);
		GsmEvent event = (GsmEvent) obj;
		switch (event.type) {
		case GsmEvent.RING: {
			String phone = getCallingNumber(event.value);
			_ring(phone);
		}
			break;
		case GsmEvent.HANGUP: {
			_hangup();
		}
			break;
		case GsmEvent.MODEM_CLOSED: {
			_close();

		}
			break;
		case GsmEvent.MODEM_OPENED: {
			_open();
		}
			break;
		}
	}
	
	private void _dial(final String phone) {
		_dial(phone, false);
	}

	private void _dial(final String phone, final boolean listen) {
		try {
			_executor.execute(new Runnable() {
				public void run() {
					try {
						_log.debug("[DSU] call transition dial " + phone);
						_fsm.dial(phone, listen);
					} catch (RuntimeException e) {
						_log.error(e.getMessage(), e);
					}
				}
			});
		} catch (InterruptedException e) {
			_log.error(e.getMessage(), e);
		}
	}

	private void _answer() {
		try {
			_executor.execute(new Runnable() {
				public void run() {
					try {
						_log.debug("[DSU] call transition answer");
						_fsm.answer();
					} catch (RuntimeException e) {
						_log.error(e.getMessage(), e);
					}
				}
			});
		} catch (InterruptedException e) {
			_log.error(e.getMessage(), e);
		}
	}

	private void _ring(final String phone) {
		try {
			_executor.execute(new Runnable() {
				public void run() {
					try {
						_log.debug("[DSU] call transition ring " + phone);
						_fsm.ring(phone);
					} catch (RuntimeException e) {
						_log.error(e.getMessage(), e);
					}
				}
			});
		} catch (InterruptedException e) {
			_log.error(e.getMessage(), e);
		}
	}

	private void _hangup() {
		try {
			_executor.execute(new Runnable() {
				public void run() {
					try {
						_log.debug("[DSU] call transition hangup ");
						_fsm.hangup();
					} catch (RuntimeException e) {
						_log.error(e.getMessage(), e);
					}
				}
			});
		} catch (InterruptedException e) {
			_log.error(e.getMessage(), e);
		}
	}

	private void _close() {
		try {
			_executor.execute(new Runnable() {
				public void run() {
					try {
						_log.debug("[DSU] call transition close");
						_fsm.close();
					} catch (RuntimeException e) {
						_log.error(e.getMessage(), e);
					}
				}
			});
		} catch (InterruptedException e) {
			_log.error(e.getMessage(), e);
		}
	}

	private void _open() {
		try {
			_executor.execute(new Runnable() {
				public void run() {
					try {
						_log.debug("[DSU] call transition open");
						_fsm.open();
					} catch (RuntimeException e) {
						_log.error(e.getMessage(), e);
					}
				}
			});
		} catch (InterruptedException e) {
			_log.error(e.getMessage(), e);
		}
	}
	

	private String getCallingNumber(String event) {
		String number = "unknown";
		final String CLIP = "+CLIP: \"";
		int idx = event.indexOf(CLIP);
		if (idx != -1) {
			String temp = event.substring(idx + CLIP.length());
			idx = temp.indexOf("\"");
			number = temp.substring(0, idx);
		}
		return number;
	}

	private void setSpeakerVolume(int volume) {
		try {
			if (_gsm != null) {
				double delta = ((double) _config.getMaxLevelVolume() - 1) / 100.0;
				int n = (int) (delta * (double) volume);
				String command = _config.getSpecificCommand("volume" + n)
						+ "\r";
				if (command != null) {
					GsmRequest request = new GsmRequest(command);
					_gsm.send(request);
				}
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	// private void journalize(String message) {
	// if (_jdb != null) { journalize("HANGUP");

	// try {
	// _jdb.journalize("phony", message);
	// } catch (Throwable e) {
	// _log.error(e.getMessage(),e);
	// }
	// }
	// }

	private void publish(PhoneEvent event) {
		if (_producer != null) {
			try {
				_producer.publish(event);
			} catch (Throwable e) {
				_log.error(e.getMessage(), e);
			}
		}
	}

	class RingingTTask implements Runnable {
		public void run() {
			try {
				_log.debug("[DSU] timeout ringing task : hangup");
				hangup();
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
	}

	class DialTask implements Runnable {

		GsmRequest _request;

		public DialTask(GsmRequest request) {
			_request = request;
			_scheduler.execute(this);
		}

		public void run() {
			try {
				_gsm.send(_request);
				try {
					_log.debug("[DSU] call transition online");
					_fsm.online();
				} catch (RuntimeException e) {
					_log.error(e.getMessage(), e);
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
				String response = e.getMessage();
				_log.error("Dial ( " + _request + " )=" + response);
				if (response != null) {
					response = response.trim();
					if (response.equals(BUSY)) {
						_producer.publish(PhoneEvent.BUSY_PHONE_EVENT);
					} else if (response.equals(NO_CARRIER)) {
						_producer.publish(PhoneEvent.NO_CARRIER_PHONE_EVENT);
					} else {
						_producer.publish(PhoneEvent.ERROR_PHONE_EVENT);
					}
				}
			}
			_log.debug("[DSU] end thread dialing");
		}

		public void cancel() {
			try {
				_request.cancel();
			} catch (InterruptedException e) {
			}
		}
	}

	class PhonyStateMachineCallback implements PhonyServiceStateMachine {

		public static final int DEFAULT_AUDIO_MODE = 0;
		public static final int PHONY_AUDIO_MODE = 1;
		public static final int LISTEN_AUDIO_MODE = 2;

		private int _mode = -1;

		public boolean isGsmAvailable() {
			return (_started == true && _gsm != null);
		}

		public void initialize() {
			_log.debug("[DSU] call initialize callback");
			String[] list = _config.getInitAtCommand();
			for (int i = 0; i < list.length; i++) {
				String description = "unk";
				String command = _config.getSpecificCommand(list[i]) + "\r";
				description = _config.getSpecificCommandProperties(list[i])
						.getProperty(PhonyConfig.TAG_CMD_DESC);
				_log.info("initialization : " + description + "("
						+ description.trim() + ")");
				try {
					GsmRequest request = new GsmRequest(command);
					_gsm.send(request);
				} catch (Exception e) {
					_log.error(e.getMessage(), e);
				}
			}
		}

		public void ringing(String phone) {
			_log.debug("[DSU] call ringing callback");

			// stop timer
			if (_ringingTask != null) {
				_log.debug("[DSU] cancel ringing task");
				_scheduler.cancel(_ringingTask);
				_ringingTask = null;
			}

			// set audio mode
			setAudioMode(PHONY_AUDIO_MODE);

			publish(new PhoneRingEvent(phone));

			// start timer
			_log.debug("[DSU] start ringing task");
			_ringingTask = _scheduler.schedule(new RingingTTask(), 6000);

		}

		public void answering() {
			_log.debug("[DSU] call answering callback");
			try {
				GsmRequest reqAnswer = new GsmRequest(AT_ANSWER);
				_gsm.send(reqAnswer);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}

		public void dialing(String phone, boolean listen) {
			_log.debug("[DSU] call dialing callback phone : " + phone
					+ " listen : " + listen);

			// set audio mode
			if (listen) {
				setAudioMode(LISTEN_AUDIO_MODE);
			} else {
				setAudioMode(PHONY_AUDIO_MODE);
			}

			// dialing
			GsmRequest request = new GsmRequest(AT_DIAL_BEGIN + phone
					+ AT_DIAL_END, OK, DIAL_ERROR, 200000);
			_dialingTask = new DialTask(request);

		}

		public void entryClosed() {
			_log.debug("[DSU] call entryClosed callback");
			publish(PhoneEvent.MODEM_NOT_AVAILABLE_PHONE_EVENT);
		}

		public void entryReady() {
			_log.debug("[DSU] call entryReady callback");

			// hanghup
			try {
				GsmRequest request = new GsmRequest(AT_HANGUP);
				_gsm.send(request);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}

			// set audio mode
			setAudioMode(DEFAULT_AUDIO_MODE);

			publish(PhoneEvent.READY_PHONE_EVENT);

		}

		public void exitRinging() {
			_log.debug("[DSU] call exitRinging callback");
			// stop timer
			if (_ringingTask != null) {
				_scheduler.cancel(_ringingTask);
				_ringingTask = null;
			}
		}

		public void entryDialing() {
			_log.debug("[DSU] call entryDialing callback");
			publish(PhoneEvent.DIALING_PHONE_EVENT);
		}

		public void exitDialing() {
			_log.debug("[DSU] call exitDialing callback");
			if (_dialingTask != null) {
				_dialingTask.cancel();
				_dialingTask = null;
			}
		}

		public void entryOnline() {
			_log.debug("[DSU] call entryOnline callback");
			publish(PhoneEvent.ON_LINE_PHONE_EVENT);
		}

		private void setAudioMode(int value) {

			switch (value) {
			case PHONY_AUDIO_MODE:
				if (_mode == value) {
					break;
				}
				// audio conducteur on
				setAudioConducteur(true);
				// phony sound config
				setSoundConfiguration(PHONY_CONFIGURATION);
				// listen mode off
				setListenMode(false);
				// default speaker volume
				setVolume(_volume);
				break;
			case LISTEN_AUDIO_MODE:
				if (_mode == value) {
					break;
				}
				// audio conducteur on
				setAudioConducteur(false);
				// listen sound config
				setSoundConfiguration(LISTEN_CONFIGURATION);
				// listen mode on
				setListenMode(true);
				// speaker volume off
				setVolume(0);
				break;
			case DEFAULT_AUDIO_MODE:
			default:
				// audio conducteur off
				setAudioConducteur(false);
				// default sound config
				setSoundConfiguration(Sound.DEFAULT_CONFIGURATION);
				// listen mode off
				setListenMode(false);
				// default speaker volume
				setVolume(0);
				break;
			}
			_mode = value;
		}

		private void setAudioConducteur(boolean state) {
			if (_cioaudio != null) {
				try {
					_cioaudio.setValue(new Measurement(state ? 1 : 0));
					_log.debug("[DSU] audio conducteur set to " + state);
				} catch (Exception e) {
					_log.error(e.getMessage(), e);
				}
			}
		}

		private void setSoundConfiguration(String name) {
			if (_sound != null) {
				try {
					_log.debug("[DSU] set audio configuration " + name);
					_sound.configure(name);
				} catch (Exception e) {
					_log.error(e.getMessage(), e);
				}
			}
		}

		private void setListenMode(boolean value) {
			try {
				String name = value ? PhonyConfig.ACTIVATE_LISTEN_MODE
						: PhonyConfig.DESACTIVATE_LISTEN_MODE;
				_log.debug("[DSU] set audio mode " + name);
				String command = _config.getSpecificCommand(name) + "\r";
				if (command != null) {
					GsmRequest request = new GsmRequest(command);
					_gsm.send(request);
				}
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}

	}

}
