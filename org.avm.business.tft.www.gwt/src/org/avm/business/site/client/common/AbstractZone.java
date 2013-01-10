package org.avm.business.site.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class AbstractZone extends Composite implements Zone {

	private int _delay;
	private boolean _isActivated = false;
	

	public AbstractZone(int delay) {
		_delay = delay;
	}

	public abstract void setData(AVMModel model);
	
	public abstract String getName();

	public boolean isPrintable(AVMModel model) {
		return true;
	}

//	public void activate(boolean b) {
//		enable(b);
//		_isActivated = b;
//	}

	public boolean isActivated() {
		return _isActivated;
	}

	public int getDelay() {
		return _delay;
	}

	public void setVisible(String id, boolean b) {
		_isActivated=b;
		DOM.setStyleAttribute(RootPanel.get(id).getElement(), "visibility",
				b ? "inherit" : "hidden");
	}

	public void setText(String div, String text) {
		Element element = RootPanel.get(div).getElement();
		if (element != null) {
			DOM.setInnerHTML(element, text);
		}
	}

	public String toString() {
		return "[" + (_isActivated?"*":"") + getName() +" (" + _delay + ")]";
	}

}
