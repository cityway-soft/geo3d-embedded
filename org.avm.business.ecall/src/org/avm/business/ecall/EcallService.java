package org.avm.business.ecall;

import org.avm.elementary.alarm.AlarmProvider;
import org.osgi.util.measurement.State;

public interface EcallService extends AlarmProvider {

	public boolean endEcall();

	public boolean startEcall();

	public boolean ack(String phone);

	public State getState();
}
