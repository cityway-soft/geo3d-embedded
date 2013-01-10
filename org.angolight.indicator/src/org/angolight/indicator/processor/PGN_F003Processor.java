package org.angolight.indicator.processor;

import java.util.Map;

import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.can.parser.fms.PGN_F003;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;

public class PGN_F003Processor extends Processor {

	private static String TARGET = PGN_F003.class.getName();

	protected PGN_F003Processor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
	}

	public void update(Object o) {
		if (o instanceof PGN_F003) {
			PGN_F003 pgn = (PGN_F003) o;
			FreeWheel freeWhell = FreeWheel.getInstance(_owner, _config,
					_producer, _jdb);
			freeWhell.update(pgn);
			pgn.dispose();
		}
	}

	public void reset() {
		FreeWheel freeWhell = FreeWheel.getInstance(_owner, _config, _producer,
				_jdb);
		freeWhell.reset();
	}

	public Map evaluate() {
		FreeWheel freeWhell = FreeWheel.getInstance(_owner, _config, _producer,
				_jdb);
		return freeWhell.evaluate();
	}

	public Map merge(Map measures) {
		FreeWheel freeWhell = FreeWheel.getInstance(_owner, _config, _producer,
				_jdb);
		return freeWhell.merge(measures);
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new PGN_F003Processor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
