package org.avm.business.tft;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.avm.elementary.common.Config;

public interface Tft {

	final static String SERVLET = "tft.do";

	final static String CSS = "tft.css";

	final static String ALIAS = "/tft";

	final static String ALIASCSS = "/css";

	final static String NAME = "/www";

	void refresh();

	void process(HttpServletRequest request, HttpServletResponse response)
			throws Throwable;

	Config getConfig();


}
