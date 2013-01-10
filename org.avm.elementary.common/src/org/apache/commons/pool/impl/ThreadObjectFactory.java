package org.apache.commons.pool.impl;

import org.apache.commons.pool.PoolableObjectFactory;

public class ThreadObjectFactory implements PoolableObjectFactory {

	private int _count = 0;

	public Object makeObject() {
		Thread thread = new WorkerThread("[AVM] pooled executor : " + _count++);
		thread.setDaemon(true);
		return thread;
	}

	public void destroyObject(Object o) {
		if (o instanceof WorkerThread) {
			WorkerThread worker = (WorkerThread) o;
			worker.setStopped(true);
		}
	}

	public boolean validateObject(Object o) {
		if (o instanceof WorkerThread) {
			WorkerThread worker = (WorkerThread) o;
			if (worker.isRunning()) {
				if (worker.getThreadGroup() == null) {
					return false;
				}
				return true;
			}
		}
		return true;
	}

	public void activateObject(Object obj) {
	}

	public void passivateObject(Object o) {
		if (o instanceof WorkerThread) {
			WorkerThread worker = (WorkerThread) o;
		}
	}
}
