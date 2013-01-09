package org.avm.business.site.client.common;

public interface Zone {

	void setData(AVMModel model);

	int getDelay();

	void activate(boolean b);

	boolean isPrintable(AVMModel model);

}
