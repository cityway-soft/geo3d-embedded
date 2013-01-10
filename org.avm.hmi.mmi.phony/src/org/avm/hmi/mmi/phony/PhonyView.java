package org.avm.hmi.mmi.phony;

import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.application.screens.MmiConstantes;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4InfoAppel;

public class PhonyView {
	// Singleton
	private static PhonyView INSTANCE = null;

	private PhonyView() {
	}

	public synchronized static PhonyView createInstance(AVMDisplay base) {
		if (INSTANCE == null)
			INSTANCE = new PhonyView();
		INSTANCE.setBase(base);
		return INSTANCE;
	}

	private AVMDisplay _base = null;
	private MmiDialogInWrapper4InfoAppel _screenAppel = null;

	public void setBase(AVMDisplay base) {
		_base = base;
	}

	public void drawRinging(String etat, String callingNum) {
		_screenAppel = MmiDialogInWrapper4InfoAppel.getNewInstance(etat);

		if (callingNum.trim().equalsIgnoreCase(Messages.getString("phony.CM")))
			_screenAppel.setLabelId(MmiConstantes.LABEL_LINE1,
					MmiConstantes.INF_RINGING);
		else {
			_screenAppel.setCallingInfos(callingNum
					+ Messages.getString("phony.appelle"));
		}
		_screenAppel.setSoftKey(MmiConstantes.F1, MmiConstantes.SK_DECROCHER);
		_base.setScreen2fg(_screenAppel);
	}

	public void drawCalling() {
		_screenAppel.setLabelId(MmiConstantes.LABEL_LINE1,
				MmiConstantes.INF_CALLING_CM);
		_screenAppel.setSoftKey(MmiConstantes.F1, MmiConstantes.SK_RACCROCHER);
		_base.setView2fgAlways(_screenAppel);
	}

	public void changeSoftKey(String key, String sk) {
		_base.setSoftKey2fgView(key, sk);
	}

	public void resetSoftKey(String key) {
		_base.resetSoftKey2fgView(key);
	}

	public void refresh() {
		_base.submit();
	}

	public void unsetPhoneScreenFgAndReset(String key) {
		_base.unsetViewFromfgAlways(key, _screenAppel);
		_screenAppel.resetDialogIn();
	}

}
