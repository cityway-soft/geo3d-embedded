package org.avm.business.site.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class Page4 extends Page {
	public static void setMessageHaut(String titre, String message) {
		setText("page4-info-titre-haut", "");
		
		
		//double size = 120-(160/200.0)*message.length();
		double size = (2000.0/(message.length()+1))+20.0;
		if (size < 20) size = 20;
		String fontSize = ((int)size)+"%";
//		String fontSize="30%";
//		if (message.length()<20){
//			fontSize="120%";
//		}
//		else if (message.length()<35){
//			fontSize="100%";
//		}
//		else if (message.length()<80){
//			fontSize="80%";
//		}
//		else if (message.length()<120){
//			fontSize="60%";
//		}
//		else if (message.length()<200){
//			fontSize="50%";
//		}
		
		Element element = RootPanel.get("page4-info-message-haut").getElement();
		
		if (element != null) {
			String tag_fontsize="fontSize";
			DOM.setStyleAttribute(element, tag_fontsize, fontSize);
		}
		setText("titre-principal", titre);//+" "+fontSize);
		setText("page4-info-message-haut", message);
	}

	public static void setStyleHaut(String style) {
		RootPanel.get("page4-info-haut").setStyleName(style);
	}

//	public static void setStyleBas(String style) {
//		RootPanel.get("page4-info-bas").setStyleName(style);
//	}
//
//	public static void setMessageBas(String titre, String message) {
//		setText("page4-info-titre-bas", titre);
//		setText("page4-info-message-bas", message);
//	}
	
	public static final String NAME = "page4";

}
