package org.angolight.halfcycle.impl;

import java.text.MessageFormat;

import org.angolight.halfcycle.HalfCycle;
import org.angolight.halfcycle.HalfCycleService;
import org.angolight.halfcycle.state.HalfCycleStateMachine;
import org.angolight.halfcycle.state.HalfCycleStateMachineContext;
import org.angolight.kinetic.Kinetic;
import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;

public class HalfCycleServiceImpl implements HalfCycleService,
		HalfCycleStateMachine, ConfigurableService, ConsumerService,
		ProducerService, ManageableService {

	private HalfCycleConfig _config;
	private ProducerManager _producer;
	private HalfCycleStateMachineContext _state;
	private HalfCycle _halfCycle;
	private Logger _log;

	public HalfCycleServiceImpl() {
		_log = Logger.getInstance(this.getClass());
		_state = new HalfCycleStateMachineContext(this);
		reset();
	}

	public void configure(Config config) {
		_config = (HalfCycleConfig) config;
		try {
			Object[] arguments = { System.getProperty("org.avm.home") };
			String text = MessageFormat.format(_config.getFilename(),
					arguments);
			HalfCycle.LoadHVCurves(text);
		} catch (Exception e) {
			HalfCycle.LoadDefaultHVCurves();
		}
	}

	public void onKinetic(Kinetic kinetic) {
		_state.onKinetic(kinetic);
	}

	public void notify(Object o) {
		if (o instanceof Kinetic) {
			Kinetic kinetic = (Kinetic) o;
			try {
				onKinetic(kinetic);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			} finally {
				kinetic.dispose();
			}
		}
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void start() {

	}

	public void stop() {

	}

	public double getMinimumSpeedDown() {
		return _config.getMinimumSpeedDown();
	}

	public double getMinimumSpeedUp() {
		return _config.getMinimumSpeedUp();
	}

	public double getNegativeAccelerationDown() {
		return _config.getNegativeAccelerationDown();
	}

	public double getNegativeAccelerationUp() {
		return _config.getNegativeAccelerationUp();
	}

	public double getPositiveAccelerationDown() {
		return _config.getPositiveAccelerationDown();
	}

	public double getPositiveAccelerationUp() {
		return _config.getPositiveAccelerationUp();
	}

	public void notifyHalfCycle() {
		_producer.publish(_halfCycle);
	}

	public void initilizeHalfCycleSpeed(double speed) {
		_halfCycle.initilizeHalfCycleSpeed(speed);
	}

	public void reset() {
		_halfCycle = new HalfCycle();
	}

	public void updatePositiveHalfCycle(double speed, double acceleration) {
		_halfCycle.updatePositiveHalfCycle(speed, acceleration);
	}

	public void updateNegativeHalfCycle(double speed, double acceleration) {
		_halfCycle.updateNegativeHalfCycle(speed, acceleration);
	}

	public void updatePositiveH1H2(double speed) {
		_halfCycle.updatePositiveH1H2(speed);
	}

	public void updateNegativeH1H2(double speed) {
		_halfCycle.updateNegativeH1H2(speed);
	}

}
