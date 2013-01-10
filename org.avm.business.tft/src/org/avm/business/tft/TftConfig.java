package org.avm.business.tft;

import org.avm.elementary.common.Config;

public interface TftConfig extends Config {
	
	final static String FONTSIZE_TAG = "font-size";
	
	final static String FONTNAME_TAG = "font-name";
	
	void setFontName (String name);
	
	String getFontName ();
	
	void setFontSize (String size);
	
	String getFontSize ();
	
	

}