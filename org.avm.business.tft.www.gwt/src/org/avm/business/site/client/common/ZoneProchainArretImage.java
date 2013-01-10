package org.avm.business.site.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.htmlunit.corejs.javascript.ast.WhileLoop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class ZoneProchainArretImage extends AbstractZone implements
		RequestCallback {
	private final String NAME = "ZoneProchainArretImage"; //$NON-NLS-1$

	private int _dernierArretIdu = -1;

	private int _ligneIdu = -1;

	private String _lastPath = null;

	TFTMessages _messages;

	static int max = 0;

	static int cpt = 1;

	static final String IMAGE_TYPE_LIGNE = "L";
	static final String IMAGE_TYPE_STOP = "S";
	static final String IMAGE_TYPE_GENERAL = "G";

	private String[] filelist = null;

	private final Map<String, String> stops = new HashMap<String, String>();
	private final Map<String, List<String>> lignes = new HashMap<String, List<String>>();
	private final List<String> general = new ArrayList<String>();

	public ZoneProchainArretImage(final int delay) {
		super(delay);
		_messages = (TFTMessages) GWT.create(TFTMessages.class);
		initialize();
	}

	private void initialize() {
		try {
			final String remoteDir = GWT.getHostPageBaseURL()
					+ "data/photos/filelist";
			final RequestBuilder builder = new RequestBuilder(
					RequestBuilder.GET, remoteDir);
			// Window.alert("request: '" + remoteDir +"'");
			builder.setTimeoutMillis(2000);
			builder.sendRequest(null, this);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onError(final Request request, final Throwable arg1) {

	}

	@Override
	public void onResponseReceived(final Request request,
			final Response response) {
		if (response.getStatusCode() == 200) {
			final String list = response.getText();
			filelist = list.split("\n");
			if (filelist != null) {
				max = filelist.length;
			}
			for (int i = 0; i < filelist.length; ++i) {
				final String[] tmp = filelist[i].split(";");
				if (tmp[0].equals(IMAGE_TYPE_GENERAL)) {
					general.add(tmp[2]);
				} else if (tmp[0].equals(IMAGE_TYPE_LIGNE)) {
					List<String> li = lignes.get(tmp[1]);
					if (li == null) {
						li = new ArrayList<String>();
						lignes.put(tmp[1], li);
					}
					li.add(tmp[2]);
				} else if (tmp[0].equals(IMAGE_TYPE_STOP)) {
					stops.put(tmp[1], tmp[2]);
				}
			}
		}
	}

	private String getStopImagePath() {
		String uri = null;
		if (_dernierArretIdu != -1) {
			// uri = GWT.getHostPageBaseURL()
			//		+ "data/photos/stop" + _dernierArretIdu + ".jpg"; //$NON-NLS-1$ //$NON-NLS-2$
			final String img = stops.get(Integer.toString(_dernierArretIdu));
			if (img != null) {
				uri = GWT.getHostPageBaseURL() + "data/photos/" + img; //$NON-NLS-1$ 
			}
			return uri;
		}
		// Window.alert("Stop image : " + uri);
		return null;
	}

	private String getLigneRandomImagePath() {
		String uri = null;
		final List<String> imgs = lignes.get(Integer.toString(_ligneIdu));
		if (imgs != null) {
			final double r = Math.random();
			final long rand = (long) ((r * ((double) imgs.size())));
			cpt = (int) (((rand) % (long) imgs.size()));
			uri = GWT.getHostPageBaseURL() + "data/photos/" + imgs.get(cpt);
		}
		// Window.alert("Random Image : " + uri);
		return uri;
	}

	private String getRandomImagePath() {
		String uri = null;
		if (general.size() > 0) {
			final double r = Math.random();
			final long rand = (long) ((r * ((double) general.size())));
			cpt = (int) (((rand) % (long) general.size()));
			uri = GWT.getHostPageBaseURL() + "data/photos/" + general.get(cpt);
		}
		// Window.alert("Random Image : " + uri);
		return uri;
	}

//	private void removeAllimages() {
//		Element parent = DOM.getElementById("page2-droit-img");
//		if (parent != null) {
//			do {
//				Node node = parent.getChild(0);
//				parent.removeChild(node);
//			} while (parent.getChildCount() > 0);
//		}
//
//	}

	private void printImage() {
		String path = getStopImagePath();
		if (path == null) {
			path = getLigneRandomImagePath();
			if (path == null) {
				path = getRandomImagePath();
			}
		}
		if (_lastPath != null && path.equals(_lastPath)) {
			return;
		}
		
		Element imageElement = DOM.getElementById("page2-image");

		final Image image = Image.wrap(imageElement);
		//image.setVisible(false);
//		image.setAltText("");
//		image.addLoadHandler(new LoadHandler() {
//			@Override
//			public void onLoad(final LoadEvent event) {
//				image.setVisible(true);
////				DOM.setElementAttribute(image.getElement(), "style",
////						"display: visible;");
//			}
//		});

		
		//DOM.setElementAttribute(image.getElement(), "id", "page2-image"); //$NON-NLS-1$ //$NON-NLS-2$
		//DOM.setElementAttribute(image.getElement(), "alt", ""); //$NON-NLS-1$ //$NON-NLS-2$
		//DOM.setElementAttribute(image.getElement(), "style", "display: hidden;");
		image.setUrl(path);
		//RootPanel.get("page2-droit-img").add(image);
		_lastPath = path;
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

		_dernierArretIdu = model.getProchainArret().getPNT_IDU();
		_ligneIdu = model.getCourse().getLGN_IDU();
		if (delay <= 0) {
			Page2.setTitre(_messages.ProchainArret());
		} else {
			final String t = Integer.toString(delay) + " min.";
			final String message = _messages.ProchainArret()
					+ "<SPAN class='titre-principal-reduit'>"
					+ _messages.Dans() + t + "</SPAN>";
			Page2.setTitre(message);
		}
		Page2.setMessage(null, model.getProchainArret().getPNT_NOM());
		Page2.setTitreStyle("titre-principal-normal");
		Page2.setStyleHaut("prochain-arret");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void activate(final boolean b) {
		setVisible(Page2.NAME, b);
		if (b) {
			printImage();
		} 
	}
}
