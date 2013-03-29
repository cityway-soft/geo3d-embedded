package org.avm.elementary.alarm;

import java.util.Collection;

public interface AlarmService {

	public boolean isAlarm();

	public Collection getList();
	
	public Alarm getAlarm(Integer id);
	
	public Alarm getAlarmByKey(String name);
}
