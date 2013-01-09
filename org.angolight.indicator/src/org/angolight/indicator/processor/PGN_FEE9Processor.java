package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.can.parser.fms.PGN_FEE9;
import org.avm.elementary.can.parser.fms.SPN250;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;

public class PGN_FEE9Processor extends Processor {

	private static String TARGET = PGN_FEE9.class.getName();

	// measure
	public static final String BEGIN_TOTAL_FUEL_USED = "begin-fuel-used";
	public static final String END_TOTAL_FUEL_USED = "end-fuel-used";

	private Measure _bfl, _efl;

	protected PGN_FEE9Processor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
		initialize();
	}

	private void initialize() {
		_bfl = new Measure(BEGIN_TOTAL_FUEL_USED, Unit.m3);
		_owner.add(_bfl);
		_efl = new Measure(END_TOTAL_FUEL_USED, Unit.m3);
		_owner.add(_efl);
		_bfl.setValue(0);
	}

	public void update(Object o) {
		if (o instanceof PGN_FEE9) {
			PGN_FEE9 pgn = (PGN_FEE9) o;

			
			double spn250 = pgn.spn250.getValue();

			// -- VMA if (pgnFEE9.spn250.isAvailable()) // Bug du
			// scalledUnAvailableValue qui est < 0

			//-- DLA il semble que la valeur 0x00FFFFFF soit une valeur invalide donc on ignore 8388607,5
			if ((pgn.spn250.isValid()) && (spn250 != 8388607.5)) {
				spn250 = spn250/1000d;// -- en m3
				if (_bfl.getValue() == 0) {
					_bfl.setValue(spn250);
					_efl.setValue(spn250);
				} else {
					_efl.setValue(spn250);
				}
			}

			pgn.dispose();
		}
	}

	public void reset() {
		_bfl.setValue(0);
		_efl.setValue(0);
	}

	public Map evaluate() {
		Map result = new HashMap();
		result.put(_bfl.getName(), new Measure(_bfl));
		result.put(_efl.getName(), new Measure(_efl));
		return result;
	}

	public Map merge(Map measures) {
		_bfl.add(((Measure) measures.get(_bfl.getName())));
		_efl.add(((Measure) measures.get(_efl.getName())));
		return evaluate();
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new PGN_FEE9Processor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
