package org.avm.elementary.time;

public interface TimeManagerConfig {

	public static String TAG_FREQUENCY = "frequency";

	public static String TAG_CHECKPOS = "check-position";

	public abstract int getFrequency();

	public abstract void setFrequency(int value);

	public abstract boolean isPositionChecked();
	
	public abstract void setPositionCheck(boolean b);
}