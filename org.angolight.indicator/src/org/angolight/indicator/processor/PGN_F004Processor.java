package org.angolight.indicator.processor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.angolight.indicator.Indicator;
import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.apache.log4j.Logger;
import org.avm.elementary.can.parser.fms.PGN_F004;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.measurement.State;

public class PGN_F004Processor extends Processor {

	private static String TARGET = PGN_F004.class.getName();

	// config
	public final static String OVERRIVVING_UP_TAG = "overrivving-up";
	public final static String OVERRIVVING_DOWN_TAG = "overrivving-down";
	public final static String UNDERRIVVING_UP_TAG = "underrivving-up";
	public final static String UNDERRIVVING_DOWN_TAG = "underrivving-down";
	public final static String LONGSTOP_TIME_THRESHOLD_TAG = "longstop-time-threshold";

	public final static double DEFAULT_OVERRIVVING_UP = 1550;
	public final static double DEFAULT_OVERRIVVING_DOWN = 1500;
	public final static double DEFAULT_UNDERRIVVING_UP = 1000;
	public final static double DEFAULT_UNDERRIVVING_DOWN = 950;
	public final static double DEFAULT_LONGSTOP_TIME_THRESHOLD = 240; // 4
	// minutes

	private double _overrivvingUp;
	private double _overrivvingDown;
	private double _underrivvingUp;
	private double _underrivvingDown;
	private double _longstopTimeThreshold;

	// measure
	public static final String ENGINESPEED_MAX = "enginespeed-max";
	public static final String ENGINESPEED_COUNTER = "enginespeed-counter";
	public static final String ENGINESPEED_SUM = "enginespeed-sum";
	public static final String OVERREVVING_TIME = "overrevving-time";
	public static final String UNDERREVVING_TIME = "underrevving-time";
	public static final String STOP_ENGINEON_TIME = "stop-engineon-time";
	public static final String LONGSTOP_ENGINEON_TIME = "longstop-engineon-time";
	public static final String LONGSTOP_ENGINEON_COUNTER = "longstop-engineon-counter";
	public static final String LONGSTOP_ENGINEON_STATE = "longstop-engineon-state";

	private Measure _em, _ec, _es, _ot, _ut, _set, _let, _lec, _les;

	public static final String NOX_SUM = "nox-sum";
	public static final String CO_SUM = "co-sum";
	public static final String HC_SUM = "hc-sum";

	private Measure _ns, _cs, _hs;

	private double _nox, _co, _hc;
	private long _now;

	private Pollutants _pollutants;

	private Measure _stop_state;
	private boolean _stopped;

	private boolean _overrevving;
	private long _overrevvingTimeOffset;

	private boolean _underrevving;
	private long _underrevvingTimeOffset;

	private boolean _stopEngineon;
	private long _stopEngineonTimeOffset;

	private boolean _longstopEngineon;

	private Logger _log = Logger.getInstance(PGN_F004Processor.class);

