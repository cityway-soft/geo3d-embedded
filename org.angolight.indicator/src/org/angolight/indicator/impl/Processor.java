package org.angolight.indicator.impl;

import java.util.Map;

import org.avm.elementary.common.ProducerManager;
import org.avm.elementary.jdb.JDB;

public abstract class Processor {

	protected IndicatorService _owner;
	protected IndicatorConfig _config;
	protected ProducerManager _producer;
	protected JDB _jdb;

	protected Processor(IndicatorService owner, IndicatorConfig config,
			ProducerManager producer, JDB jdb) {
		_owner = owner;
		_config = config;
		_producer = producer;
		_jdb = jdb;
	}

	public abstract void reset();

	public abstract void update(Object o);

	public abstract Map evaluate();

	public abstract Map merge(Map measures);
}
