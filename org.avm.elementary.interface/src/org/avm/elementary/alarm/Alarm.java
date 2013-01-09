package org.avm.elementary.alarm;

import java.util.Date;

public class Alarm {
	public static final String INDEX = "org.avm.elementary.alarm.index";
	public static final String SOURCE = "org.avm.elementary.alarm.source";
	public static final String NOTIFY = "org.avm.elementary.alarm.notify";
	public static final String ACKNOWLEDGE = "org.avm.elementary.alarm.acknowledge";
	public static int MIN_PRIORITY = 0;
	public static int NORM_PRIORITY = 4;
	public static int MAX_PRIORITY = 9;
	public boolean status;
	public String description;
	public Date date;
	public String source;
	public int priority;

	public Alarm(boolean status, String description, Date date, String source,
			int priority) {
		super();
		this.status = status;
		this.description = description;
		this.date = date;
		this.source = source;
		this.priority = priority;
	}

	public String toString() {
		return "status: " + status + " description: " + description + " date: "
				+ date + " source: " + source + " priority: " + priority;
	}
}
