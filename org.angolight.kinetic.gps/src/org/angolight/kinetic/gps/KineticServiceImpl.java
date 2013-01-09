package org.angolight.kinetic.gps;

import org.angolight.kinetic.Kinetic;
import org.angolight.kinetic.KineticService;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.osgi.util.position.Position;

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

	public KineticServiceImpl() {
		_log = Logger.getInstance(this.getClass());
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
		_period = 1;
	}

	public void stop() {

	}

	public void notify(Object o) {
		try {
			if (o instanceof Position) {
				Position position = (Position) o;
				if (!((position.getLatitude().getValue() == 0) && (position
						.getLongitude().getValue() == 0))) {
					double speed = position.getSpeed().getValue();

					// initialize kinetic
					if (_speed >= 0) {
						Kinetic kinetic = (Kinetic) _pool.borrowObject();
						kinetic.setPool(_pool);

						// set speed
						kinetic.setSpeed(speed);
						kinetic.setPeriod(_period);

						// set acceleration
						double acceleration = 0;
						if ((_speed == 0) && (speed > 0)) {
							acceleration = (speed - _config.getMinimumSpeedUp())
									/ _period;
						} else if ((_speed > 0) && (speed == 0)) {
							acceleration = (_config.getMinimumSpeedDown() - _speed)
									/ _period;
						} else if ((_speed > 0) && (speed > 0)) {
							acceleration = (speed - _speed) / _period;
						}
						kinetic.setAcceleration(acceleration);

						// set odometer
						double odometer = _odometer
								+ (((_speed + speed) * _period) / 2);
						kinetic.setOdometer(odometer);
						_odometer = odometer;

						// publish kinetic
						_producer.publish(kinetic);
					}
					_speed = speed;

				}
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}
}
