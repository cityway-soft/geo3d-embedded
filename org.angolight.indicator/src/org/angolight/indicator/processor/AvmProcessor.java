package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Map;

import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.measurement.State;

public class AvmProcessor extends Processor {

	private static String TARGET = "org.avm.business.core.Avm";

	// config

	// measure

	protected AvmProcessor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		super(owner, config, producer, jdb);
		initialize();
	}

	private void initialize() {

	}

	public void update(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			if (state.getName().equals(TARGET)) {
			}

		}
	}

	public void reset() {

	}

	public Map evaluate() {
		Map result = new HashMap();

		return result;
	}

	public Map merge(Map measures) {

		return evaluate();
	}

	// abstract factory
	public static class DefaultProcessorFactory extends ProcessorFactory {
		protected Processor makeObject(IndicatorService owner,
				IndicatorConfig config, ProducerManager producer, JDB jdb)
				throws Exception {
			return new AvmProcessor(owner, config, producer, jdb);
		}
	}

	static {
		ProcessorFactory._factories.put(TARGET, new DefaultProcessorFactory());
	}
}
