package org.avm.hmi.swt.avm;

public interface AvmIhmConfig {
	public static final String PERIODE = "periode";

	void setPeriode(int freq);
	int getPeriode();
}
