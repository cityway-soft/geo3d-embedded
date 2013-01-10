package org.avm.elementary.common;

import java.util.Dictionary;

public interface Media {

	public String getMediaId();

	public void send(Dictionary header, byte[] data) throws Exception;
}
