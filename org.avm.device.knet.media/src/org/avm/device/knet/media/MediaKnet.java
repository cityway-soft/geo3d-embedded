package org.avm.device.knet.media;

import java.util.Dictionary;

import org.avm.elementary.common.Media;
import org.avm.elementary.common.PublisherService;

public interface MediaKnet extends Media, PublisherService {

	// added methods for mediaKnet
	public void send(Dictionary header, byte[] data) throws Exception;
}
