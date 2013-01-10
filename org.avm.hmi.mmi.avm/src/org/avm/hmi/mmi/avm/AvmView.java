package org.avm.hmi.mmi.avm;

import org.avm.hmi.mmi.application.display.AVMDisplay;
import org.avm.hmi.mmi.application.screens.MmiConstantes;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4Arret;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4ChoixAct;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4EnCourse;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4Info;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4Saisie;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4ServiceKms;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4ServiceOcc;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4ValidChoix;
import org.avm.hmi.mmi.application.screens.MmiDialogInWrapper4ValidChoix2;

public class AvmView {
	public static final int NORMAL = 0;
	public static final int DEVIE = 1;
	public static final int LABO = 2;
	// Singleton
	private static AvmView INSTANCE = null;

	private AvmView() {
	}

	public synchronized static AvmView createInstance(AVMDisplay base) {
		if (INSTANCE == null)
			INSTANCE = new AvmView();
		INSTANCE.setBase(base);
		return INSTANCE;
	}

	private AVMDisplay _base = null;
	private MmiDialogInWrapper4Saisie _screenSaisie = null;
	private MmiDialogInWrapper4ChoixAct _screenChoixAct = null;
	private MmiDialogInWrapper4ValidChoix _screenValidChoix = null;
	private MmiDialogInWrapper4ValidChoix2 _screenValidChoix2 = null;
	private MmiDialogInWrapper4Arret _screenArret = null;
	private MmiDialogInWrapper4EnCourse _screenCourse = null;
	private MmiDialogInWrapper4ServiceKms _screenKms = null;
	private MmiDialogInWrapper4ServiceOcc _screenOcc = null;
	private MmiDialogInWrapper4Info _screenInfo = null;
	private boolean _arretIsManuel = false;

	public void setBase(AVMDisplay base) {
		_base = base;
	}

	public AVMDisplay getBase() {
		return _base;
	}

	public void activatePriseDeService(String etat) {
		_screenSaisie = MmiDialogInWrapper4Saisie.getNewInstance(etat);
		_screenSaisie.resetDialogIn();
		_screenSaisie.clearSoftKeys();
		_screenSaisie.setPrompt(MmiDialogInWrapper4Saisie.SEGMENT);
		_base.setScreen2fg(_screenSaisie);
	}

	public void activateSaisieService(String state) {
		activateSaisieService(state, null);
	}

	public void activateSaisieService(String state, String message) {
		_screenChoixAct = MmiDialogInWrapper4ChoixAct.getNewInstance(state);
		_screenChoixAct.setLine(message);
		_base.setScreen2fg(_screenChoixAct);
	}

	public void activateValidCourse(String state, String nom,
			String heureDepart, String dest) {
		_screenValidChoix2 = MmiDialogInWrapper4ValidChoix2
				.getNewInstance(state);

		StringBuffer value1 = new StringBuffer();
		value1.append(Messages.getString("avm.seg")); //$NON-NLS-1$
		value1.append(nom);
		value1.append(Messages.getString("avm.vers"));
		value1.append(dest);
		_screenValidChoix2.setLine1(value1.toString());

		StringBuffer value2 = new StringBuffer();
		value2.append(Messages.getString("avm.dep"));
		value2.append(Messages.getString("avm.a"));
		value2.append(heureDepart);

		_screenValidChoix2.setLine2(value2.toString());
		_base.setScreen2fg(_screenValidChoix2);
	}

	public void activateAttenteDepart(String etat, int delay, String arretDep,
			String nextStopName, String nextStopHeure) {
		_screenArret = MmiDialogInWrapper4Arret.getNewInstance(etat);

		_screenArret.setTerminusDepart(arretDep);
		if (nextStopName != null)
			_screenArret.setNextStop(nextStopName + Messages.getString("avm.a")
					+ nextStopHeure);
		else
			_screenArret.setNextStop(Messages.getString("avm.courseEnd"));

		_screenArret.setDelaiAvantDepart(delay);
		_base.setScreen2fg(_screenArret);
	}

