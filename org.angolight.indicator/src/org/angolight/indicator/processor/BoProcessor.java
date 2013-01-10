package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.bo.Bo;
import org.angolight.bo.BoState;
import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.measurement.State;

public class BoProcessor extends Processor {

	private static String TARGET = Bo.class.getName();

	public static final int TRANSITION_TIMEOUT = 200 + 100;

	// measure
	public static final String POSITIVE_STATE_COUNTER = "positive-state-counter";
	public static final String NEGATIVE_STATE_COUNTER = "negativetive-state-counter";

	private Measure[] _psc, _nsc;
	private Measure _acceleration;

	private State _lastState, _previousState;
	private double _lastAcceleration;

	protected BoProcessor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
		initialize();
	}

	private void initialize() {
		_psc = new Measure[BoState.NAMES.length];
		_nsc = new Measure[BoState.NAMES.length];
		for (int i = 0; i < BoState.NAMES.length; i++) {
			_psc[i] = new Measure("positive-" + BoState.NAMES[i], Unit.unity);
			_owner.add(_psc[i]);
			_nsc[i] = new Measure("negative-" + BoState.NAMES[i], Unit.unity);
			_owner.add(_nsc[i]);
		}
	}

	private double getAccelerationValue() {
		if (_acceleration == null) {
			_acceleration = _owner.get(KineticProcessor.ACCELERATION);
			if (_acceleration == null) {
				return 0;
			}
		}
		return _acceleration.getValue();
	}

	public void update(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(TARGET)) {

				if (_lastState != null
						&& _lastState.getValue() != state.getValue()) {
					if ((_lastState.getValue() >= 0)
							&& (_lastState.getValue() < BoState.NAMES.length)) {
						if ((_lastState.getValue() > state.getValue())
								&& (_previousState != null)
								&& ((_lastState.getValue() > _previousState
										.getValue()) || (state.getTime()
										- _lastState.getTime() > TRANSITION_TIMEOUT))) {
							if (_lastAcceleration >= 0) {
								_psc[_lastState.getValue()].add(1);
							} else {
								_nsc[_lastState.getValue()].add(1);
							}
						}
					}

				}
				_lastAcceleration = getAccelerationValue();
				_previousState = _lastState;
				_lastState = state;

				FreeWheel freeWhell = FreeWheel.getInstance(_owner, _config,
						_producer, _jdb);
				freeWhell.update(state);
			}
		}
	}

	public void reset() {
		for (int i = 0; i < BoState.NAMES.length; i++) {
			_psc[i].setValue(0);
			_nsc[i].setValue(0);
		}
		// reset FreeWhell par F0003Processor
	}

	public Map evaluate() {
		Map result = new HashMap();
		for (int i = 0; i < BoState.NAMES.length; i++) {
			result.put(_psc[i].getName(), new Measure(_psc[i]));
			result.put(_nsc[i].getName(), new Measure(_nsc[i]));
		}
		// evaluate FreeWhell par F0003Processor
		return result;
	}

	public Map merge(Map measures) {
		for (int i = 0; i < BoState.NAMES.length; i++) {
			_psc[i].add(((Measure) measures.get(_psc[i].getName())));
			_nsc[i].add(((Measure) measures.get(_nsc[i].getName())));
		}
		// merge FreeWhell par F0003Processor
		return evaluate();
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new BoProcessor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
