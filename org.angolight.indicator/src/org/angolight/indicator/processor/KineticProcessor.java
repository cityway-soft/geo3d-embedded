package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.angolight.kinetic.Kinetic;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;

public class KineticProcessor extends Processor {

	private static String TARGET = Kinetic.class.getName();

	// config
	public final static String MINIMUM_SPEED_UP_TAG = "minimum-speed-up";
	public final static String MINIMUM_SPEED_DOWN_TAG = "minimum-speed-down";

	// measure acceleration
	public static final String ACCELERATION = "acceleration";
	public static final String POSITIVE_ACCELERATION_COUNTER = "positive-acceleration-counter";
	public static final String NEGATIVE_ACCELERATION_COUNTER = "negative-acceleration-counter";
	public static final String POSITIVE_ACCELERATION_MAX = "positive-acceleration-max";
	public static final String NEGATIVE_ACCELERATION_MAX = "negative-acceleration-max";
	public static final String POSITIVE_ACCELERATION_SUM = "positive-acceleration-sum";
	public static final String NEGATIVE_ACCELERATION_SUM = "negative-acceleration-sum";

	// measure odometer
	public static final String DISTANCE = "distance";
	public static final String TIME = "time";

	// measure speed
	public static final String STOP_COUNTER = "stop-counter";
	public static final String STOP_TIME = "stop-time";
	public static final String STOP_STATE = "stop-state";

	private Measure _pac, _nac, _pam, _nam, _pas, _nas;
	private Measure _distance, _time;
	private Measure _sc, _st;

	private Measure _stop_state, _acceleration;

	private long _timeOffset;
	private double _distanceOffset;
	private long _stopTimeOffset;

	private double _speedup;
	private double _speedown;

	protected KineticProcessor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);

		// config
		Properties p = _config.getProperties();
		_speedup = Double.parseDouble((String) p.get(MINIMUM_SPEED_UP_TAG));
		_speedown = Double.parseDouble((String) p.get(MINIMUM_SPEED_DOWN_TAG));

		initialize();
	}

	private void initialize() {
		_stopTimeOffset = System.currentTimeMillis(); // DLA
		_timeOffset = System.currentTimeMillis(); // DLA;

		_pac = new Measure(POSITIVE_ACCELERATION_COUNTER, Unit.unity);
		_owner.add(_pac);
		_nac = new Measure(NEGATIVE_ACCELERATION_COUNTER, Unit.unity);
		_owner.add(_nac);
		_pam = new Measure(POSITIVE_ACCELERATION_MAX, Unit.m_s2);
		_owner.add(_pam);
		_nam = new Measure(NEGATIVE_ACCELERATION_MAX, Unit.m_s2);
		_owner.add(_nam);
		_pas = new Measure(POSITIVE_ACCELERATION_SUM, Unit.m_s2);
		_owner.add(_pas);
		_nas = new Measure(NEGATIVE_ACCELERATION_SUM, Unit.m_s2);
		_owner.add(_nas);

		_distance = new Measure(DISTANCE, Unit.m);
		_owner.add(_distance);
		_time = new Measure(TIME, Unit.s);
		_owner.add(_time);

		_sc = new Measure(STOP_COUNTER, Unit.unity);
		_owner.add(_sc);
		_st = new Measure(STOP_TIME, Unit.s);
		_owner.add(_st);

		_stop_state = new Measure(STOP_STATE, Unit.unity);
		_owner.add(_stop_state);
		_acceleration = new Measure(ACCELERATION, Unit.m_s2);
		_owner.add(_acceleration);

	}

	public void update(Object o) {
		if (o instanceof Kinetic) {
			Kinetic kinetic = (Kinetic) o;

			// acceleration
			double acceleration = kinetic.getAcceleration();
			_acceleration.setValue(acceleration);
			if (acceleration > 0) {
				_pac.add(1);
				_pas.add(acceleration);
				if (acceleration > _pam.getValue())
					_pam.setValue(acceleration);

			} else if (acceleration < 0) {
				_nac.add(1);
				_nas.add(acceleration);
				if (acceleration < _nam.getValue()) // [DLA] acc negative => '<'
													// au lieu de '>'
					_nam.setValue(acceleration);
			}

			// odometer
			double odometer = kinetic.get0dometer();
			if ((_distanceOffset == 0) || (odometer < _distanceOffset))
				_distanceOffset = odometer;
			_distance.setValue(odometer - _distanceOffset);
			_time.setValue(System.currentTimeMillis() - _timeOffset); // DLA :
																		// setTime
																		// ou
																		// setValue
																		// ?

			// speed
			double speed = kinetic.getSpeed();
			if (_stop_state.getValue() == 1) {
				if (speed > _speedup) {
					_stop_state.setValue(0);
					long delay = System.currentTimeMillis() - _stopTimeOffset;
					if (delay > 0) {
						_st.add(delay);
					}
				}
			} else {
				if (speed < _speedown) {
					_stop_state.setValue(1);
					_stopTimeOffset = System.currentTimeMillis();
					_sc.add(1);
				}
			}

			kinetic.dispose();
		}

	}

	public void reset() {
		_pac.setValue(0);
		_nac.setValue(0);
		_pam.setValue(0);
		_nam.setValue(0);
		_pas.setValue(0);
		_nas.setValue(0);

		_timeOffset = System.currentTimeMillis();
		_distanceOffset = 0;
		_distance.setValue(0);
		_time.setValue(0);

		_stopTimeOffset = System.currentTimeMillis();
		_sc.setValue(0);
		_st.setValue(0);
	}

	public Map evaluate() {
		Map result = new HashMap();
		result.put(_pac.getName(), new Measure(_pac));
		result.put(_nac.getName(), new Measure(_nac));
		result.put(_pam.getName(), new Measure(_pam));
		result.put(_nam.getName(), new Measure(_nam));
		result.put(_pas.getName(), new Measure(_pas));
		result.put(_nas.getName(), new Measure(_nas));

		result.put(_distance.getName(), new Measure(_distance));
		result.put(_time.getName(), new Measure(_time));

		result.put(_sc.getName(), new Measure(_sc));
		
		
		if (_stop_state.getValue() == 1) {
				//-- [DLA/FLA] on compte ici le temps passe a l'arret (au cas ou speed reste < speedmin)
				long delay = System.currentTimeMillis() - _stopTimeOffset;
				if (delay > 0) {
					_st.add(delay);
				}
				_stopTimeOffset =  System.currentTimeMillis();
		}
		
		result.put(_st.getName(), new Measure(_st));

		return result;
	}

	public Map merge(Map measures) {

		_pam.setValue(Math.max(((Measure) measures
				.get(POSITIVE_ACCELERATION_MAX)).getValue(), _pam.getValue()));
		_nam.setValue(Math.min(((Measure) measures
				.get(NEGATIVE_ACCELERATION_MAX)).getValue(), _nam.getValue()));
		_pac.add(((Measure) measures.get(POSITIVE_ACCELERATION_COUNTER)));
		_nac.add(((Measure) measures.get(NEGATIVE_ACCELERATION_COUNTER)));
		_pas.add(((Measure) measures.get(POSITIVE_ACCELERATION_SUM)));
		_nas.add(((Measure) measures.get(NEGATIVE_ACCELERATION_SUM)));

		_distance.add(((Measure) measures.get(DISTANCE)));
		_time.add(((Measure) measures.get(TIME)));

		_sc.add(((Measure) measures.get(STOP_COUNTER)));
		_st.add(((Measure) measures.get(STOP_TIME)));

		return evaluate();
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new KineticProcessor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
