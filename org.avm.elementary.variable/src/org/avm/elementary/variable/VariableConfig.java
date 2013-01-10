package org.avm.elementary.variable;

import java.util.Dictionary;
import java.util.Properties;

public interface VariableConfig {

	void add(Properties properties);

	void remove(String name);

	public Properties get(String name);

	public Dictionary get();

}
