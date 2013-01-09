package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;

public class ZoneInformationVoyageurArret extends AbstractZone {
	private String NAME = "ZoneInformationVoyageur";

	static int cpt = 0;

	static int lastCpt = -1;

	String[] _messagesInfoVoyageur = null;

	TFTMessages _messages;

	public ZoneInformationVoyageurArret(int delay) {
		super(delay);
		_messages = (TFTMessages) GWT.create(TFTMessages.class);
	}

	public boolean isPrintable(AVMModel model) {
		boolean result = (_messagesInfoVoyageur != null
				&& _messagesInfoVoyageur.length != 0 && model
				.getProchainArret() != null);

		return result;
	}

	private String getMessage() {
		String msg = _messagesInfoVoyageur[cpt];
		return msg;
	}

	private String getStyle() {
		String s = _messagesInfoVoyageur[cpt + 1];
		String result = "information-generale";
		if (s.equals("1")) {
			result = "information-avertissement";
		} else if (s.equals("2")) {
			result = "information-perturbation";
		}
		return result;
	}

	public void setData(AVMModel model) {
		ExchangeControler.debug("ZoneIV setdata ");
		String[] messages = model.getMessages();
		if (messages != _messagesInfoVoyageur) {
			ExchangeControler.debug("ZoneIV messages changed! ");
			_messagesInfoVoyageur = messages;
			lastCpt = -1;
		}

		print(model);

	}

	private void print(AVMModel model) {
		if (isPrintable(model) == false)
			return;
		// ExchangeControler.debug("ZoneIV printable ");
		if (cpt != lastCpt) {
			String message = getMessage();
			ExchangeControler.debug("ZoneIV message: " + message);
			Page4.setMessageHaut("Information", message);
			Page4.setStyleHaut(getStyle());
			// Effect.blindDown(RootPanel.get("page4-info-haut"));
			lastCpt = cpt;
		}

//		Page4.setMessageBas(_messages.TitreArret(), model.getArret()
//				.getPNT_NOM());
//		Page4.setStyleBas("arret");

	}

	public String getName() {
		return NAME;
	}

	public void activate(boolean b) {
		setVisible(Page4.NAME, b);
		if (b) {
			if (_messagesInfoVoyageur != null) {
				cpt += 2;
				cpt = cpt % _messagesInfoVoyageur.length;
			}
		}
	}
}