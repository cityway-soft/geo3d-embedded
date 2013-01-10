package org.avm.business.messages.bundle;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.avm.business.messages.Messages;
import org.avm.business.messages.MessagesConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements MessagesConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public void addMessage(String key, String debut, String fin, String jours,
			int destinataire, String affectation, String message, int priority,
			boolean acq) {
		Properties p = new Properties();
		if (message != null && key != null) {
			p.put(Messages.ID, key);
			p.put(Messages.MESSAGE, message);
			p.put(Messages.DEBUT, debut == null ? "" : debut);
			p.put(Messages.FIN, fin == null ? "" : fin);
			p.put(Messages.JOURSEMAINE, formatJours(jours));
			p.put(Messages.TYPE, Integer.toString(destinataire));
			p.put(Messages.AFFECTATION, affectation == null ? Messages.AFFECTATION_ALL : affectation);
			p.put(Messages.MESSAGE, message);
			p.put(Messages.PRIORITE, Integer.toString(priority));
			p.put(Messages.ACQUITTEMENT, acq ? "true" : "false");
		}

		String text = save(p);
		_config.put(key, text);
	}
	
	private String formatJours(String jour){
		if (jour == null){
			return "LMMJVSD";
		}
		if (jour.length()>7){
			jour = jour.substring(0,6);
			System.out.println("decoupe :=> " + jour);
		}
		StringBuffer buf = new StringBuffer(jour);
		while(buf.length()<7){
			buf.append("_");
		}
		String j=buf.toString();
		buf.delete(0,7);
		buf.append((j.charAt(0)=='_')?"_":"L");
		buf.append((j.charAt(1)=='_')?"_":"M");
		buf.append((j.charAt(2)=='_')?"_":"M");
		buf.append((j.charAt(3)=='_')?"_":"J");
		buf.append((j.charAt(4)=='_')?"_":"V");
		buf.append((j.charAt(5)=='_')?"_":"S");
		buf.append((j.charAt(6)=='_')?"_":"D");

		return buf.toString();
	}

	public Properties getMessage(String key) {
		String text = (String) _config.get(key);
		if (text != null) {
			return load(text);
		} else {
			return null;
		}
	}

	public void removeMessage(String key) {
		_config.remove(key);
	}

	public Dictionary getMessages() {
		Hashtable map = new Hashtable();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))

				continue;
			Properties p = getMessage(key);
			if (p != null) {
				map.put(key, p);
			}
		}
		return map;
	}

	public String toString() {
		StringBuffer text = new StringBuffer();

		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))

				continue;
			Properties p = getMessage(key);
			if (p != null) {
				text.append(p.toString() + "\n");
			}
		}
		return text.toString();
	}

	public void removeAllMessages() {
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String key = (String) it.nextElement();
			if ("service.pid".equals(key) || "config.date".equals(key)
					|| "service.bundleLocation".equals(key)
					|| "org.avm.elementary.dummy.property".equals(key)
					|| "org.avm.config.version".equals(key))

				continue;
			removeMessage(key);
		}
	}

}
