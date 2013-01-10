package org.avm.hmi.swt.avm;

import org.avm.business.core.Avm;

public interface FollowUp {
	public void updatePoint();

	public void updateCourse();

	public void dispose();

	public void setAvm(Avm avm);

	public void setHorsItineraire(boolean b);

}
