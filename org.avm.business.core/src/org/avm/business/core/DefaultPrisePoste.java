package org.avm.business.core;

import java.util.Date;

import org.apache.log4j.Logger;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.common.Scheduler;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.avm.elementary.variable.Variable;

public class DefaultPrisePoste implements Runnable, AvmInjector, JDBInjector,
		ProducerService {

	private static final double DISTANCE_BEFORE_NOTIFY = 150;

	public static final String ALARM_NAME = "defmat";

	private Variable _odometer;

	private ProducerManager _producer;

	private Avm _avm;

	private Logger _log = Logger.getInstance(this.getClass());

	private JDB _jdb;

	private boolean _sendNeeded = false;

	private Alarm _alarm;

	private Scheduler _scheduler = null;

	private Object _taskId = null;

	private double _lastIndexOdo = Double.MAX_VALUE;

	public DefaultPrisePoste() {
	}

	public void start() {
		_sendNeeded = false;
	  //  _log.setPriority(Priority.DEBUG);
		if (_taskId != null) {
			stop();
		}

		_scheduler = new Scheduler();

		try {
			if (_odometer != null) {
				_lastIndexOdo = _odometer.getValue().getValue();
			}
			_log.debug("Running service 'defaut prise de poste'");
			_taskId = _scheduler.schedule(this, 3 * 1000, true);
		} catch (Exception e) {
			_log.error("Error schedule task 'DefautPrisePoste' ", e);
		}
	}

	public void stop() {
		if (_taskId != null) {
			_scheduler.cancel(_taskId);
		}
		_taskId = null;
		annulation();
		_log.debug("Service 'defaut prise de poste' stopped.");

	}

	public void setOdometer(Variable odometer) {
		_odometer = odometer;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void setAvm(Avm avm) {
		_avm = avm;
	}

	private void avertissement() {
		_log.debug("Defaut de prise de service : avertissement !");
		Alarm alarm = new Alarm(true, ALARM_NAME, new Date(), Avm.class
				.getName(), Alarm.MIN_PRIORITY);
		_alarm = alarm;
		_producer.publish(alarm);
	}

	private void alarme() {
		_log.debug("Defaut de prise de service : alarme !");
		Alarm alarm = new Alarm(true, ALARM_NAME, new Date(), Avm.class
				.getName(), Alarm.MAX_PRIORITY);
		_alarm = alarm;
		_producer.publish(alarm);
	}

	private void annulation() {
		_log.debug("Defaut de prise de service : annulation !");
		_lastIndexOdo = -1;
		Alarm alarm = new Alarm(false, ALARM_NAME, new Date(), Avm.class
				.getName(), Alarm.MIN_PRIORITY);
		_alarm = alarm;
		_producer.publish(alarm);
	}

	public void run() {
		if (_odometer != null) {

			double val = _odometer.getValue().getValue();
			if (_lastIndexOdo == -1){
				_lastIndexOdo = _odometer.getValue().getValue();
				return;
			}
			double delta = (val - _lastIndexOdo);
			if (_log.isDebugEnabled()) {
				_log.debug("odometer value:" + val + " delta="
						+ delta);
			}

			if (_avm != null && _producer != null) {
				boolean pp = (_avm.getModel().getAuthentification()!=null && _avm.getModel().getAuthentification().isPrisePoste());
				if ((delta > DISTANCE_BEFORE_NOTIFY)
						&& !pp) {
					if (_sendNeeded == false) {
						journalize("DEFMAT;");
						_sendNeeded = true;
						avertissement();
						_lastIndexOdo = _odometer.getValue().getValue();
					} else {
						alarme();
						if (_taskId != null) {
							_scheduler.cancel(_taskId);
						}
					}
				}
			} else {
				if (_avm == null) {
					_log.warn("avm is null");
				}
				if (_producer == null) {
					_log.warn("producer is null");
				}
			}

		}

	}

	private void journalize(String message) {
		_log.info(message);
		if (_jdb != null) {
			_jdb.journalize("AVM", message);
		}

	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	public void unsetAvm(Avm avm) {
		_avm = null;
	}

	public Alarm getAlarm() {
		return _alarm;
	}

}
