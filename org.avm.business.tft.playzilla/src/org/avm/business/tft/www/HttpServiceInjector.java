package org.avm.business.tft.www;

import org.osgi.service.http.HttpService;

public interface HttpServiceInjector {

	void unsetHttpService(HttpService http);

	void setHttpService(HttpService http);
}
