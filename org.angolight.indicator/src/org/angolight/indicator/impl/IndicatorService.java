package org.angolight.indicator.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.angolight.indicator.Indicator;
import org.angolight.indicator.Measure;
import org.angolight.indicator.processor.ProcessorFactory;
import org.apache.log4j.Logger;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ConsumerService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.common.ProducerService;
import org.avm.elementary.jdb.JDB;
import org.avm.elementary.jdb.JDBInjector;
import org.osgi.util.measurement.State;

public class IndicatorService implements Indicator, ConfigurableService,
		ConsumerService, ProducerService, ManageableService, JDBInjector {

	private IndicatorConfig _config;
	private ProducerManager _producer;
	private Logger _log;
	private Map _map;

	private JDB _jdb;

	public IndicatorService() {
		_log = Logger.getInstance(this.getClass());
		_map = new HashMap();
	}

	public void configure(Config config) {
		_config = (IndicatorConfig) config;
	}

	public void setProducer(ProducerManager producer) {
		_producer = producer;
	}

	public void setJdb(JDB jdb) {
		_jdb = jdb;
	}

	public void unsetJdb(JDB jdb) {
		_jdb = null;
	}

	public void start() {
		synchronized (_map) {
			ProcessorFactory.clear();
			_map.clear();
		}
	}

	public void stop() {
		if (_jdb != null) {
			_jdb.sync();
		}
	}

	public void notify(Object o) {
		try {
			synchronized (_map) {
				Processor processor = ProcessorFactory.makeObject(o, this,
						_config, _producer, _jdb);
				if (processor != null) {
					processor.update(o);
				}
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	public Map merge(Map measures) {
		Map result = new HashMap();
		synchronized (_map) {
			Collection list = ProcessorFactory._factories.values();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				ProcessorFactory factory = (ProcessorFactory) iterator.next();
				Processor processor = factory.getInstance();
				Map map = processor.merge(measures);
				result.putAll(map);
			}
		}
		return result;
	}

	public Map evaluate() {
		Map result = new HashMap();
		synchronized (_map) {
			Collection list = ProcessorFactory._factories.values();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				ProcessorFactory factory = (ProcessorFactory) iterator.next();
				Processor processor = factory.getInstance();
				Map map = processor.evaluate();
				result.putAll(map);
			}
		}
		return result;
	}

	public void reset() {
		synchronized (_map) {
			Collection list = ProcessorFactory._factories.values();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				ProcessorFactory factory = (ProcessorFactory) iterator.next();
				Processor processor = factory.getInstance();
				processor.reset();
			}
		}
	}

	public void add(Measure measure) {
		_map.put(measure.getName(), measure);
	}

	public Measure get(String name) {
		return (Measure) _map.get(name);
	}

	public void remove(Measure measure) {
		_map.remove(measure.getName());
	}

	public void publish(State state) {
		_producer.publish(state);
	}
}
