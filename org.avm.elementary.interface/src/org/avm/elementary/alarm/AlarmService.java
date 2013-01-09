package org.avm.elementary.alarm;

import java.util.BitSet;

public interface AlarmService {
	public BitSet getAlarms();

	public long getCounter();
}