	public void refreshAttenteDepart(int delai) {
		_screenArret.update(delai);
		_base.updateScreen2fg(_screenArret);
	}

	public void activateSuiviCourse(String etat, String nomNextArret,
			String heureNextArret, int ar, int deviation_labo) {
		_screenCourse = MmiDialogInWrapper4EnCourse.getNewInstance(etat);
		_screenCourse.setAvanceRetard(ar);
		if (nomNextArret == null)
			_screenCourse.setNextStop(Messages.getString("avm.courseEnd")); //$NON-NLS-1$
		else {
			_screenCourse.setNextStop(nomNextArret
					+ Messages.getString("avm.a") + heureNextArret);
		}
		if (deviation_labo == NORMAL)
			_screenCourse.setSoftKey(MmiConstantes.F5,
					MmiConstantes.SK_DEVIATION);
		else if (deviation_labo == LABO)// Prochaine coordonnées inconnues
			_screenCourse.setSoftKey(MmiConstantes.F5, MmiConstantes.SK_IN);
		else
			// Le conducteur est en déviation
			_screenCourse.setSoftKey(MmiConstantes.F5, MmiConstantes.SK_DEVIE);

		_base.setScreen2fg(_screenCourse);
	}

	public void activateSuiviCourseKM(String etat) {
		_screenKms = MmiDialogInWrapper4ServiceKms.getNewInstance(etat);
		_screenKms.setLabelId(MmiConstantes.INF_KMS_AV);
		_base.setScreen2fg(_screenKms);
	}

	public void activateSuiviCourseSO(String etat) {
		_screenOcc = MmiDialogInWrapper4ServiceOcc.getNewInstance(etat);
		_screenOcc.setLabelId(MmiConstantes.INF_SERV_OCC);
		_base.setScreen2fg(_screenOcc);
	}

	// le 11/01/2008 on souhaite pouvoir terminer une course à tout moment : on
	// affiche le F2 tout le temps (afficheF2 est toujours vrai)
	// public void activateArret(String etat, String nomArret, String
	// heureArret, String nomNextArret, String heureNextArret, boolean
	// afficheF2) {
	// if (_arretIsManuel){
	// if (afficheF2){
	// _screenArretF2F5 =
	// MmiDialogInWrapperAvantDernierArret_F5.getNewInstance(MmiConstantes.ADARRET_F5,etat);
	//				
	// _screenArretF2F5.setLine1(nomArret);
	// _screenArretF2F5.setLine2(heureArret);
	// if (nomNextArret != null)
	// _screenArretF2F5.setNextStop(nomNextArret+Messages.getString("avm.a")+heureNextArret);
	// else
	// _screenArretF2F5.setNextStop(Messages.getString("avm.courseEnd"));
	// _base.setScreen2fg(_screenArretF2F5);
	// }else{
	// _screenArretF5 =
	// MmiDialogInWrapperArret_F5.getNewInstance(MmiConstantes.ARRET_F5, etat);
	// _screenArretF5.setLine1(nomArret);
	// _screenArretF5.setLine2(heureArret);
	// if (nomNextArret != null)
	// _screenArretF5.setNextStop(nomNextArret+Messages.getString("avm.a")+heureNextArret);
	// else
	// _screenArretF5.setNextStop(Messages.getString("avm.unknownNext"));
	// _base.setScreen2fg(_screenArretF5);
	// }
	// }else{
	// if (afficheF2){
	// _screenArretF2 =
	// MmiDialogInWrapperAvantDernierArret.getNewInstance(MmiConstantes.ADARRET,etat);
	//				
	// _screenArretF2.setLine1(nomArret);
	// _screenArretF2.setLine2(heureArret);
	// if (nomNextArret != null)
	// _screenArretF2.setNextStop(nomNextArret+Messages.getString("avm.a")+heureNextArret);
	// else
	// _screenArretF2.setNextStop(Messages.getString("avm.courseEnd"));
	// _base.setScreen2fg(_screenArretF2);
	// }else{
	// _screenArret =
	// MmiDialogInWrapperArret.getNewInstance(MmiConstantes.ARRET, etat);
	// _screenArret.setLine1(nomArret);
	// _screenArret.setLine2(heureArret);
	// if (nomNextArret != null)
	// _screenArret.setNextStop(nomNextArret+Messages.getString("avm.a")+heureNextArret);
	// else
	// _screenArret.setNextStop(Messages.getString("avm.unknownNext"));
	// _base.setScreen2fg(_screenArret);
	// }
	// }
	// }
	public void activateArret(String etat, String nomArret, String heureArret,
			String nomNextArret, String heureNextArret) {
		_screenArret = MmiDialogInWrapper4Arret.getNewInstance(etat);

		_screenArret.setLine1(nomArret);
		_screenArret.setLine2(heureArret);
		if (nomNextArret != null)
			_screenArret.setNextStop(nomNextArret + Messages.getString("avm.a")
					+ heureNextArret);
		else
			_screenArret.setNextStop(Messages.getString("avm.courseEnd"));
		_base.setScreen2fg(_screenArret);
	}

