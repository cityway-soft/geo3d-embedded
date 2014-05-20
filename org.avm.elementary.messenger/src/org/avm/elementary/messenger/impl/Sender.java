package org.avm.elementary.messenger.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.avm.elementary.common.Media;

import EDU.oswego.cs.dl.util.concurrent.BoundedLinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.Channel;

public class Sender implements Runnable {

	private static Sender _instance;

	protected Thread _thread;

	protected final Channel _queue;

	protected volatile boolean shutdown_;

	protected Sender() {
		this(new BoundedLinkedQueue(50));
	}

	protected Sender(Channel queue) {
		_queue = queue;
	}

	public static Sender getInstance() {
		if (_instance == null) {
			_instance = new Sender();
		}
		return _instance;
	}

	protected static Runnable ENDTASK = new Runnable() {
		public void run() {
		}
	};

	public synchronized Thread getThread() {
		return _thread;
	}

	protected synchronized void clearThread() {
		_thread = null;
	}

	public  synchronized void send(Hashtable medias, String mediaName, Dictionary header, byte[] data) {
		try {
			execute(new SendTask(medias, mediaName, header, data));
		} catch (InterruptedException e) {

		}
	}

	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	public void run() {
		try {
			while (!shutdown_) {
				try {

					Runnable task = (Runnable) (_queue.peek());
					if (task == ENDTASK) {
						shutdown_ = true;
						break;
					} else if (task != null) {
						task.run();
						task = (Runnable) (_queue.take());
					} else {
						sleep(100);
					}
				} catch (RuntimeException e) {
					sleep(3000);
				}
			}
		} catch (InterruptedException ex) {
		} finally {
			clearThread();
		}
	}

	public synchronized void restart() {
		if (_thread == null && !shutdown_) {
			_thread = new Thread(this);
			_thread.setName("[AVM] messenger");
			_thread.start();
			sleep(200);
		}
	}

	public void execute(Runnable command) throws InterruptedException {
		restart();
		if (!_queue.offer(command, 100)) {
			throw new IndexOutOfBoundsException();
		}
	}

	public synchronized void shutdownAfterProcessingCurrentlyQueuedTasks() {
		if (!shutdown_) {
			try {
				_queue.offer(ENDTASK, 100);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public synchronized void shutdownAfterProcessingCurrentTask() {
		shutdown_ = true;
		try {
			while (_queue.poll(0) != null)
				;
			_queue.put(ENDTASK);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void shutdownNow() {
		shutdown_ = true;
		Thread t = _thread;
		if (t != null)
			t.interrupt();
		shutdownAfterProcessingCurrentTask();
	}

	class SendTask implements Runnable {

		private Hashtable _medias;
		private String _mediaName;
		private Dictionary _header;
		private byte[] _data;

		public SendTask(Hashtable medias, String mediaName, Dictionary header, byte[] data) {
			_medias = medias;
			_mediaName = mediaName;
			_header = header;
			_data = data;
		}

		public void run() {
			try {
				Media media = (Media) _medias.get(_mediaName);
				media.send(_header, _data);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	};
}
