package org.avm.hmi.swt.ecall;

import org.avm.business.ecall.EcallService;

public interface Ecall {
	public void setEcallService(EcallService ecallService);

	public void etatInitial();

	public void etatAttentePriseEnCharge();

	public void etatEcouteDiscrete();
}
