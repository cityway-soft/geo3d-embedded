package org.angolight.indicator;

import java.util.Map;

public interface Indicator {
	
	public static final String CATEGORY = "org.angolight.indicator";

	public Map merge(Map measures);

	public Map evaluate();

	public void reset();

}
