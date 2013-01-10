package org.avm.business.site.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class Page {

	public static void setText(String div, String text) {
		Element element = RootPanel.get(div).getElement();
		
		if (element != null) {
			DOM.setInnerHTML(element, text);
		}
	}
	
	public static void setTitre(String titre){
		setText("titre-principal", titre); //$NON-NLS-1$
	}
	
	public static void setTitreStyle(String style){
		RootPanel.get("titre-principal").setStyleName(style); //$NON-NLS-1$
	}

	public static native void scrollText(String parentid, String id)/*-{
			$wnd.scroll(parentid, id);
		}-*/;

	public static native void cancelScrollText()/*-{
		$wnd.cancelScrolling();
	}-*/;

}
