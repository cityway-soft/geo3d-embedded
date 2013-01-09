package org.avm.business.core;

import org.avm.business.core.event.Course;
import org.avm.business.core.event.ServiceAgent;

public interface AvmDatasource  {
	
	public void setCheckValidite(boolean check);
	
	public int getVersion();
	
	public String getValiditePeriode();
	
	public String getValiditePropriete();

	public ServiceAgent getServiceAgent(int sag_idu) throws AvmDatabaseException;

	public Course getCourse(ServiceAgent sa, int courseIDU) throws AvmDatabaseException;

}
