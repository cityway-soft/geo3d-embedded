package org.avm.business.site.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class Debug {
	public static void setText(String div, String text) {
		Element element = RootPanel.get(div).getElement();
		if (element != null) {
			DOM.setInnerHTML(element, text);
		}
	}
}
