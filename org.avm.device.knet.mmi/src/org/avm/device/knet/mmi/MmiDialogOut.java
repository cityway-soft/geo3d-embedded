package org.avm.device.knet.mmi;

import org.avm.device.knet.model.KmsMsg;

public class MmiDialogOut {
	// private Logger _log = Logger.getInstance(this.getClass());
	public String _appId = null;
	public String _action = null;
	public String _textName = null;
	public String _textValue = null;

	/**
	 * Constructeur pour permettre la creation de "faux" dialogOut via la
	 * console.
	 * 
	 * @param mmi
	 */
	public MmiDialogOut(KmsMsg msg) {
		_appId = msg.getAppId();
		_action = msg.getAction();
		_textName = msg.getTextName();
		_textValue = msg.getTextValue();
	}

	public MmiDialogOut(String appId, String action, String textName,
			String textVal) {
		_appId = appId;
		_action = action;
		_textName = textName;
		_textValue = textVal;
	}

	public MmiDialogOut(MmiDialogOut dlgOut) {
		setAppId(dlgOut.getAppId());
		setAction(dlgOut.getAction());
		setTextName(dlgOut.getTextName());
		setTextValue(dlgOut.getTextValue());
	}

	private void setTextValue(String textValue) {
		_textValue = textValue;
	}

	private void setTextName(String textName) {
		_textName = textName;
	}

	private void setAction(String action) {
		_action = action;
	}

	/**
	 * @return the _action
	 */
	public String getAction() {
		return _action;
	}

	/**
	 * @return the _appId
	 */
	public String getAppId() {
		return _appId;
	}

	/**
	 * @return the _textName
	 */
	public String getTextName() {
		return _textName;
	}

	/**
	 * @return the _textValue
	 */
	public String getTextValue() {
		if (_textValue == null)
			return "";
		if (_textValue.equalsIgnoreCase("\n"))
			_textValue = "";
		return _textValue;
	}

	public void setAppId(String appid) {
		_appId = appid;
	}

	public String toString() {
		return "MmiDialogOut [" + _appId + ", " + _action + ", " + _textName
				+ ", " + _textValue + "]";
	}
}