	public void activatePanne(String etat) {
		_screenInfo = MmiDialogInWrapper4Info.getNewInstance(etat);
		_screenInfo.setLabelId(MmiConstantes.INF_HS);
		_screenInfo.setSoftKey(MmiConstantes.F5, MmiConstantes.SK_FIN_PANNE);
		_base.setScreen2fg(_screenInfo);
	}

	public void activateValidGeoloc(String etat, String nomArret) {
		_screenValidChoix = MmiDialogInWrapper4ValidChoix.getNewInstance(etat);
		_screenValidChoix.setLine1(nomArret
				+ Messages.getString("avm.arretGeolocalise"));
		_base.setScreen2fg(_screenValidChoix);
	}

	public void activateValidFinService(String state, String msg) {
		_screenValidChoix = MmiDialogInWrapper4ValidChoix.getNewInstance(state);
		_screenValidChoix.setLine1(Messages.getString("avm.finSeg") + msg);
		_base.setScreen2fg(_screenValidChoix);
	}

	public void activateValidPanne(String etat) {
		_screenValidChoix = MmiDialogInWrapper4ValidChoix.getNewInstance(etat);
		_screenValidChoix.setLabelId(MmiConstantes.INF_HS);
		_base.setScreen2fg(_screenValidChoix);
	}

	public void activateValidFinPanne(String etat) {
		_screenValidChoix = MmiDialogInWrapper4ValidChoix.getNewInstance(etat);
		_screenValidChoix.setLabelId(MmiConstantes.INF_FIN_HS);
		_base.setScreen2fg(_screenValidChoix);
	}

	public void activateValidChoixSO(String etat) {
		_screenValidChoix = MmiDialogInWrapper4ValidChoix.getNewInstance(etat);
		_screenValidChoix.setLabelId(MmiConstantes.INF_SERV_OCC);
		_base.setScreen2fg(_screenValidChoix);
	}

	public void activateValidChoixKM(String etat) {
		_screenValidChoix = MmiDialogInWrapper4ValidChoix.getNewInstance(etat);
		_screenValidChoix.setLabelId(MmiConstantes.INF_KMS_AV);
		_base.setScreen2fg(_screenValidChoix);
	}

	public void activateValidFinSo(String etat) {
		_screenValidChoix = MmiDialogInWrapper4ValidChoix.getNewInstance(etat);
		_screenValidChoix.setLabelId(MmiConstantes.INF_FIN_SO);
		_base.setScreen2fg(_screenValidChoix);
	}

	public void activateValidFinKm(String etat) {
		_screenValidChoix = MmiDialogInWrapper4ValidChoix.getNewInstance(etat);
		_screenValidChoix.setLabelId(MmiConstantes.INF_FIN_KM);
		_base.setScreen2fg(_screenValidChoix);
	}

	public void refresh() {
		_base.clearMessages();
		_base.submit();
	}

	public void back() {
		_base.back();
	}

	public void setArretManuel() {
		_arretIsManuel = true;
	}

	public void resetArretManuel() {
		_arretIsManuel = false;
	}
}
