package org.avm.elementary.command;

import java.util.Dictionary;
import java.util.Map;

public class MessengerContext extends CommandChainContext {

	public static final String HEADER_KEY = "header";

	public static final String MESSAGE_KEY = "message";

	public static final String MEDIA_KEY = "media";

	public Dictionary getHeader() {
		return (Dictionary) get(HEADER_KEY);
	}

	public void setHeader(Dictionary header) {
		put(HEADER_KEY, header);
	}

	public Object getMessage() {
		return get(MESSAGE_KEY);
	}

	public void setMessage(Object message) {
		put(MESSAGE_KEY, message);
	}

	public Map getMedias() {
		return (Map) get(MEDIA_KEY);
	}

	public void setMedias(Map media) {
		put(MEDIA_KEY, media);
	}

}
