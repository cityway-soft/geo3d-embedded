package org.apache.commons.pool.impl;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class ThreadPool extends GenericObjectPool {
	
	private Logger _log = Logger.getInstance(this.getClass());

	public ThreadPool(ThreadObjectFactory factory) {
		super(factory);
		this.setMaxIdle(2);
		this.setMinEvictableIdleTimeMillis(30000);
		//_log.setPriority(Priority.DEBUG);
	}

	public ThreadPool(ThreadObjectFactory factory,
			GenericObjectPool.Config config) {
		super(factory, config);
	}

	public Object borrowObject() throws Exception {
		Object o = super.borrowObject();
		_log.debug("[DSU] borrow object " + o);
		return o;
	}

	public void returnObject(Object o) throws Exception {
		_log.debug("[DSU] return object " + o);
		super.returnObject(o);
	}
}
