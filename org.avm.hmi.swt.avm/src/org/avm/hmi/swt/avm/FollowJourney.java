package org.avm.hmi.swt.avm;

import org.avm.business.core.Avm;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.hmi.swt.desktop.Desktop;
import org.eclipse.swt.widgets.Composite;


public class FollowJourney extends Composite {

	protected Desktop _desktop;
	protected Avm _avm;

	public FollowJourney(Composite arg0, int arg1) {
		super(arg0, arg1);
	}

	public void setDesktop(Desktop desktop){
		_desktop = desktop;
	}
	
	public void setAvm(Avm avm) {
		_avm = avm;
	}
	

	public void setGeorefRole(boolean georefRole) {
		
	}

	public void setService(ServiceAgent serviceAgent) {
		
	}

	public void setCourse(Course co) {

	}

	public void activatePanel() {
		
	}

	public void setPoint(Point point) {
		
	}

	public void setAvanceRetard(int ar) {
		
	}

	public void setHorsItineraire(boolean b) {
		
	}

	public void updateMessage() {
		// TODO Auto-generated method stub
		
	}


}