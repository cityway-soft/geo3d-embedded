package org.avm.elementary.common;

import java.util.Dictionary;

public interface MediaListener {
	public void receive(Dictionary header, byte[] data);
	public void setMedia(Media media);
}
