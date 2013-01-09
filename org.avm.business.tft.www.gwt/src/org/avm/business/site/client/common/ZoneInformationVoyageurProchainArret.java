package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;

public class ZoneInformationVoyageurProchainArret extends AbstractZone {
	private String NAME = "ZoneInformationVoyageurProchainArret"; //$NON-NLS-1$
	TFTMessages _messages;
	
	static int cpt = 0;

	static int lastCpt = -1;

	String[] _messagesInfoVoyageur = null;

	public ZoneInformationVoyageurProchainArret(int delay) {
		super(delay);
		_messages = (TFTMessages) GWT.create(TFTMessages.class);
	}

	public boolean isPrintable(AVMModel model) {
		boolean result = (_messagesInfoVoyageur != null && _messagesInfoVoyageur.length != 0 && model.getProchainArret() !=null );

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
		// ExchangeControler.debug("ZoneIV setdata ");
		String[] messages = model.getMessages();
		if (messages != _messagesInfoVoyageur) {
			// ExchangeControler.debug("ZoneIV messages changed! ");
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
			// ExchangeControler.debug("ZoneIV message: " + message);
			Page4.setMessageHaut(_messages.TitreInformation(), message); //$NON-NLS-1$
			Page4.setStyleHaut(getStyle()); //$NON-NLS-1$
		//Effect.blindDown(RootPanel.get("page4-info-haut"));
			lastCpt = cpt;
		}

//		int time = model.getTime();
//		int heureProchainArret = model.getProchainArret().getHOD_ARRIVEE() - 1; //
//
//		if (time > heureProchainArret) {
//			Page4.setMessageBas(_messages.ProchainArret(), model.getProchainArret() //$NON-NLS-1$
//					.getPNT_NOM());
//		} else {
////			String t = Util.formatHeure(model.getProchainArret()
////					.getHOD_ARRIVEE());
//			String t = Integer.toString((model.getProchainArret()
//					.getHOD_ARRIVEE()-time)/60) + " min.";
//			Page4.setMessageBas(_messages.ProchainArret()+_messages.Dans()
//					+ t, model.getProchainArret()
//					.getPNT_NOM());
//		}
//
//		Page4.setStyleBas("prochain-arret"); //$NON-NLS-1$

	}
	
	
	public String getName(){
		return NAME;
	}

	public void activate(boolean b) {
		setVisible(Page4.NAME, b);
		if (b) {
			// ExchangeControler.debug("ZoneIV activate");
			if (_messagesInfoVoyageur != null) {
				cpt+=2;
				cpt = cpt % _messagesInfoVoyageur.length;
				// ExchangeControler.debug("ZoneIV activate cpt=" + cpt);
			}
		}
	}
}