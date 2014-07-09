package org.avm.business.hmi.web.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LongPollingWaiter extends ResponseWaiter {

	private long count = 0;

	public LongPollingWaiter() {

	}

	public LongPollingWaiter(int timeout) {
		super(timeout);
	}

	public void waitToDoResponse(HttpServletRequest req) {
		HttpSession session = req.getSession(true);
		if (!session.isNew()) {
			Long reqcount = (Long) session.getValue("count");
			if (reqcount.longValue() == count) {
				waitRequest();
			}
		}
		session.putValue("count", new Long(count));
	}

	public void unlockRequest() {
		count++;
		super.unlockRequest();
	}

}
