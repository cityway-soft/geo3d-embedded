package org.avm.elementary.alarm.bundle;

import java.util.Dictionary;
import java.util.Properties;

public interface AlarmServiceConfig {

	public abstract void add(Properties p);

	public abstract void remove(String index);

	public abstract Properties get(String name);

	public abstract Dictionary get();

}