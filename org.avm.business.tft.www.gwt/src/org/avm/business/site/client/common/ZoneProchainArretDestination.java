package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;

public class ZoneProchainArretDestination extends AbstractZone {
	private final String NAME = "ZoneProchainArretDestination";

	TFTMessages _messages;

	public ZoneProchainArretDestination(final int delay) {
		super(delay);
		_messages = (TFTMessages) GWT.create(TFTMessages.class);
	}

	@Override
	public boolean isPrintable(final AVMModel model) {
		final boolean result = (model.getProchainArret() != null);
		return result;
	}

	@Override
	public void setData(final AVMModel model) {
		if (isPrintable(model) == false) {
			return;
		}
		final int time = model.getTime();
		final int heureProchainArret = model.getProchainArret()
				.getHOD_ARRIVEE() - 1; //

		final int delay = (heureProchainArret - time) / 60;

		if (delay <= 0) {
			Page1.setTitre(_messages.ProchainArret());

			Page1.setMessageHaut(null, model.getProchainArret().getPNT_NOM());
		} else {
			final String t = Integer.toString(delay) + " min.";
			final String message = _messages.ProchainArret()
					+ "<SPAN class='titre-principal-reduit'>"
					+ _messages.Dans() + t + "</SPAN>";
			Page1.setTitre(message);
			Page1.setMessageHaut(null, model.getProchainArret().getPNT_NOM());
		}
		Page1.setTitreStyle("titre-principal-normal");
		Page1.setStyleHaut("prochain-arret");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void activate(final boolean b) {
		setVisible(Page1.NAME, b);
	}
}
