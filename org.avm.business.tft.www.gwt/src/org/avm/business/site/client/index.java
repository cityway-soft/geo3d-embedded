package org.avm.business.site.client;

import org.avm.business.site.client.common.ExchangeControler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class index implements EntryPoint/* , SpecificActions */{
	private ExchangeControler _exchangeCtrl;

	public static native String getParamString() /*-{
		return $wnd.location.search;
	}-*/;

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		GWT.log("onModuleLoad------", null);
		Window.enableScrolling(false);

		if (_exchangeCtrl == null) {
			final String urlparams = getParamString();
			_exchangeCtrl = new ExchangeControler(urlparams);
		}
	}

}
