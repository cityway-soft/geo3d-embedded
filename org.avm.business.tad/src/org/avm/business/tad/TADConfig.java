package org.avm.business.tad;

import java.util.Enumeration;

public interface TADConfig {

	public void add(Mission mission);

	public void remove(Long id);

	public Mission getMission(Long id);
	
	public Enumeration elements();
}
