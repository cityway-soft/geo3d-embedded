package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class ZoneImageLigne extends AbstractZone {
	private String NAME = "ZoneImageLigne";

	private int _lgn_idu = -1;

	public ZoneImageLigne(int delay) {
		super(delay);
	}

	private void printImage(AVMModel model) {
		String path = GWT.getHostPageBaseURL() + "data/lignes/ligne"
				+ model.getCourse().getLGN_IDU() + ".png";

		final Image image = new Image();
		// Effect.fade(image);

		image.setVisible(false);
		DOM.setElementAttribute(image.getElement(), "id", "img-ligne");
		DOM.setElementAttribute(image.getElement(), "alt", "L "
				+ model.getCourse().getLGN_NOM());

		
	    image.addErrorHandler(new ErrorHandler() {
	        public void onError(ErrorEvent event) {
	        	String path = GWT.getHostPageBaseURL()
						+ "ressources/empty.jpg";
				image.setUrl(path);
	        }
	      });
	    
	    image.addLoadHandler(new LoadHandler() {
	  			@Override
			public void onLoad(LoadEvent event) {
	  				image.setVisible(true);
					// Effect.appear(image);
					Element parent = DOM.getElementById("logo-ligne");
					Element child = DOM.getElementById("img-ligne");
					if (child != null) {
						DOM.removeChild(parent, child);// DOM.getChild(parent, 0));
					}
			}
	      });
	    
	    /*
		// DOM.setInnerHTML(RootPanel.get("page2-droit-img").getElement(), "");
		image.addLoadListener(new LoadListener() {
			public void onError(Widget sender) {
				String path = GWT.getHostPageBaseURL()
						+ "ressources/empty.jpg";
				image.setUrl(path);
			}

			public void onLoad(Widget sender) {
				image.setVisible(true);
				// Effect.appear(image);
				Element parent = DOM.getElementById("logo-ligne");
				Element child = DOM.getElementById("img-ligne");
				if (child != null) {
					DOM.removeChild(parent, child);// DOM.getChild(parent, 0));
				}
			}
		});*/
		image.setUrl(path);
		RootPanel.get("logo-ligne").add(image);
//		image.setWidth("100%");

		// Effect.appear(image);
	}

	public void setData(AVMModel model) {
		if (model.getCourse() != null) {
			int lgn_idu = model.getCourse().getLGN_IDU();
			if (lgn_idu != _lgn_idu) {
				printImage(model);
				_lgn_idu = lgn_idu;
			}
		}
	}
	
	
	public String getName(){
		return NAME;
	}
	

	public void activate(boolean b) {
//		setVisible("logo-ligne", b);
	}
}
