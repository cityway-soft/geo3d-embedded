package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.indicator.Measure;
import org.angolight.indicator.Unit;
import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.can.parser.fms.PGN_FEF2;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;

public class PGN_FEF2Processor extends Processor {

	private static String TARGET = PGN_FEF2.class.getName();

	// measure
	public static final String CONSUMPTION_SUM = "consumption-sum";
	public static final String STOP_CONSUMPTION_SUM = "stop-consumption-sum";
	public static final String LONGSTOP_CONSUMPTION_SUM = "longstop-consumption-sum";

	private Measure _cs, _scs, _lcs;
	private Measure _stop_state, _longstop_engineon_state;
	
	// 3600=par sec, 1000=m3, 10=par 100ms
	private double M3_PER_100MS = 1d / (3600d * 1000d * 10d);

	private double _spn183;
	
	protected PGN_FEF2Processor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
		initialize();
	}

	private void initialize() {
		_cs = new Measure(CONSUMPTION_SUM, Unit.m3);
		_owner.add(_cs);
		_scs = new Measure(STOP_CONSUMPTION_SUM, Unit.m3);
		_owner.add(_scs);
		_lcs = new Measure(LONGSTOP_CONSUMPTION_SUM, Unit.m3);
		_owner.add(_lcs);

	}

	public void update(Object o) {
		_stop_state = _owner.get(KineticProcessor.STOP_STATE);
		_longstop_engineon_state = _owner
				.get(PGN_F004Processor.LONGSTOP_ENGINEON_STATE);
		if (o instanceof PGN_FEF2) {
			PGN_FEF2 pgn = (PGN_FEF2) o;
			double spn183 = pgn.spn183.getValue();
			double volume = ((spn183 + _spn183) / 2) * M3_PER_100MS; // volume en m3
			
			if (_stop_state != null && _stop_state.getValue() == 1)
				_scs.add(volume);
			if (_longstop_engineon_state != null && _longstop_engineon_state.getValue() == 1)
				_lcs.add(volume);
			_cs.add(volume);
			_spn183 = spn183;
			pgn.dispose();
		}
	}

	public void reset() {
		_cs.setValue(0);
		_scs.setValue(0);
		_lcs.setValue(0);
	}

	public Map evaluate() {
		Map result = new HashMap();
		result.put(_cs.getName(), new Measure(_cs));
		result.put(_scs.getName(), new Measure(_scs));
		result.put(_lcs.getName(), new Measure(_lcs));
		return result;
	}

	public Map merge(Map measures) {
		_cs.add(((Measure) measures.get(_cs.getName())));
		_scs.add(((Measure) measures.get(_scs.getName())));
		_lcs.add(((Measure) measures.get(_lcs.getName())));
		return evaluate();
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new PGN_FEF2Processor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
