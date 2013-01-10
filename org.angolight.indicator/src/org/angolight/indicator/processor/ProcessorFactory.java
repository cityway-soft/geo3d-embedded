package org.angolight.indicator.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.angolight.indicator.impl.IndicatorConfig;
import org.angolight.indicator.impl.IndicatorService;
import org.angolight.indicator.impl.Processor;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;
import org.osgi.util.measurement.State;

public abstract class ProcessorFactory {

	public static Map _factories = new HashMap();

	protected Processor _instance;

	protected abstract Processor makeObject(IndicatorService owner,
			IndicatorConfig config, ProducerManager producer, JDB jdb)
			throws Exception;

	public static void clear() {
		for (Iterator iterator = _factories.values().iterator(); iterator
				.hasNext();) {
			ProcessorFactory factory = (ProcessorFactory) iterator.next();
			factory._instance = null;
		}
	}

	public static Processor makeObject(Object o, IndicatorService owner,
			IndicatorConfig config, ProducerManager producer, JDB jdb)
			throws Exception {
		String key = null;
		if (o instanceof State) {
			key = ((State) o).getName();
		} else {
			key = o.getClass().getName();
		}

		ProcessorFactory factory = ((ProcessorFactory) _factories.get(key));
		if (factory == null) {
			String name = ProcessorFactory.class.getPackage().getName()
					+ key.substring(key.lastIndexOf('.')) + "Processor";
			Class.forName(name);
			factory = ((ProcessorFactory) _factories.get(key));
		}

		if (factory._instance == null) {
			factory._instance = factory
					.makeObject(owner, config, producer, jdb);
		}
		return factory._instance;
	}

	public Processor getInstance() {
		return _instance;
	}
}
