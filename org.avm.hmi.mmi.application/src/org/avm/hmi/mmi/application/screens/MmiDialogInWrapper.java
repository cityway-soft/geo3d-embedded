package org.avm.hmi.mmi.application.screens;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.avm.device.knet.mmi.MmiDialogIn;

/**
 * @author lbr
 */
public abstract class MmiDialogInWrapper implements MmiConstantes {

	protected Logger _log = Logger.getInstance(MmiDialogInWrapper.class);
	// Les softkeys communes à tous les écrans
	private static Hashtable _softKeys = new Hashtable();

	// Les messages communs à tous les écrans
	private static Hashtable _messages = new Hashtable(2);

	private MmiDialogIn _dialogIn;

	protected MmiDialogInWrapper() {
		// _log.setPriority(Priority.DEBUG);
		_dialogIn = new MmiDialogIn();
	}

	public abstract String abstractGetLabel4Key(String key);

	public void resetDialogIn() {
		_log.debug("resetDialogIn()");
		_dialogIn.reset();
	}

	protected abstract void reset();

	protected void setLabelValue(String inName, String inTextValue) {
		_log.debug("setLabelValue(" + inName + "," + inTextValue + ")");
		if (inTextValue != null)
			_dialogIn.setLabelValue(inName, inTextValue);
	}

	public void setLabelId(String labelId, String valueId) {
		_log.debug("setLabelId(" + labelId + "," + valueId + ")");
		if (valueId != null)
			_dialogIn.setLabelId(labelId, valueId);
	}

	public void setMessage(String message) {
		if (message == null) {
			clearMessages();
			return;
		}
		message = message.trim();
		_log.debug("setMessage(" + message + ")");
		StringTokenizer strtok = new StringTokenizer(message);
		StringBuffer line1 = new StringBuffer();
		StringBuffer line2 = new StringBuffer();
		if (strtok.countTokens() <= 1) {
			line1.append(message.substring(
					0,
					(message.length() > SIZE_MAX_MSG) ? SIZE_MAX_MSG : message
							.length()).trim());
		} else {
			int taille1 = 0;
			boolean line1full = false;
			while (strtok.hasMoreTokens()) {
				String tmp = strtok.nextToken();
				// entre 0 et SIZE_MAX_MSG caractères : line 1
				// puis entre SIZE_MAX_MSG+1 et 2*SIZE_MAX_MSG car : line2
				taille1 = line1.length() + tmp.length();
				if (taille1 >= SIZE_MAX_MSG)
					line1full = true;
				if (!line1full) {
					line1.append(tmp);
					line1.append(" ");
				}
			}
			if (line1full) {
				int index = line1.length() - 1;
				_log.debug("index = " + index + " message.length="
						+ message.length());
				// remplir la deuxieme ligne
				if (index < message.length()) {
					if (message.length() >= index + SIZE_MAX_MSG)
						line2.append(message.substring(index,
								index + SIZE_MAX_MSG).trim());
					else
						line2.append(message.substring(index).trim());
				}
			}
		}
		_log.debug("line1 = " + line1.toString() + " line2 = "
				+ line2.toString());

		setMessage(LABEL_MSG1, line1.toString());
		setMessage(LABEL_MSG2, line2.toString());
	}

	private void setMessage(String label_msg, String newValue) {
		_messages.put(label_msg, newValue);
	}

	public void resetMessage(String label_msg) {
		try {
			_messages.remove(label_msg);
		} catch (Exception e) {
			error("Erreur dans remove(" + label_msg + ")", e);
		}
	}

	public void clearMessages() {
		_log.debug("clearMessages()");
		_messages.clear();
		resetSoftKey(F1);
	}

	public void setSoftKey(String softkey, String newValue) {
		_softKeys.put(softkey, newValue);
		refreshSoftKeyValues();
	}

	public void resetSoftKey(String softkey) {
		if (softkey == null)
			return;
		try {
			_softKeys.remove(softkey);
		} catch (Exception e) {
			error("Erreur dans remove(" + softkey + ")", e);
		}
	}

	public void clearSoftKeys() {
		_log.debug("clearSoftKeys()");
		_softKeys.clear();
	}

	protected void refreshSoftKeyValues() {
		_log.debug("refreshSoftKeyValues"); //$NON-NLS-1$
		Enumeration keys = _softKeys.keys();
		while (keys.hasMoreElements()) {
			String k = (String) keys.nextElement();
			if (getLabel4Key(k) != null) {
				_log
						.debug("setLabelId(" + k + "[" + getLabel4Key(k) + "]," + (String) _softKeys.get(k) + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				_dialogIn
						.setLabelId(getLabel4Key(k), (String) _softKeys.get(k));
			}
		}
	}

	private String getLabel4Key(String k) {
		if (k != null)
			return abstractGetLabel4Key(k);
		return null;
	}

	private String getDialogName() {
		return _dialogIn.getDialogName();
	}

	protected void setDialogName(String screenName) {
		_dialogIn.setDialogName(screenName);
	}

	public void setAppId(String etat) {
		_dialogIn.setAppId(etat);
	}

	public String toString() {
		return getDialogName() + " [" + _dialogIn.getAppId() + "]";
	}

	public boolean equals(Object s) {
		if (s instanceof MmiDialogInWrapper) {
			return getDialogName().equals(
					((MmiDialogInWrapper) s).getDialogName());
		}
		return false;
	}

	public abstract void abstractGetDialogIn();

	public MmiDialogIn getDialogIn() {
		abstractGetDialogIn();
		_dialogIn.setLabelValue(LABEL_MSG1, (String) _messages.get(LABEL_MSG1));
		_dialogIn.setLabelValue(LABEL_MSG2, (String) _messages.get(LABEL_MSG2));
		refreshSoftKeyValues();
		return _dialogIn;
	}

	protected void error(String msg, Exception e) {
		// cree un log a la log4J : 9159190 ERROR
		// [org.avm.elementary.application]
		long time = System.currentTimeMillis() / 1000;
		System.err.println(time + " ERROR [" + this.getClass().getName() + "] "
				+ msg);
		e.printStackTrace(System.err);
	}
}
