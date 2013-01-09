package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;

public class ZoneDeviation extends AbstractZone {
	private String NAME = "ZoneDeviation";
	private TFTMessages _messages;

	public ZoneDeviation(int delay) {
		super(delay);
		_messages = (TFTMessages) GWT.create(TFTMessages.class);
	}

	public boolean isPrintable(AVMModel model) {
		boolean result = (model.getDestination()!=null);
		return result;
	}
	
	public void setData(AVMModel model) {
		if (isPrintable(model) == false)return;
		Page1.setTitre(_messages.TitreDestination());
		Page1.setTitreStyle("titre-principal-normal");
		Page1.setMessageHaut(null, model.getDestination());
		Page1.setStyleHaut("destination");

		// Page1.setMessageHaut("", "En déviation");
		// Page1.setStyleHaut("information-perturbation");
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