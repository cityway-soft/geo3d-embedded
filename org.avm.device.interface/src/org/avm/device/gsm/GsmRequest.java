package org.avm.device.gsm;

import java.util.Collection;
import java.util.Iterator;

public class GsmRequest implements Comparable {

	public static int DEFAUT_PRIORITY = 0;

	public static int DEFAUT_TIMEOUT = 3000;

	public static int MIN_PRIORITY = Integer.MIN_VALUE;

	public static int MAX_PRIORITY = Integer.MAX_VALUE;

	public byte[] command;

	public String goodMatch;

	public String[] badMatchs;

	public int timeout;
	
	public int priority;

	public GsmResponse result;

	protected GsmRequest[] requests = new GsmRequest[0];

	private volatile boolean _canceled = false;

	public GsmRequest(String command) {
		this(command, Constant.OK, Constant.ERROR, DEFAUT_TIMEOUT,
				DEFAUT_PRIORITY);
	}

	public GsmRequest(String command, String goodMatch, String[] badMatchs,
			int timeout) {
		this(command, goodMatch, badMatchs, timeout, DEFAUT_PRIORITY);
	}

	public GsmRequest(String command, String goodMatch, String[] badMatchs,
			int timeout, int priority) {

		this.command = command.getBytes();
		this.goodMatch = goodMatch;
		this.badMatchs = badMatchs;
		this.timeout = timeout;
		this.priority = priority;

		add(this);
	}

	public GsmRequest(byte[] command, String goodMatch, String[] badMatchs,
			int timeout, int priority) {

		this.command = command;
		this.goodMatch = goodMatch;
		this.badMatchs = badMatchs;
		this.timeout = timeout;
		this.priority = priority;

		add(this);
	}

	public GsmRequest(GsmRequest request) {
		this.command = request.command;
		this.goodMatch = request.goodMatch;
		this.badMatchs = request.badMatchs;
		this.timeout = request.timeout;
		this.priority = request.priority;

		add(this);
	}

	public GsmRequest(GsmRequest[] requests) {
		if (requests == null || requests.length <= 0) {
			throw new IllegalArgumentException();
		}
		this.command = requests[0].command;
		this.goodMatch = requests[0].goodMatch;
		this.badMatchs = requests[0].badMatchs;
		this.timeout = requests[0].timeout;
		this.priority = requests[0].priority;

		for (int i = 0; i < requests.length; i++) {
			add(requests[i]);
		}
	}

	public GsmRequest(Collection requests) {
		if (requests == null || requests.size() <= 0) {
			throw new IllegalArgumentException();
		}

		Iterator elements = requests.iterator();

		GsmRequest request = (GsmRequest) elements.next();
		this.command = request.command;
		this.goodMatch = request.goodMatch;
		this.badMatchs = request.badMatchs;
		this.timeout = request.timeout;
		this.priority = request.priority;
		add(this);

		while (elements.hasNext()) {
			add((GsmRequest) elements.next());
		}
	}

	public void add(GsmRequest request) {

		if (request == null) {
			throw new IllegalArgumentException();
		}

		GsmRequest[] results = new GsmRequest[requests.length + 1];
		System.arraycopy(requests, 0, results, 0, requests.length);
		results[requests.length] = request;
		requests = results;

	}

	public GsmRequest[] getRequests() {
		return (requests);
	}

	public int compareTo(Object o) {
		GsmRequest resquest = (GsmRequest) o;
		return this.priority - resquest.priority;
	}

	public synchronized void cancel() throws InterruptedException {
		_canceled = true;
		for (int i = 1; i < requests.length; i++) {
			GsmRequest command = requests[i];
			command._canceled = true;
		}
	}

	public boolean isCanceled() {
		return _canceled;
	}

	public String toString() {
		return new String(command).trim() + " => "
				+ ((result != null) ? result.toString().trim() : "");
	}

}
