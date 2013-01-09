package org.avm.elementary.variable;

import org.osgi.util.measurement.Measurement;

public interface VariableService {

	public Measurement read(String name);

	public void write(String name, Measurement value);
}
