package org.avm.business.ecall;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.business.protocol.phoebus.ClotureAlerte;
import org.avm.business.protocol.phoebus.PriseEnCharge;
import org.avm.device.phony.Phony;
import org.avm.device.phony.PhonyInjector;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmService;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.variable.DigitalVariable;
import org.osgi.util.measurement.State;

public class EcallServiceImpl implements EcallService, ConfigurableService,
		ManageableService, ProducerService, ConsumerService,
		EcallServiceStateMachine, JDBInjector, PhonyInjector {

	public static final int ALARM_INDEX=0;
	
	public static final String STATE_NO_ALERT = "STATE_NO_ALERT";

	public static final String STATE_WAIT_ACK = "STATE_WAIT_ACK";

	public static final String STATE_LISTEN_MODE = "STATE_LISTEN_MODE";

	private static final String APPEL_URGENCE = "APPEL URGENCE";

	private Logger _log;

	private ProducerManager _producer;

	private boolean _initialized;

	private EcallServiceStateMachineContext _fsm;

	private Phony _phony;

	private Alarm _alarm;

	private State _state = new State(0, STATE_NO_ALERT);

	private JDB _jdb;

	private AlarmService _alarmService;;

	public EcallServiceImpl() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		// _alarm = new Alarm(false, APPEL_URGENCE, new Date(),
		// EcallService.class
		// .getName(), Alarm.MAX_PRIORITY);
		_alarm = new Alarm(new Integer(ALARM_INDEX));
	}

	public void start() {
		initialize();
		_fsm = new EcallServiceStateMachineContext(this);

		_fsm.setDebugFlag(false);
	}

	public void stop() {
		_initialized = false;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
	}

	public void notify(Object o) {
		if (!_initialized)
			return;

		if (o instanceof PriseEnCharge) {
			PriseEnCharge pec = (PriseEnCharge) o;
			String phone = pec.getTel();
			ack(phone);
		} else if (o instanceof ClotureAlerte) {
			// _alarm.priority = Alarm.MIN_PRIORITY;
			endEcall();
		} else if (o instanceof DigitalVariable) {
			DigitalVariable v = (DigitalVariable) o;
			if (v.getValue().getValue() > 0) {
				startEcall();
			}
		} else if (o instanceof State) {
			_log.debug("RECEIVE :" + o);
			State state = (State) o;
			if (state.getName().equals(AlarmService.class.getName())) {
				Alarm alarm = _alarmService.getAlarm(new Integer(ALARM_INDEX));
				if (alarm.isStatus()){
					startEcall();
				}
				else{
					endEcall();
				}
			}
		}

	}

	private void listenMode(String number) {
		if (_phony != null) {
			try {
				journalize("LISTEN");
				_phony.dialListenMode(number);
			} catch (Exception e) {
				_log.error("error when listenMode :", e);
			}
		} else {
			_log.warn("Phony service not available!");
		}
	}

	public void setPhony(Phony phony) {
		_phony = phony;
	}

	private void initialize() {
		_initialized = true;
	}

	// -- Transitions
	public boolean ack(String phone) {
		boolean res = false;
		try {
			_fsm.ack(phone);
			res = true;
		} catch (RuntimeException e) {
			_log.debug(e.getMessage(), e);
		}
		journalize("ACK");
		return res;
	}

	public boolean endEcall() {
		boolean res = false;
		try {
			_fsm.endEcall();
			res = true;
		} catch (RuntimeException e) {
			_log.debug(e.getMessage(), e);
		}
		journalize("DOWN");
		return res;
	}

	public boolean startEcall() {
		_log.debug("Etat : alerte en cours");
		boolean res = false;
		try {
			_fsm.startEcall();
			res = true;
		} catch (RuntimeException e) {
			_log.debug(e.getMessage(), e);
		}
		journalize("UP");
		return res;
	}

	public void entryNoAlert() {
		_log.debug("Etat initial : pas d'alerte");
		_state = new State(0, STATE_NO_ALERT);
		_producer.publish(_state);
		// _alarm.date = new Date();
		_alarm.setStatus(false);
		_producer.publish(_alarm);
	}

	public void entryWaitAck() {
		_log.debug("Etat : attente prise en charge");
		// _alarm.date = new Date();
		_alarm.setStatus(true);
		// _alarm.priority = Alarm.MAX_PRIORITY;
		_state = new State(1, STATE_WAIT_ACK);
		_producer.publish(_state);
		_producer.publish(_alarm);
	}

	public void entryListenMode() {
		_log.debug("Etat : Ecoute Discrete");
		_state = new State(2, STATE_LISTEN_MODE);
		_producer.publish(_state);
	}

	public List getAlarm() {
		if (_alarm != null && _alarm.isStatus()) {
			LinkedList list = new LinkedList();
			list.add(_alarm);
			return list;
		} else {
			return null;
		}
	}

	public String getProducerPID() {
		return EcallService.class.getName();
	}

	public State getState() {
		return _state;
	}

	public void call(String phone) {
		if (phone != null) {
			listenMode(phone);// _listenModePhoneNumber);
		} else {
			_log.error("Phone number for listen mode is not set");
		}
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	private void journalize(String message) {
		if (_jdb != null) {
			if (_log.isDebugEnabled()) {
				_log.debug(message);
			}
			try {
				_jdb.journalize("ecall", message);
			} catch (Throwable t) {
				_log.error("Journalize error : ", t);
			}
		}
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	public void unsetPhony(Phony phony) {
		_phony = null;
	}

	public void setAlarmService(AlarmService service) {
		_alarmService = service;

	}

	public void unsetAlarmService(AlarmService service) {
		_alarmService = service;
	}

}
