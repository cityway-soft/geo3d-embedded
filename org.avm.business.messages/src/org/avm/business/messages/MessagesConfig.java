package org.avm.business.messages;

import java.util.Dictionary;
import java.util.Properties;

import org.avm.elementary.common.Config;

public interface MessagesConfig extends Config{
	

	
	public void addMessage(String key, String reception, String debut, String fin, String jours, int destinataire,
			String affectation, String message, int priority, boolean acq);

	public Properties getMessage(String key);

	public void removeMessage(String key);

	public Dictionary getMessages();

	public void removeAllMessages();
}