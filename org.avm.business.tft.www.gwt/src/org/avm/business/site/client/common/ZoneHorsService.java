package org.avm.business.site.client.common;

public class ZoneHorsService extends AbstractZone {
	private String NAME = "ZoneHorsService";

	public ZoneHorsService( int delay) {
		super(delay);
	}

	public void setData(AVMModel model) {
		Page1.setMessageHaut(null, "En déviation");
		Page1.setStyleHaut("information-perturbation");
//		Page1.setMessageBas("", "");
//		Page1.setStyleBas("vide");
	}
	
	
	public String getName(){
		return NAME;
	}
	

	public void activate(boolean b) {
		setVisible(Page1.NAME, b);
	}
}