package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;

public class ZoneDestination extends AbstractZone {
	private String NAME = "ZoneDestination";
	private TFTMessages _messages;

	public ZoneDestination(int delay) {
		super(delay);
		_messages = (TFTMessages) GWT.create(TFTMessages.class);
	}

	public void setData(AVMModel model) {
		Page1.setTitre(_messages.TitreDestination());
		Page1.setTitreStyle("titre-principal-normal");
		Page1.setMessageHaut(null, model.getDestination());
		Page1.setStyleHaut("destination");

//		int time = model.getTime();
//		int depart = model.getCourse().getCRS_DEPART() - 1; //
//
//		if (time > depart) {
//			Page1.setMessageBas("Départ ", "imminent...");
//			Page1.setStyleBas("normal");
//		} else {
//			Page1.setMessageBas("Départ à ", Util.formatHeure(depart));
//			Page1.setStyleBas("normal");
//		}
	}
	
	
	public String getName(){
		return NAME;
	}
	

	public void activate(boolean b) {
		setVisible(Page1.NAME, b);
	}

}