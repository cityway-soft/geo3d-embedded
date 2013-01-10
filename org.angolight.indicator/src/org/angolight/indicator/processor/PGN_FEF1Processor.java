package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.can.parser.fms.PGN_FEF1;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;

public class PGN_FEF1Processor extends Processor {

	private static String TARGET = PGN_FEF1.class.getName();;

	// measure
	public static final String BRAKE_COUNTER = "brake-counter";
	public static final String BRAKE_TIME = "brake-time";

	private Measure _bc, _bt;

	private boolean _brakeState = true;
	private long _brakeTimeOffset;

	protected PGN_FEF1Processor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
		initialize();
	}

	private void initialize() {
		_bc = new Measure(BRAKE_COUNTER, Unit.unity);
		_owner.add(_bc);
		_bt = new Measure(BRAKE_TIME, Unit.s);
		_owner.add(_bt);
		_brakeTimeOffset = System.currentTimeMillis();
	}

	public void update(Object o) {
		// -- DLA (1) : comptabilise les coups de freins seulement si le
		// -- vehicule roule
		Measure stopStateMeasure = _owner.get(KineticProcessor.STOP_STATE); 
		boolean stopState = (stopStateMeasure == null) ? true
				: (stopStateMeasure.getValue() == 1); // --DLA (1)
		if (o instanceof PGN_FEF1) {
			PGN_FEF1 pgn = (PGN_FEF1) o;
			if (pgn.spn597.isValid()) {
				double spn597 = pgn.spn597.getValue();
				if (_brakeState) {
					if (spn597 == 0) {
						_brakeState = false;
						if (stopState == false) { // -- DLA (1)
							long delay = System.currentTimeMillis()
									- _brakeTimeOffset;
							if (delay > 0) {
								_bt.add(delay);
							}
						}
					}
				} else {
					if (spn597 == 1) {
						_brakeState = true;// -- DLA : à true, non ????
						if (stopState == false) { // -- DLA (1)
							_brakeTimeOffset = System.currentTimeMillis();
							_bc.add(1);
						}
					}
				}
			}

			FreeWheel freeWhell = FreeWheel.getInstance(_owner, _config,
					_producer, _jdb);
			freeWhell.update(pgn);

			pgn.dispose();
		}

	}

	public void reset() {
		_bc.setValue(0);
		_bt.setValue(0);
		_brakeTimeOffset = System.currentTimeMillis();
		// reset FreeWhell par F0003Processor
	}

	public Map evaluate() {
		Map result = new HashMap();
		result.put(_bc.getName(), new Measure(_bc));
		result.put(_bt.getName(), new Measure(_bt));
		// evaluate FreeWhell par F0003Processor
		return result;
	}

	public Map merge(Map measures) {
		_bc.add(((Measure) measures.get(_bc.getName())));
		_bt.add(((Measure) measures.get(_bt.getName())));
		// merge FreeWhell par F0003Processor
		return evaluate();
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new PGN_FEF1Processor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
