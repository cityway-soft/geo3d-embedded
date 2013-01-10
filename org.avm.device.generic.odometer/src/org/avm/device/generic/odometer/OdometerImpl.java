package org.avm.device.generic.odometer;

import org.apache.log4j.Logger;
import org.avm.device.odometer.Odometer;
import org.avm.elementary.common.BundleThreadFactory;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.common.ThreadFactoryInjector;
import org.osgi.util.position.Position;

import EDU.oswego.cs.dl.util.concurrent.DirectExecutor;

public class OdometerImpl implements Odometer, ConfigurableService,
		ManageableService, ProducerService, ConsumerService,
		ThreadFactoryInjector {

	Position _previous;

	double _counter;

	private Logger _log;

	private ProducerManager _producer;

	OdometerConfig _config;

	private DirectExecutor _executor;

	private BundleThreadFactory _factory;

	public OdometerImpl() {
		_log = Logger.getInstance(this.getClass());
	}

	public void start() {
		_log.info("Start Service Odometer " + _config);
		_executor = new DirectExecutor();
		_log.info("Service Odometer started.");
	}

	public void stop() {
		_log.debug("Stop Service Odometer");
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void configure(Config config) {
		_config = (OdometerConfig) config;
	}

	public int getCounterValue() {
		int value = (int) (((int) (Math.ceil(_counter) / _config
				.getCounterFactor().doubleValue())) % 0xffff);
		return value;
	}

	public void setThreadFactory(BundleThreadFactory factory) {
		_factory = factory;
	}

	public void notify(Object o) {
		if (o instanceof Position) {
			Position p = (Position) o;
			_log.debug("Current Position =" + p.getLatitude() + ", "
					+ p.getLongitude() + ", speed=" + p.getSpeed());
			if (p.getLatitude().getValue() == 0d
					&& p.getLongitude().getValue() == 0d) {
				return;
			}

			try {

				OdometerCallable callable = new OdometerCallable(this, p);
				_executor.execute(callable);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}

		}

	}

	public double getCounterFactor() {
		return _config.getCounterFactor().doubleValue();
	}

}