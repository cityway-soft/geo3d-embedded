package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.can.parser.fms.PGN_F005;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;

public class PGN_F005Processor extends Processor {

	private static String TARGET = PGN_F005.class.getName();

	// config
	private static final int GEAR_COUNT = 6;

	// measure
	public static final String GEAR_TIME = "gear-time";
	public static final String GEAR_COUNTER = "gear-counter";

	private Measure[] _gt;
	private Measure _gc;

	private long _gearTimeOffset;
	private double _spn523 = -125;

	protected PGN_F005Processor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
		initialize();
	}

	private void initialize() {
		_gt = new Measure[GEAR_COUNT + 1];
		for (int i = 0; i < _gt.length; i++) {
			_gt[i] = new Measure(GEAR_TIME + "-" + i, Unit.s);
			_owner.add(_gt[i]);
		}
		_gc = new Measure(GEAR_COUNTER, Unit.unity);
		_owner.add(_gc);
		_gearTimeOffset = System.currentTimeMillis();
	}

	public void update(Object o) {
		if (o instanceof PGN_F005) {
			PGN_F005 pgn = (PGN_F005) o;
			if (pgn.spn523.isValid()) {
				double spn523 = pgn.spn523.getValue();
				if ((spn523 >= 0) && (spn523 <= GEAR_COUNT)
						&& (spn523 != _spn523)) {
					long now = System.currentTimeMillis();

					if ((_spn523 >= 0) && (_spn523 <= GEAR_COUNT)) {
						Measure measure = _gt[(int) _spn523];
						measure.add(now - _gearTimeOffset);
					}
					_spn523 = spn523;
					_gearTimeOffset = now;
					_gc.add(1);
				}
			}
			pgn.dispose();
		}
	}

	public void reset() {
		for (int i = 0; i < _gt.length; i++) {
			_gt[i].setValue(0);
		}
		_gc.setValue(0);
		_gearTimeOffset = System.currentTimeMillis();
	}

	public Map evaluate() {
		Map result = new HashMap();
		for (int i = 0; i < _gt.length; i++) {
			result.put(_gt[i].getName(), new Measure(_gt[i]));
		}
		result.put(_gc.getName(), new Measure(_gc));
		return result;
	}

	public Map merge(Map measures) {
		for (int i = 0; i < _gt.length; i++) {
			_gt[i].add(((Measure) measures.get(_gt[i].getName())));
		}
		_gc.add(((Measure) measures.get(_gc.getName())));
		return evaluate();
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new PGN_F005Processor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}