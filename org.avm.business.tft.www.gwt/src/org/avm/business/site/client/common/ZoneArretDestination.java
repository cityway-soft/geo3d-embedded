package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;


public class ZoneArretDestination extends AbstractZone {
	private String NAME = "ZoneArretDestination"; //$NON-NLS-1$
	TFTMessages _messages;

	public ZoneArretDestination(int delay) {
		super( delay);
		_messages = (TFTMessages) GWT.create(TFTMessages.class);
	}

	
	public boolean isPrintable(AVMModel model) {
		boolean result = (model.getArret() !=null && model.getDestination()!=null);
		return result;
	}
	
	public void setData(AVMModel model) {
		if (isPrintable(model)==false)return;
		Page1.setTitre(model.getProchainArret()==null?_messages.Terminus():_messages.TitreArret());
		Page1.setTitreStyle("titre-principal-normal");
		Page1.setMessageHaut(null, model.getArret() //$NON-NLS-1$
				.getPNT_NOM());
		Page1.setStyleHaut("arret"); //$NON-NLS-1$
	}
	
	public String getName(){
		return NAME;
	}


	public void activate(boolean b) {
		setVisible(Page1.NAME, b);
	}
}