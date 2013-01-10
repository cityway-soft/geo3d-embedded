package org.avm.elementary.common;

import org.apache.commons.pool.impl.ThreadObjectFactory;
import org.apache.commons.pool.impl.ThreadPool;
import org.apache.commons.pool.impl.WorkerThread;

import EDU.oswego.cs.dl.util.concurrent.BoundedLinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Channel;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactoryUser;

public class PooledQueuedExecutor extends ThreadFactoryUser implements
		EDU.oswego.cs.dl.util.concurrent.Executor {

	protected volatile boolean _shutdown = true;

	protected static Runnable ENDTASK = new Runnable() {
		public void run() {
		}
	};
	
	private static ThreadPool _pool = new ThreadPool(new ThreadObjectFactory());;

	protected final Channel _queue;

	protected final RunLoop _task;

	private WorkerThread _worker;

	public PooledQueuedExecutor() {
		this(new BoundedLinkedQueue());
	}

	public PooledQueuedExecutor(Channel queue) {
		_queue = queue;
		_task = new RunLoop();
	}

	public void execute(Runnable command) throws InterruptedException {
		_queue.put(command);
		if (_shutdown) {
			_shutdown = false;
			try {
				_worker = (WorkerThread) _pool.borrowObject();
				_worker.execute(_task);
			} catch (Exception e) {

			}
		}
	}

	protected class RunLoop implements Runnable {
		public void run() {
			try {
				while (!_shutdown) {
					Runnable task = (Runnable) (_queue.poll(1000));
					if (task == ENDTASK) {
						_shutdown = true;
						break;
					} else if (task != null) {
						task.run();
						task = null;
					} else {
						_shutdown = true;
						break;
					}
				}
			} catch (InterruptedException ex) {
			} finally {
				_shutdown = true;
				try {
					if (_worker != null) {
						_pool.returnObject(_worker);
						_worker = null;
					}
				} catch (Exception e) {
				}
			}
		}
	}

}