	protected PGN_F004Processor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);

		// config
		Properties p = _config.getProperties();
		_overrivvingUp = (p.get(OVERRIVVING_UP_TAG) == null) ? DEFAULT_OVERRIVVING_UP
				: Double.parseDouble((String) p.get(OVERRIVVING_UP_TAG));
		_overrivvingDown = (p.get(OVERRIVVING_DOWN_TAG) == null) ? DEFAULT_OVERRIVVING_DOWN
				: Double.parseDouble((String) p.get(OVERRIVVING_DOWN_TAG));
		_underrivvingUp = (p.get(UNDERRIVVING_UP_TAG) == null) ? DEFAULT_UNDERRIVVING_UP
				: Double.parseDouble((String) p.get(UNDERRIVVING_UP_TAG));
		_underrivvingDown = (p.get(UNDERRIVVING_DOWN_TAG) == null) ? DEFAULT_UNDERRIVVING_DOWN
				: Double.parseDouble((String) p.get(UNDERRIVVING_DOWN_TAG));
		_longstopTimeThreshold = (p.get(LONGSTOP_TIME_THRESHOLD_TAG) == null) ? DEFAULT_LONGSTOP_TIME_THRESHOLD
				: Double.parseDouble((String) p
						.get(LONGSTOP_TIME_THRESHOLD_TAG));
		_longstopTimeThreshold *= 1000; // DLA : en ms

		String filename = _config.getFilename();
		File bornes = new File(filename + "/bornes.txt");
		File nox = new File(filename + "/nox.txt");
		File thc = new File(filename + "/thc.txt");
		File co = new File(filename + "/co.txt");
		if (bornes.exists() && nox.exists() && thc.exists() && co.exists()) {
			_pollutants = new Pollutants(Pollutants.loadDoubleArray(bornes
					.getAbsolutePath()), Pollutants.loadDoubleArray(nox
					.getAbsolutePath()), Pollutants.loadDoubleArray(thc
					.getAbsolutePath()), Pollutants.loadDoubleArray(co
					.getAbsolutePath()));

		} else {
			_log.warn("Pollutant files not found, use default values !");
			_pollutants = new Pollutants();
		}

		initialize();
	}

	private void initialize() {
		_em = new Measure(ENGINESPEED_MAX, Unit.unity);
		_owner.add(_em);
		_ec = new Measure(ENGINESPEED_COUNTER, Unit.unity);
		_owner.add(_ec);
		_es = new Measure(ENGINESPEED_SUM, Unit.unity);
		_owner.add(_es);
		_ot = new Measure(OVERREVVING_TIME, Unit.s);
		_owner.add(_ot);
		_ut = new Measure(UNDERREVVING_TIME, Unit.s);
		_owner.add(_ut);
		_set = new Measure(STOP_ENGINEON_TIME, Unit.s);
		_owner.add(_set);
		_let = new Measure(LONGSTOP_ENGINEON_TIME, Unit.s);
		_owner.add(_let);
		_lec = new Measure(LONGSTOP_ENGINEON_COUNTER, Unit.unity);
		_owner.add(_lec);
		_les = new Measure(LONGSTOP_ENGINEON_STATE, Unit.unity);
		_owner.add(_les);

		_ns = new Measure(NOX_SUM, Unit.unity);
		_owner.add(_ns);
		_cs = new Measure(CO_SUM, Unit.unity);
		_owner.add(_cs);
		_hs = new Measure(HC_SUM, Unit.unity);
		_owner.add(_hs);

		_stopEngineonTimeOffset = _underrevvingTimeOffset = _overrevvingTimeOffset = System
				.currentTimeMillis();

	}

	public void update(Object o) {
		if (o instanceof PGN_F004) {
			PGN_F004 pgn = (PGN_F004) o;

			_stop_state = _owner.get(KineticProcessor.STOP_STATE);
			if (_stop_state == null)
				return;

			if (pgn.spn190.isValid()) {
				double enginespeed = pgn.spn190.getValue();
				long now = System.currentTimeMillis();
				_ec.add(1);
				_es.add(enginespeed);
				if (enginespeed > _em.getValue())
					_em.setValue(enginespeed);

				// Véhicule arreté
				if (_stop_state.getValue() == 1) {

					// Le véhicule vient-il de s'arrêter ?
					if (!_stopped) {
						_stopped = true;
						if (_overrevving) {
							long delay = now - _overrevvingTimeOffset;
							if (delay > 0)
								_ot.add(delay);
						}
						if (_underrevving) {
							long delay = now - _underrevvingTimeOffset;
							if (delay > 0)
								_ut.add(delay);
						}
						_overrevving = _underrevving = false;
					}

					// Moteur démarré ?
					if (_stopEngineon) {
						if (enginespeed == 0) {
							long delay = now - _stopEngineonTimeOffset;
							_stopEngineon = false;
							if (delay > 0) {
								_set.add(delay);
							}
							if (delay >= _longstopTimeThreshold) {
								_let.add(delay);
							}

							if (_longstopEngineon) {
								_longstopEngineon = false;

								// Notification de fin d'un long arrêt moteur
								// allumé
								_les.setValue(0);
								_producer.publish(new State(0,
										LONGSTOP_ENGINEON_STATE));

							}
						} else {
							// Détection d'un temps long au ralenti
							if (!_longstopEngineon) {
								long delay = now - _stopEngineonTimeOffset;
								if (delay >= _longstopTimeThreshold) {
									_longstopEngineon = true;
									_lec.add(1);

									// Notification de début d'un long arrêt
									// moteur allumé
									_les.setValue(1);
									_producer.publish(new State(1,
											LONGSTOP_ENGINEON_STATE));
								}
							}
						}
					} else {
						// Démarrage du moteur ?
						if (enginespeed > 0) {
							_stopEngineon = true;
							_stopEngineonTimeOffset = now;
						}
					}
				} else {
					// Démarrage du véhicule ?
					if (_stopped) {
						_stopped = false;

						if (_stopEngineon) {
							long delay = now - _stopEngineonTimeOffset;
							if (delay > 0) {
								_set.add(delay);
							}

							_stopEngineon = false;

							if (_longstopEngineon) {
								_longstopEngineon = false;
								_let.add(delay);

								// Notification de fin d'un long arrêt moteur
								// allumé
								_les.setValue(0);
								_jdb.journalize(Indicator.CATEGORY, "LONGSTOP;"
										+ delay);
								_producer.publish(new State(0,
										LONGSTOP_ENGINEON_STATE));
							}
						}
					}

					if (_overrevving) {
						if (enginespeed < _overrivvingDown) {
							_overrevving = false;
							long delay = now - _overrevvingTimeOffset;
							if (delay > 0)
								_ot.add(delay);
						}
					} else {
						if (enginespeed > _overrivvingUp) {
							_overrevving = true;
							_overrevvingTimeOffset = now;
						}
					}

					if (_underrevving) {
						if (enginespeed > _underrivvingUp) {
							_underrevving = false;
							long delay = _now - _underrevvingTimeOffset;
							if (delay > 0)
								_ut.add(delay);
						}
					} else {
						if (enginespeed < _underrivvingDown) {
							_underrevving = true;
							_underrevvingTimeOffset = now;
						}
					}
				}
			}

			// pollutants
			if (pgn.spn190.isValid() && pgn.spn513.isValid()) {
				double torque = pgn.spn513.getValue();
				double enginepeed = pgn.spn190.getValue();

				long now = System.currentTimeMillis();
				int line = Pollutants.dichotomieInterval(torque,
						_pollutants.torque);
				int column = Pollutants.dichotomieInterval(enginepeed,
						_pollutants.enginespeed);

				double nox = _pollutants.nox[line][column];
				double hc = _pollutants.hc[line][column];
				double co = _pollutants.co[line][column];

				if (nox < 0)
					nox = 0;
				if (hc < 0)
					hc = 0;
				if (co < 0)
					co = 0;

				if (_now != 0) {
					double delta = ((double) (now - _now)) / 1000;
					_ns.add((nox + _nox) * delta / 2);
					_cs.add((co + _co) * delta / 2);
					_hs.add((hc + _hc) * delta / 2);
				}

				_nox = nox;
				_hc = hc;
				_co = co;

				_now = now;
			}

			pgn.dispose();
		}
	}

	public void reset() {
		_em.setValue(0);
		_ec.setValue(0);
		_es.setValue(0);
		_ot.setValue(0);
		_ut.setValue(0);
		_set.setValue(0);
		_let.setValue(0);
		_lec.setValue(0);

		_ns.setValue(0);
		_cs.setValue(0);
		_hs.setValue(0);
		_stopEngineonTimeOffset = _underrevvingTimeOffset = _overrevvingTimeOffset = System
				.currentTimeMillis();
	}

	public Map evaluate() {
		Map result = new HashMap();
		result.put(_em.getName(), new Measure(_em));
		result.put(_ec.getName(), new Measure(_ec));
		result.put(_es.getName(), new Measure(_es));
		result.put(_ot.getName(), new Measure(_ot));
		result.put(_ut.getName(), new Measure(_ut));
		result.put(_set.getName(), new Measure(_set));
		result.put(_let.getName(), new Measure(_let));
		result.put(_lec.getName(), new Measure(_lec));

		result.put(_ns.getName(), new Measure(_ns));
		result.put(_cs.getName(), new Measure(_cs));
		result.put(_hs.getName(), new Measure(_hs));

		return result;
	}

	public Map merge(Map measures) {
		_em.setValue(Math.max(((Measure) measures.get(ENGINESPEED_MAX))
				.getValue(), _em.getValue()));
		_ec.add(((Measure) measures.get(_ec.getName())));
		_es.add(((Measure) measures.get(_es.getName())));
		_ot.add(((Measure) measures.get(_ot.getName())));
		_ut.add(((Measure) measures.get(_ut.getName())));
		_set.add(((Measure) measures.get(_set.getName())));
		_let.add(((Measure) measures.get(_let.getName())));
		_lec.add(((Measure) measures.get(_lec.getName())));

		_ns.add(((Measure) measures.get(_ns.getName())));
		_cs.add(((Measure) measures.get(_cs.getName())));
		_hs.add(((Measure) measures.get(_hs.getName())));

		return evaluate();
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new PGN_F004Processor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
