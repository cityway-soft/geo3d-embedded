package org.avm.elementary.time;

import java.util.Date;

import org.apache.log4j.Logger;
import org.avm.device.gps.Gps;
import org.avm.device.gps.GpsInjector;
import org.avm.device.timemanager.TimeManager;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.Scheduler;
import org.osgi.util.position.Position;

public class TimeManagerImpl implements TimeManager, ConfigurableService,
		ManageableService, Runnable, GpsInjector {

	private int _frequency = 1;

	private Gps _gps;

	private Logger _log;

	private TimeManagerConfig _config;

	private Scheduler _scheduler;

	private Object _taskid;

	public TimeManagerImpl() {
		_log = Logger.getInstance(TimeManager.class);

	}

	public void configure(Config config) {
		_config = (TimeManagerConfig) config;
	}

	public void setGps(Gps gps) {
		_gps = gps;
	}

	public void unsetGps(Gps gps) {
		_config = null;
	}

	public void start() {
		_scheduler = new Scheduler();
		if (_config != null) {
			_frequency = _config.getFrequency();
		}
		run();

	}

	public void stop() {
		_scheduler.cancel(_taskid);
		_scheduler = null;
	}

	public void run() {
		if (!update()) {
			_taskid = _scheduler.schedule(this, _frequency / 10 * 1000);
		} else {
			_taskid = _scheduler.schedule(this, _frequency * 1000);
		}
	}

	private boolean check(Position p) {
		boolean result = (p != null && p.getLatitude() != null
				&& p.getLatitude() != null && p.getLatitude().getValue() != 0d && p
				.getLatitude().getValue() != 0d);
		return result;

	}

	public boolean update() {
		boolean result = false;
		if (_gps == null) {
			return result;
		}

		Position p = _gps.getCurrentPosition();
		result = check(p);
		if (result) {
			long time = p.getLatitude().getTime();
			long delta = Math.abs(time - System.currentTimeMillis());
			if (((delta > 10000) || org.avm.device.plateform.System.isOnTime() == false)) {
				setTime(time);
				_frequency = _config.getFrequency();
			}
		}
		return result;

	}

	public void setTime(long time) {
		Date old = new Date();
		org.avm.device.plateform.System.settime(time / 1000);
		_log.info("time updated from " + old + " to : " + new Date());
	}

}
