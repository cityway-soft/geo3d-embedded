package org.avm.business.site.client.common;

import com.google.gwt.user.client.ui.RootPanel;

public class Page2 extends Page {
	public static void setMessage(String titre, final String message) {
		if (titre == null) {
			titre = "";
		}
		setText("page2-info-titre-max", titre);
		setText("page2-info-message-max", message);
	}

	public static void setStyleHaut(final String style) {
		RootPanel.get("page2-gauche").setStyleName(style);
	}

	public static final String NAME = "page2";

}
