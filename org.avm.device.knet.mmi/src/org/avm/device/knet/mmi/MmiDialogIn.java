package org.avm.device.knet.mmi;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsLabel;
import org.avm.device.knet.model.KmsNew;
import org.avm.device.knet.model.KmsUpdate;

/**
 * @author lbr
 */
public class MmiDialogIn {
	private Logger _log = null;
	private String _dialogName = null;

	private String _appId = null;

	private String _menuId = null; // 1 menu per dialog

	private int _timeOut = 0; // 1 timeout per menu

	private Hashtable _subTrees = null;

	private boolean _hasBeenBuilt = false;

	// private KmsMmi _kmsmmi;

	/**
	 * Construit un nouveau dialogue. inAppId est un identifiant utilise par
	 * l'application pour distinguer les dialogues notamment dans le cas de
	 * l'interruption d'un dialogue par un autre. Dans ce cas precis la reponse
	 * du dialogue interrompu peut croiser la soumission du second.
	 */
	public MmiDialogIn(String inDialogName, String inAppId) {
		_log = Logger.getInstance(this.getClass());
		_log.debug("New MMI dialog(" + inDialogName + ", " + inAppId + ")");

		_dialogName = inDialogName;
		_appId = inAppId;

		_subTrees = new Hashtable();
	}

	public MmiDialogIn() {
		_log = Logger.getInstance(this.getClass());
		_log.debug("New MMI dialog()");
		_subTrees = new Hashtable();
	}

	public String toString() {
		return "DialogIn [name =" + _dialogName + ", appId =" + _appId + "]";
	}

	public void reset() {
		_menuId = null;
		_timeOut = 0;
		_subTrees.clear();
		_hasBeenBuilt = false;
	}

	public void setAppId(String inAppId) {
		_appId = inAppId;
	}

	public void setDialogName(String inDialogName) {
		_dialogName = inDialogName;
	}

	public String getDialogName() {
		return _dialogName;
	}

	/**
	 * Parametre le dialog avec une valeur predefinie de texte, de menu ou
	 * d'entier. inParamId peut etre : TITLE CONTENT MENU (reserve), STOP ,
	 * DELAY. inId identifie un texte, un menu ou un entier predefini.
	 */
	public void setLabelId(String inName, String inTextId) {
		KmsLabel lab = new KmsLabel(inName);
		lab.setAttTextId(inTextId);
		_subTrees.put(inName, lab);
	}

	/**
	 * Retourne le nom de l'instance.
	 */
	public String getAppId() {
		return _appId;
	}

	public void setLabelValue(String inName, String inTextValue) {
		KmsLabel lab = new KmsLabel(inName);
		lab.setAttText(inTextValue);
		_subTrees.put(inName, lab);
	}

	public void setMenuId(String inMenuId) {
		_menuId = inMenuId;
	}

	/**
	 * Parametre le timeout du dialogue avec une valeur dynamique d'entier.
	 */
	public void setTimeOut(int inNumValue) {
		_timeOut = inNumValue;
	}

	// public String buildReq() {
	// // Open XML doc
	// StringBuffer req = new StringBuffer("<msg><mmi>");
	//
	// if (!_built) { // New dialog of that type
	// req.append("<new");
	//
	// if (null != _name) {
	// req.append(" name=\"" + _name + "\"");
	// }
	// if (null != _appId) {
	// req.append(" id=\"" + _appId + "\"");
	// }
	// if (0 != _timeOut) {
	// req.append(" timeout=\"" + _timeOut + "\"");
	// }
	// if (null != _menuId) {
	// req.append(" menu=\"" + _menuId + "\"");
	// }
	//
	// req.append("/>");
	// } else { // Update dialog
	// req.append("<update");
	//
	// if (null != _appId) {
	// req.append(" id=\"" + _appId + "\"");
	// }
	// if (0 != _timeOut) {
	// req.append(" timeout=\"" + _timeOut + "\"");
	// }
	// if (null != _menuId) {
	// req.append(" menu=\"" + _menuId + "\"");
	// }
	//
	// req.append("/>");
	// }
	//
	// // Add labels
	// Enumeration properties = _subTrees.elements();
	// while (properties.hasMoreElements()) {
	// req.append(properties.nextElement());
	// }
	//
	// // Close XML doc
	// req.append("</mmi></msg>");
	//
	// // / Reset values so that dialog is ready to be updated
	// // _appId = null;
	// // _menuId = null;
	// // _timeOut = 0;
	// // _properties.clear();
	// //
	// _built = true;
	//
	// _log.debug("Building KMS request " + req.toString());
	//
	// return (req.toString());
	// }
	/**
	 * @return
	 */
	public Hashtable getMmiSubTrees() {
		Kms kms;
		if (_hasBeenBuilt) {
			kms = new KmsUpdate(_appId, _menuId, _timeOut);
		} else {
			kms = new KmsNew(_dialogName, _appId, _menuId, _timeOut);
			_hasBeenBuilt = true;
		}
		_subTrees.put("root", kms);
		return _subTrees;
	}

}
