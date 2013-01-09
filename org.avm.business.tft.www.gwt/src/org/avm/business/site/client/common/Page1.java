package org.avm.business.site.client.common;

import com.google.gwt.user.client.ui.RootPanel;

public class Page1 extends Page {
	public static void setMessageHaut(String titre, String message) {
		if (titre == null){
			titre = "";
		}
		setText("page1-info-titre-haut", titre); //$NON-NLS-1$ //$NON-NLS-2$
		setText("page1-info-message-haut", "" + message); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setStyleHaut(String style) {
		RootPanel.get("page1-info-haut").setStyleName(style); //$NON-NLS-1$
	}

	public static final String NAME = "page1"; //$NON-NLS-1$
}
