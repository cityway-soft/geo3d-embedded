package org.avm.elementary.management.core;

import org.osgi.service.startlevel.StartLevel;


/**
 * @author Didier LALLEMAND
 * 
 */
public interface ManagementService {

	public void synchronize();
		
	public void init();

	public String getPrivateUploadUrl();

	public StartLevel getStartLevelService();


}
