package org.avm.device.linux.odometer;

import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.avm.device.linux.odometer.bundle.ConfigImpl;
import org.avm.device.odometer.Odometer;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.osgi.util.position.Position;

public class OdometerImpl implements Odometer, ConfigurableService,
		ManageableService, ConsumerService, Runnable {

	private Logger _log;

	private OdometerConfig _config;

	private static final String DEVICE = "/dev/vsi3";

	private static final int SAMPLE_FACTOR = 5;

	private Thread _thread;

	private Model _model;

	private int _counter;

	class Model {
		Position first_position;
		int first_counter;
		Position previous_position;
		int previous_counter;
		int count;
	}

	public OdometerImpl() {
		_log = Logger.getInstance(this.getClass());
		_model = new Model();
	}

	public void start() {
		if (_thread == null) {
			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();
		}
	}

	public void stop() {
		if (_thread != null && !_thread.isInterrupted()) {
			_thread.interrupt();
			_thread = null;
		}
	}

	public void configure(Config config) {
		_config = (OdometerConfig) config;
	}

	public int getCounterValue() {
		int value = (int) (((int) (Math.ceil(_counter) / _config
				.getCounterFactor())) % 0xffff);
		return value;
	}

	public double getCounterFactor() {
		return _config.getCounterFactor();
	}

	public void notify(Object o) {
		if (o instanceof Position) {
			Position p = (Position) o;
			update(p);
		}
	}

	public void run() {
		FileInputStream in;
		try {
			in = new FileInputStream(DEVICE);
			byte[] buffer = new byte[2];
			int previous = -1;
			while (_thread != null && !_thread.isInterrupted()) {
				if (in.read(buffer) != 2) {
					break;
				}
				int value = (int) ((buffer[0] << 8) | (buffer[1] & 0x00ff));
				if (previous == -1) {
					previous = value;
					continue;
				}

				int delta = (value > previous) ? (value - previous)
						: (65536 - previous + value);
				_counter += delta;
				previous = value;
			}
			in.close();
		} catch (Exception e) {
			_log.error(e);
		}
	}

	private boolean check() {

		if (_model.first_counter == _model.previous_counter) {
			return false;
		}

		if (_model.previous_position.getSpeed().getValue() < _config
				.getSpeedLimit()) {
			return false;
		}

		if (Math
				.abs((_model.previous_position.getTrack().getValue() - _model.first_position
						.getTrack().getValue())) > _config.getTrackLimit()) {
			return false;
		}

		return true;
	}

	private void update(Position p) {

		if (_model.count == 0) {
			_model.first_position = p;
			_model.first_counter = _counter;
			_model.previous_counter = _counter;
			_model.previous_position = p;
			_model.count++;
			return;
		}

		if (check()) {
			_model.previous_counter = _counter;
			_model.previous_position = p;
			_model.count++;
		} else {
			int limit = (_config.getCounterFactor() != 1d) ? _config
					.getSampleLimit()
					* SAMPLE_FACTOR : _config.getSampleLimit();
			if (_model.count > limit) {
				evaluate();
			}
			_model.count = 0;
		}
	}

	private void evaluate() {
		double d = computeHaversineFormula(_model.first_position,
				_model.previous_position);
		int c = _model.previous_counter - _model.first_counter;
		_config.setCounterFactor(d / c);
		((ConfigImpl) _config).updateConfig(false);
	}

	private static double computeHaversineFormula(Position p1, Position p2) {

		final double R = 6371008.8;

		double lon1 = p1.getLongitude().getValue();
		double lat1 = p1.getLatitude().getValue();

		double lon2 = p2.getLongitude().getValue();
		double lat2 = p2.getLatitude().getValue();

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;

		return d;
	}

}