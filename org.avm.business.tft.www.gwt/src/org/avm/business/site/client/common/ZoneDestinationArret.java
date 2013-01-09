package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;

public class ZoneDestinationArret extends AbstractZone {
	private String NAME = "ZoneDestinationArret";
	TFTMessages _messages;

	public ZoneDestinationArret(int delay) {
		super(delay);
		_messages = (TFTMessages) GWT.create(TFTMessages.class);
	}

	
	public boolean isPrintable(AVMModel model) {
		boolean result = (model.getArret() !=null && model.getDestination()!=null && model.getProchainArret()!=null);
		return result;
	}
	
	
	public void setData(AVMModel model) {
		if (isPrintable(model) == false)return;
		Page1.setTitre(_messages.TitreDestination());
		Page1.setTitreStyle("titre-principal-normal");
		Page1.setMessageHaut(null, model.getDestination());
		Page1.setStyleHaut("destination");
//		Page1.setMessageBas(_messages.TitreALArret(), model.getArret()
//				.getPNT_NOM());
//		Page1.setStyleBas("arret");
	}
	
	
	public String getName(){
		return NAME;
	}
	

	public void activate(boolean b) {
		setVisible(Page1.NAME, b);
	}
}