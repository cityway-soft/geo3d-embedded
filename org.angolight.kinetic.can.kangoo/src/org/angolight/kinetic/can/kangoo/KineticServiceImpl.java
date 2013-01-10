package org.angolight.kinetic.can.kangoo;

import org.angolight.kinetic.Kinetic;
import org.angolight.kinetic.KineticService;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

public class KineticServiceImpl implements KineticService, ConfigurableService,
		ConsumerService, ProducerService, ManageableService {

	protected PoolableObjectFactory _factory;
	protected ObjectPool _pool;
	protected ProducerManager _producer;
	protected KineticConfig _config;
	protected Logger _log;

	private double _speed;
	private double _odometer;
	private double _period;

	private MedianFilter _filter;
	private int _size;
	private int _count;

	public KineticServiceImpl() {
		_log = Logger.getInstance(this.getClass());
		_log.setPriority(Priority.DEBUG);
	}

	public Kinetic makeObject() {
		Kinetic kinetic = null;
		try {
			kinetic = (Kinetic) _pool.borrowObject();
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		kinetic.setPool(_pool);
		return kinetic;
	}

	public void configure(Config config) {
		_config = (KineticConfig) config;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void start() {
		_factory = new KineticFactory();
		_pool = new StackObjectPool(_factory);
		_speed = -1;
		_odometer = 0;
		_period = _config.getNotificationPeriod();
		_filter = new MedianFilter(_config.getMedianFilterLength());
		_size = Math.max(1, (int) (_config.getNotificationPeriod() / _config
				.getAcquisitionPeriod()));
		_count = 0;
	}

	public void stop() {

	}

	public void notify(Object o) {
		try {
			if (o instanceof PGN_102) {
				PGN_102 pgn = (PGN_102) o;
				
				if (_log.isDebugEnabled()) {
					_log.debug("[DSU] " + pgn.toString());
				}
				
				double speed = pgn.vehiclespeed.getValue() / 3.6;
				if (_speed == -1) {
					_speed = speed;
				}
				_filter.update(speed);
				_count++;

				if (_count >= _size) {
					_count = 0;
					speed = _filter.getValue();

					// initialize kinetic

					Kinetic kinetic = (Kinetic) _pool.borrowObject();
					kinetic.setPool(_pool);

					// set speed
					kinetic.setSpeed(speed);
					kinetic.setPeriod(_period);

					// set acceleration
					double acceleration = 0;
					if ((_speed > 0) && (speed > 0)) {
						acceleration = (speed - _speed) / _period;
					}
					kinetic.setAcceleration(acceleration);

					// set odometer
					double odometer = _odometer
							+ (((_speed + speed) * _period) / 2);
					kinetic.setOdometer(odometer);
					_odometer = odometer;

					// publish kinetic
					if (_log.isDebugEnabled()) {
						_log.debug("[DSU] " + kinetic.toString());
					}
					_producer.publish(kinetic);

					_speed = speed;
				}

				pgn.dispose();
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}
}
