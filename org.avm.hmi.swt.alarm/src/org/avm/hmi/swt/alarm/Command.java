package org.avm.hmi.swt.alarm;

import org.avm.elementary.alarm.Alarm;
import org.osgi.service.component.ComponentContext;

public interface Command{

	public static final String NOTIFY = "notify";
	public static final String SOURCE = "source";


	public Alarm getAlarm();

	public void activate(boolean b);
	

	public void setContext(ComponentContext context);
}
