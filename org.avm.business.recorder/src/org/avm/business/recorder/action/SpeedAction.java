package org.avm.business.recorder.action;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.avm.business.recorder.Action;
import org.avm.business.recorder.ActionFactory;
import org.avm.business.recorder.Journalizable;
import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.alarm.AlarmProvider;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.variable.Variable;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.position.Position;

public class SpeedAction extends Journalizable implements Action, AlarmProvider {

	private static final String ALARM_SPEED = "speed";

	private long _t0 = -1;

	private SpeedInterval _currentSpeedLimit;

	long t;

	private List _list;

	private ProducerManager _producer;

	private Alarm _alarm;

	private Variable _asmsr;

	private double _totalspeed;

	private int _countspeed;

	private double _maxspeed;

	public SpeedAction() {
		super();
		_list = new LinkedList();
		_list.add(new SpeedInterval(0, 85, new Alarm(false, ALARM_SPEED,
				new Date(), SpeedAction.class.getName(), Alarm.MIN_PRIORITY)));
		_list.add(new SpeedInterval(92, 110, new Alarm(true, ALARM_SPEED,
				new Date(), SpeedAction.class.getName(), Alarm.MAX_PRIORITY)));
		_list.add(new SpeedInterval(112, 120, new Alarm(true, ALARM_SPEED,
				new Date(), SpeedAction.class.getName(), Alarm.MAX_PRIORITY)));
		_list.add(new SpeedInterval(132, Double.MAX_VALUE, new Alarm(true,
				ALARM_SPEED, new Date(), SpeedAction.class.getName(),
				Alarm.MAX_PRIORITY)));
	}

	public void compute(Object o) {
		try {
			Position position = (Position) o;
			double speed = position.getSpeed().getValue() * 3.6;
			long data = 0;
			SpeedInterval sl = getCurrentSpeedInterval(speed);
			if (sl != _currentSpeedLimit && sl != null) {
				if (_t0 != -1) {
					t = System.currentTimeMillis();
					// t0=_positionHighSpeed.getSpeed().getTime();
					data = (t - _t0) / 1000;
				}
				journalize(_currentSpeedLimit + ";" + Long.toString(data));
				_currentSpeedLimit = sl;
				_alarm = sl.getAlarm();
				publish(_alarm);
				_t0 = System.currentTimeMillis();
			}
			computeSpeedRatio(speed);
		} catch (Throwable t) {
			_log.error("Error on compute ", t);
		}

	}

	private SpeedInterval getCurrentSpeedInterval(double speed) {
		Iterator iter = _list.iterator();
		SpeedInterval sl = null;
		while (iter.hasNext()) {
			sl = (SpeedInterval) iter.next();
			if (sl.isInside(speed)) {
				return sl;
			}
		}
		return null;
	}

	class SpeedInterval {
		private double _limitInf;

		private double _limitSup;

		private Alarm _alarm;

		public SpeedInterval(double limitInf, double limitSup, Alarm alarm) {
			_limitInf = limitInf;
			_limitSup = limitSup;
			_alarm = alarm;
		}

		public Alarm getAlarm() {
			return _alarm;
		}

		public boolean isInside(double speed) {
			return (speed >= _limitInf && speed < _limitSup);
		}

		public String toString() {
			return "SPEED" + (int) _limitInf;
		}
	}

	public void configure(Object o) {
		if (o instanceof ProducerManager) {
			_producer = (ProducerManager) o;
		} else if (o instanceof Variable) {
			Variable var = (Variable) o;
			if (var.getName().equals("average-speed-max-speed-ratio")) {
				_asmsr = var;
				_asmsr.setValue(new Measurement(0.0d));
			}
		} else if (o instanceof JDB) {
			setJdb((JDB) o);
		}
	}

	private void computeSpeedRatio(double speed) {
		_totalspeed += speed;
		_countspeed++;

		_maxspeed = Math.max(_maxspeed, speed);
		double val = 1.0d;
		if (_maxspeed != 0.0d) {
			val = (_totalspeed / _countspeed) / _maxspeed;
		}

		if (_asmsr != null) {
			_asmsr.setValue(new Measurement(val));
		}

	}

	private void publish(Object o) {
		if (_producer != null) {
			_producer.publish(o);
		}
	}

	static {
		ActionFactory.addAction(Position.class, new SpeedAction());
	}

	public List getAlarm() {
		if (_alarm.status) {
			LinkedList list = new LinkedList();
			list.add(_alarm);
			return list;
		} else {
			return null;
		}
	}

	public String getProducerPID() {
		return SpeedAction.class.getName();
	}

}
