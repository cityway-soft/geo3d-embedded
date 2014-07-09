package org.avm.business.hmi.web.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseWaiter {

	private static final int DEFAULT_TIMEOUT = 10000;
	private Object lock = new Object();
	private int timeout = DEFAULT_TIMEOUT;

	public ResponseWaiter(int timeout) {
		this.timeout = timeout;
	}

	public ResponseWaiter() {

	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 
	 * @return true if unblock, false on timeout
	 */
	public boolean waitRequest() {
		synchronized (lock) {
			try {
				lock.wait(timeout);
			} catch (InterruptedException e) {
				return true;
			}
		}
		return false;
	}

	public void unlockRequest() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}

}
