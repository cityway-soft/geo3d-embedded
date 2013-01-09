/**
 * 
 */
package org.avm.business.site.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author lbr
 * 
 */
public class ExchangeControler {
	public static int ERRCOUNT = 0;
	public static final int STATUS_CODE_OK = 200;
	public static final int STATUS_CODE_NOT_FOUND = 404;
	private static String[] JSONs = {
			"{\"jex_date\":{\"time\":1244481909667,\"class\":\"class java.util.Date\",},\"statut\":0,\"course\":null,\"messages\":[],\"class\":\"class org.avm.business.tft.model.SERVICE\",}", //$NON-NLS-1$
			"{\"jex_date\":{\"time\":1244482057247,\"class\":\"class java.util.Date\",},\"statut\":3,\"course\":{\"crs_id\":59,\"crs_idu\":5,\"crs_nom\":null,\"crs_depart\":18900,\"lgn_idu\":0,\"lgn_nom\":\"1\",\"lgn_amplitude\":180,\"lgn_chevauchement\":60,\"crd_statut\":0,\"points\":[{\"pnt_id\":24,\"pnt_idu\":46,\"pnt_nom\":\"Bitche Gare SNCF\",\"grp_nom\":\"BIT1\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":223,\"hod_arrivee\":18900,\"hod_attente\":0,\"hod_arrivee_theorique\":18900,\"hod_attente_theorique\":0,\"hod_rang\":1,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":25,\"pnt_idu\":47,\"pnt_nom\":\"Bitche Actiparc\",\"grp_nom\":\"BITC\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":19080,\"hod_attente\":0,\"hod_arrivee_theorique\":19080,\"hod_attente_theorique\":0,\"hod_rang\":2,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":26,\"pnt_idu\":48,\"pnt_nom\":\"Eguelshardt Abri\",\"grp_nom\":\"EGUE\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":19440,\"hod_attente\":0,\"hod_arrivee_theorique\":19440,\"hod_attente_theorique\":0,\"hod_rang\":3,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":27,\"pnt_idu\":49,\"pnt_nom\":\"Bannstein rue de la Gare\",\"grp_nom\":\"BANN\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":19620,\"hod_attente\":0,\"hod_arrivee_theorique\":19620,\"hod_attente_theorique\":0,\"hod_rang\":4,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":28,\"pnt_idu\":50,\"pnt_nom\":\"Philippsbourg Lieschbach\",\"grp_nom\":\"PHI1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":19920,\"hod_attente\":0,\"hod_arrivee_theorique\":19920,\"hod_attente_theorique\":0,\"hod_rang\":5,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":29,\"pnt_idu\":51,\"pnt_nom\":\"Philippsbourg Falkenstein\",\"grp_nom\":\"PHIL\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20040,\"hod_attente\":0,\"hod_arrivee_theorique\":20040,\"hod_attente_theorique\":0,\"hod_rang\":6,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":30,\"pnt_idu\":52,\"pnt_nom\":\"Niederbronn Usine\",\"grp_nom\":\"NIE5\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20460,\"hod_attente\":0,\"hod_arrivee_theorique\":20460,\"hod_attente_theorique\":0,\"hod_rang\":7,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":31,\"pnt_idu\":54,\"pnt_nom\":\"Niederbronn Gare SNCF\",\"grp_nom\":\"NIE8\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20580,\"hod_attente\":0,\"hod_arrivee_theorique\":20580,\"hod_attente_theorique\":0,\"hod_rang\":8,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":32,\"pnt_idu\":55,\"pnt_nom\":\"Niederbronn Hôtel de ville\",\"grp_nom\":\"NIE1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20700,\"hod_attente\":0,\"hod_arrivee_theorique\":20700,\"hod_attente_theorique\":0,\"hod_rang\":9,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":33,\"pnt_idu\":56,\"pnt_nom\":\"Niederbronn Archéologie\",\"grp_nom\":\"NIED\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20820,\"hod_attente\":0,\"hod_arrivee_theorique\":20820,\"hod_attente_theorique\":0,\"hod_rang\":10,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":34,\"pnt_idu\":57,\"pnt_nom\":\"Reichshoffen Gare SNCF\",\"grp_nom\":\"REI3\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21060,\"hod_attente\":0,\"hod_arrivee_theorique\":21060,\"hod_attente_theorique\":0,\"hod_rang\":11,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":35,\"pnt_idu\":58,\"pnt_nom\":\"Reichshoffen Usine\",\"grp_nom\":\"REI1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21240,\"hod_attente\":0,\"hod_arrivee_theorique\":21240,\"hod_attente_theorique\":0,\"hod_rang\":12,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":36,\"pnt_idu\":59,\"pnt_nom\":\"Reichshoffen au Sapin\",\"grp_nom\":\"REIC\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21300,\"hod_attente\":0,\"hod_arrivee_theorique\":21300,\"hod_attente_theorique\":0,\"hod_rang\":13,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":37,\"pnt_idu\":60,\"pnt_nom\":\"Gundershoffen au Cygne\",\"grp_nom\":\"GUN1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21420,\"hod_attente\":0,\"hod_arrivee_theorique\":21420,\"hod_attente_theorique\":0,\"hod_rang\":14,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":38,\"pnt_idu\":62,\"pnt_nom\":\"Gundershoffen Gare SNCF\",\"grp_nom\":\"GUN4\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21480,\"hod_attente\":0,\"hod_arrivee_theorique\":21480,\"hod_attente_theorique\":0,\"hod_rang\":15,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":39,\"pnt_idu\":63,\"pnt_nom\":\"Griesbach Moulin\",\"grp_nom\":\"GRIE\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21660,\"hod_attente\":0,\"hod_arrivee_theorique\":21660,\"hod_attente_theorique\":0,\"hod_rang\":16,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":40,\"pnt_idu\":64,\"pnt_nom\":\"Mertzwiller Gare SNCF\",\"grp_nom\":\"MER1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21840,\"hod_attente\":0,\"hod_arrivee_theorique\":21840,\"hod_attente_theorique\":0,\"hod_rang\":17,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":41,\"pnt_idu\":65,\"pnt_nom\":\"Mertzwiller Monuments\",\"grp_nom\":\"MERT\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21900,\"hod_attente\":0,\"hod_arrivee_theorique\":21900,\"hod_attente_theorique\":0,\"hod_rang\":18,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":42,\"pnt_idu\":66,\"pnt_nom\":\"Schweighouse Gare SNCF\",\"grp_nom\":\"SCH1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":22620,\"hod_attente\":0,\"hod_arrivee_theorique\":22620,\"hod_attente_theorique\":0,\"hod_rang\":19,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":43,\"pnt_idu\":67,\"pnt_nom\":\"Schweighouse Au Faubourg\",\"grp_nom\":\"SCHW\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":22500,\"hod_attente\":0,\"hod_arrivee_theorique\":22500,\"hod_attente_theorique\":0,\"hod_rang\":20,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":44,\"pnt_idu\":68,\"pnt_nom\":\"Haguenau Gare SNCF\",\"grp_nom\":\"HAGU\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":22980,\"hod_attente\":0,\"hod_arrivee_theorique\":22980,\"hod_attente_theorique\":0,\"hod_rang\":21,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",}],\"class\":\"class org.avm.business.tft.model.COURSE\",},\"messages\":[],\"class\":\"class org.avm.business.tft.model.SERVICE\",}", //$NON-NLS-1$
			"{\"jex_date\":{\"time\":1244482113050,\"class\":\"class java.util.Date\",},\"statut\":4,\"course\":{\"crs_id\":59,\"crs_idu\":5,\"crs_nom\":null,\"crs_depart\":18900,\"lgn_idu\":0,\"lgn_nom\":\"1\",\"lgn_amplitude\":180,\"lgn_chevauchement\":60,\"crd_statut\":1,\"points\":[{\"pnt_id\":24,\"pnt_idu\":46,\"pnt_nom\":\"Bitche Gare SNCF\",\"grp_nom\":\"BIT1\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":223,\"hod_arrivee\":18900,\"hod_attente\":0,\"hod_arrivee_theorique\":18900,\"hod_attente_theorique\":0,\"hod_rang\":1,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":25,\"pnt_idu\":47,\"pnt_nom\":\"Bitche Actiparc\",\"grp_nom\":\"BITC\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":19080,\"hod_attente\":0,\"hod_arrivee_theorique\":19080,\"hod_attente_theorique\":0,\"hod_rang\":2,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":26,\"pnt_idu\":48,\"pnt_nom\":\"Eguelshardt Abri\",\"grp_nom\":\"EGUE\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":19440,\"hod_attente\":0,\"hod_arrivee_theorique\":19440,\"hod_attente_theorique\":0,\"hod_rang\":3,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":27,\"pnt_idu\":49,\"pnt_nom\":\"Bannstein rue de la Gare\",\"grp_nom\":\"BANN\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":19620,\"hod_attente\":0,\"hod_arrivee_theorique\":19620,\"hod_attente_theorique\":0,\"hod_rang\":4,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":28,\"pnt_idu\":50,\"pnt_nom\":\"Philippsbourg Lieschbach\",\"grp_nom\":\"PHI1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":19920,\"hod_attente\":0,\"hod_arrivee_theorique\":19920,\"hod_attente_theorique\":0,\"hod_rang\":5,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":29,\"pnt_idu\":51,\"pnt_nom\":\"Philippsbourg Falkenstein\",\"grp_nom\":\"PHIL\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20040,\"hod_attente\":0,\"hod_arrivee_theorique\":20040,\"hod_attente_theorique\":0,\"hod_rang\":6,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":30,\"pnt_idu\":52,\"pnt_nom\":\"Niederbronn Usine\",\"grp_nom\":\"NIE5\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20460,\"hod_attente\":0,\"hod_arrivee_theorique\":20460,\"hod_attente_theorique\":0,\"hod_rang\":7,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":31,\"pnt_idu\":54,\"pnt_nom\":\"Niederbronn Gare SNCF\",\"grp_nom\":\"NIE8\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20580,\"hod_attente\":0,\"hod_arrivee_theorique\":20580,\"hod_attente_theorique\":0,\"hod_rang\":8,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":32,\"pnt_idu\":55,\"pnt_nom\":\"Niederbronn Hôtel de ville\",\"grp_nom\":\"NIE1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20700,\"hod_attente\":0,\"hod_arrivee_theorique\":20700,\"hod_attente_theorique\":0,\"hod_rang\":9,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":33,\"pnt_idu\":56,\"pnt_nom\":\"Niederbronn Archéologie\",\"grp_nom\":\"NIED\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":20820,\"hod_attente\":0,\"hod_arrivee_theorique\":20820,\"hod_attente_theorique\":0,\"hod_rang\":10,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":34,\"pnt_idu\":57,\"pnt_nom\":\"Reichshoffen Gare SNCF\",\"grp_nom\":\"REI3\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21060,\"hod_attente\":0,\"hod_arrivee_theorique\":21060,\"hod_attente_theorique\":0,\"hod_rang\":11,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":35,\"pnt_idu\":58,\"pnt_nom\":\"Reichshoffen Usine\",\"grp_nom\":\"REI1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21240,\"hod_attente\":0,\"hod_arrivee_theorique\":21240,\"hod_attente_theorique\":0,\"hod_rang\":12,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":36,\"pnt_idu\":59,\"pnt_nom\":\"Reichshoffen au Sapin\",\"grp_nom\":\"REIC\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21300,\"hod_attente\":0,\"hod_arrivee_theorique\":21300,\"hod_attente_theorique\":0,\"hod_rang\":13,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":37,\"pnt_idu\":60,\"pnt_nom\":\"Gundershoffen au Cygne\",\"grp_nom\":\"GUN1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21420,\"hod_attente\":0,\"hod_arrivee_theorique\":21420,\"hod_attente_theorique\":0,\"hod_rang\":14,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":38,\"pnt_idu\":62,\"pnt_nom\":\"Gundershoffen Gare SNCF\",\"grp_nom\":\"GUN4\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21480,\"hod_attente\":0,\"hod_arrivee_theorique\":21480,\"hod_attente_theorique\":0,\"hod_rang\":15,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":39,\"pnt_idu\":63,\"pnt_nom\":\"Griesbach Moulin\",\"grp_nom\":\"GRIE\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21660,\"hod_attente\":0,\"hod_arrivee_theorique\":21660,\"hod_attente_theorique\":0,\"hod_rang\":16,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":40,\"pnt_idu\":64,\"pnt_nom\":\"Mertzwiller Gare SNCF\",\"grp_nom\":\"MER1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21840,\"hod_attente\":0,\"hod_arrivee_theorique\":21840,\"hod_attente_theorique\":0,\"hod_rang\":17,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":41,\"pnt_idu\":65,\"pnt_nom\":\"Mertzwiller Monuments\",\"grp_nom\":\"MERT\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":21900,\"hod_attente\":0,\"hod_arrivee_theorique\":21900,\"hod_attente_theorique\":0,\"hod_rang\":18,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":42,\"pnt_idu\":66,\"pnt_nom\":\"Schweighouse Gare SNCF\",\"grp_nom\":\"SCH1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":22620,\"hod_attente\":0,\"hod_arrivee_theorique\":22620,\"hod_attente_theorique\":0,\"hod_rang\":19,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":43,\"pnt_idu\":67,\"pnt_nom\":\"Schweighouse Au Faubourg\",\"grp_nom\":\"SCHW\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":22500,\"hod_attente\":0,\"hod_arrivee_theorique\":22500,\"hod_attente_theorique\":0,\"hod_rang\":20,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":44,\"pnt_idu\":68,\"pnt_nom\":\"Haguenau Gare SNCF\",\"grp_nom\":\"HAGU\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":22980,\"hod_attente\":0,\"hod_arrivee_theorique\":22980,\"hod_attente_theorique\":0,\"hod_rang\":21,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",}],\"class\":\"class org.avm.business.tft.model.COURSE\",},\"messages\":[],\"class\":\"class org.avm.business.tft.model.SERVICE\",}", //$NON-NLS-1$
			"{\"jex_date\":{\"time\":1244482169536,\"class\":\"class java.util.Date\",},\"statut\":7,\"course\":{\"crs_id\":59,\"crs_idu\":5,\"crs_nom\":null,\"crs_depart\":18900,\"lgn_idu\":0,\"lgn_nom\":\"1\",\"lgn_amplitude\":180,\"lgn_chevauchement\":60,\"crd_statut\":0,\"points\":[{\"pnt_id\":24,\"pnt_idu\":46,\"pnt_nom\":\"Bitche Gare SNCF\",\"grp_nom\":\"BIT1\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":223,\"hod_arrivee\":62940,\"hod_attente\":0,\"hod_arrivee_theorique\":18900,\"hod_attente_theorique\":0,\"hod_rang\":1,\"hod_statut\":1,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":25,\"pnt_idu\":47,\"pnt_nom\":\"Bitche Actiparc\",\"grp_nom\":\"BITC\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":63120,\"hod_attente\":0,\"hod_arrivee_theorique\":19080,\"hod_attente_theorique\":0,\"hod_rang\":2,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":26,\"pnt_idu\":48,\"pnt_nom\":\"Eguelshardt Abri\",\"grp_nom\":\"EGUE\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":63480,\"hod_attente\":0,\"hod_arrivee_theorique\":19440,\"hod_attente_theorique\":0,\"hod_rang\":3,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":27,\"pnt_idu\":49,\"pnt_nom\":\"Bannstein rue de la Gare\",\"grp_nom\":\"BANN\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":63660,\"hod_attente\":0,\"hod_arrivee_theorique\":19620,\"hod_attente_theorique\":0,\"hod_rang\":4,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":28,\"pnt_idu\":50,\"pnt_nom\":\"Philippsbourg Lieschbach\",\"grp_nom\":\"PHI1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":63960,\"hod_attente\":0,\"hod_arrivee_theorique\":19920,\"hod_attente_theorique\":0,\"hod_rang\":5,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":29,\"pnt_idu\":51,\"pnt_nom\":\"Philippsbourg Falkenstein\",\"grp_nom\":\"PHIL\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64080,\"hod_attente\":0,\"hod_arrivee_theorique\":20040,\"hod_attente_theorique\":0,\"hod_rang\":6,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":30,\"pnt_idu\":52,\"pnt_nom\":\"Niederbronn Usine\",\"grp_nom\":\"NIE5\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64500,\"hod_attente\":0,\"hod_arrivee_theorique\":20460,\"hod_attente_theorique\":0,\"hod_rang\":7,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":31,\"pnt_idu\":54,\"pnt_nom\":\"Niederbronn Gare SNCF\",\"grp_nom\":\"NIE8\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64620,\"hod_attente\":0,\"hod_arrivee_theorique\":20580,\"hod_attente_theorique\":0,\"hod_rang\":8,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":32,\"pnt_idu\":55,\"pnt_nom\":\"Niederbronn Hôtel de ville\",\"grp_nom\":\"NIE1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64740,\"hod_attente\":0,\"hod_arrivee_theorique\":20700,\"hod_attente_theorique\":0,\"hod_rang\":9,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":33,\"pnt_idu\":56,\"pnt_nom\":\"Niederbronn Archéologie\",\"grp_nom\":\"NIED\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64860,\"hod_attente\":0,\"hod_arrivee_theorique\":20820,\"hod_attente_theorique\":0,\"hod_rang\":10,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":34,\"pnt_idu\":57,\"pnt_nom\":\"Reichshoffen Gare SNCF\",\"grp_nom\":\"REI3\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65100,\"hod_attente\":0,\"hod_arrivee_theorique\":21060,\"hod_attente_theorique\":0,\"hod_rang\":11,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":35,\"pnt_idu\":58,\"pnt_nom\":\"Reichshoffen Usine\",\"grp_nom\":\"REI1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65280,\"hod_attente\":0,\"hod_arrivee_theorique\":21240,\"hod_attente_theorique\":0,\"hod_rang\":12,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":36,\"pnt_idu\":59,\"pnt_nom\":\"Reichshoffen au Sapin\",\"grp_nom\":\"REIC\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65340,\"hod_attente\":0,\"hod_arrivee_theorique\":21300,\"hod_attente_theorique\":0,\"hod_rang\":13,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":37,\"pnt_idu\":60,\"pnt_nom\":\"Gundershoffen au Cygne\",\"grp_nom\":\"GUN1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65460,\"hod_attente\":0,\"hod_arrivee_theorique\":21420,\"hod_attente_theorique\":0,\"hod_rang\":14,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":38,\"pnt_idu\":62,\"pnt_nom\":\"Gundershoffen Gare SNCF\",\"grp_nom\":\"GUN4\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65520,\"hod_attente\":0,\"hod_arrivee_theorique\":21480,\"hod_attente_theorique\":0,\"hod_rang\":15,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":39,\"pnt_idu\":63,\"pnt_nom\":\"Griesbach Moulin\",\"grp_nom\":\"GRIE\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65700,\"hod_attente\":0,\"hod_arrivee_theorique\":21660,\"hod_attente_theorique\":0,\"hod_rang\":16,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":40,\"pnt_idu\":64,\"pnt_nom\":\"Mertzwiller Gare SNCF\",\"grp_nom\":\"MER1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65880,\"hod_attente\":0,\"hod_arrivee_theorique\":21840,\"hod_attente_theorique\":0,\"hod_rang\":17,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":41,\"pnt_idu\":65,\"pnt_nom\":\"Mertzwiller Monuments\",\"grp_nom\":\"MERT\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65940,\"hod_attente\":0,\"hod_arrivee_theorique\":21900,\"hod_attente_theorique\":0,\"hod_rang\":18,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":42,\"pnt_idu\":66,\"pnt_nom\":\"Schweighouse Gare SNCF\",\"grp_nom\":\"SCH1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":66660,\"hod_attente\":0,\"hod_arrivee_theorique\":22620,\"hod_attente_theorique\":0,\"hod_rang\":19,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":43,\"pnt_idu\":67,\"pnt_nom\":\"Schweighouse Au Faubourg\",\"grp_nom\":\"SCHW\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":66540,\"hod_attente\":0,\"hod_arrivee_theorique\":22500,\"hod_attente_theorique\":0,\"hod_rang\":20,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":44,\"pnt_idu\":68,\"pnt_nom\":\"Haguenau Gare SNCF\",\"grp_nom\":\"HAGU\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":67020,\"hod_attente\":0,\"hod_arrivee_theorique\":22980,\"hod_attente_theorique\":0,\"hod_rang\":21,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",}],\"class\":\"class org.avm.business.tft.model.COURSE\",},\"messages\":[],\"class\":\"class org.avm.business.tft.model.SERVICE\",}", //$NON-NLS-1$
			"{\"jex_date\":{\"time\":1244482217837,\"class\":\"class java.util.Date\",},\"statut\":8,\"course\":{\"crs_id\":59,\"crs_idu\":5,\"crs_nom\":null,\"crs_depart\":18900,\"lgn_idu\":0,\"lgn_nom\":\"1\",\"lgn_amplitude\":180,\"lgn_chevauchement\":60,\"crd_statut\":0,\"points\":[{\"pnt_id\":24,\"pnt_idu\":46,\"pnt_nom\":\"Bitche Gare SNCF\",\"grp_nom\":\"BIT1\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":223,\"hod_arrivee\":62940,\"hod_attente\":0,\"hod_arrivee_theorique\":18900,\"hod_attente_theorique\":0,\"hod_rang\":1,\"hod_statut\":1,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":25,\"pnt_idu\":47,\"pnt_nom\":\"Bitche Actiparc\",\"grp_nom\":\"BITC\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":63120,\"hod_attente\":0,\"hod_arrivee_theorique\":19080,\"hod_attente_theorique\":0,\"hod_rang\":2,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":26,\"pnt_idu\":48,\"pnt_nom\":\"Eguelshardt Abri\",\"grp_nom\":\"EGUE\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":63480,\"hod_attente\":0,\"hod_arrivee_theorique\":19440,\"hod_attente_theorique\":0,\"hod_rang\":3,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":27,\"pnt_idu\":49,\"pnt_nom\":\"Bannstein rue de la Gare\",\"grp_nom\":\"BANN\",\"pnt_x\":7.0,\"pnt_y\":49.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":63660,\"hod_attente\":0,\"hod_arrivee_theorique\":19620,\"hod_attente_theorique\":0,\"hod_rang\":4,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":28,\"pnt_idu\":50,\"pnt_nom\":\"Philippsbourg Lieschbach\",\"grp_nom\":\"PHI1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":63960,\"hod_attente\":0,\"hod_arrivee_theorique\":19920,\"hod_attente_theorique\":0,\"hod_rang\":5,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":29,\"pnt_idu\":51,\"pnt_nom\":\"Philippsbourg Falkenstein\",\"grp_nom\":\"PHIL\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64080,\"hod_attente\":0,\"hod_arrivee_theorique\":20040,\"hod_attente_theorique\":0,\"hod_rang\":6,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":30,\"pnt_idu\":52,\"pnt_nom\":\"Niederbronn Usine\",\"grp_nom\":\"NIE5\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64500,\"hod_attente\":0,\"hod_arrivee_theorique\":20460,\"hod_attente_theorique\":0,\"hod_rang\":7,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":31,\"pnt_idu\":54,\"pnt_nom\":\"Niederbronn Gare SNCF\",\"grp_nom\":\"NIE8\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64620,\"hod_attente\":0,\"hod_arrivee_theorique\":20580,\"hod_attente_theorique\":0,\"hod_rang\":8,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":32,\"pnt_idu\":55,\"pnt_nom\":\"Niederbronn Hôtel de ville\",\"grp_nom\":\"NIE1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64740,\"hod_attente\":0,\"hod_arrivee_theorique\":20700,\"hod_attente_theorique\":0,\"hod_rang\":9,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":33,\"pnt_idu\":56,\"pnt_nom\":\"Niederbronn Archéologie\",\"grp_nom\":\"NIED\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":64860,\"hod_attente\":0,\"hod_arrivee_theorique\":20820,\"hod_attente_theorique\":0,\"hod_rang\":10,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":34,\"pnt_idu\":57,\"pnt_nom\":\"Reichshoffen Gare SNCF\",\"grp_nom\":\"REI3\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65100,\"hod_attente\":0,\"hod_arrivee_theorique\":21060,\"hod_attente_theorique\":0,\"hod_rang\":11,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":35,\"pnt_idu\":58,\"pnt_nom\":\"Reichshoffen Usine\",\"grp_nom\":\"REI1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65280,\"hod_attente\":0,\"hod_arrivee_theorique\":21240,\"hod_attente_theorique\":0,\"hod_rang\":12,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":36,\"pnt_idu\":59,\"pnt_nom\":\"Reichshoffen au Sapin\",\"grp_nom\":\"REIC\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65340,\"hod_attente\":0,\"hod_arrivee_theorique\":21300,\"hod_attente_theorique\":0,\"hod_rang\":13,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":37,\"pnt_idu\":60,\"pnt_nom\":\"Gundershoffen au Cygne\",\"grp_nom\":\"GUN1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65460,\"hod_attente\":0,\"hod_arrivee_theorique\":21420,\"hod_attente_theorique\":0,\"hod_rang\":14,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":38,\"pnt_idu\":62,\"pnt_nom\":\"Gundershoffen Gare SNCF\",\"grp_nom\":\"GUN4\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65520,\"hod_attente\":0,\"hod_arrivee_theorique\":21480,\"hod_attente_theorique\":0,\"hod_rang\":15,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":39,\"pnt_idu\":63,\"pnt_nom\":\"Griesbach Moulin\",\"grp_nom\":\"GRIE\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65700,\"hod_attente\":0,\"hod_arrivee_theorique\":21660,\"hod_attente_theorique\":0,\"hod_rang\":16,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":40,\"pnt_idu\":64,\"pnt_nom\":\"Mertzwiller Gare SNCF\",\"grp_nom\":\"MER1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65880,\"hod_attente\":0,\"hod_arrivee_theorique\":21840,\"hod_attente_theorique\":0,\"hod_rang\":17,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":41,\"pnt_idu\":65,\"pnt_nom\":\"Mertzwiller Monuments\",\"grp_nom\":\"MERT\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":65940,\"hod_attente\":0,\"hod_arrivee_theorique\":21900,\"hod_attente_theorique\":0,\"hod_rang\":18,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":42,\"pnt_idu\":66,\"pnt_nom\":\"Schweighouse Gare SNCF\",\"grp_nom\":\"SCH1\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":66660,\"hod_attente\":0,\"hod_arrivee_theorique\":22620,\"hod_attente_theorique\":0,\"hod_rang\":19,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":43,\"pnt_idu\":67,\"pnt_nom\":\"Schweighouse Au Faubourg\",\"grp_nom\":\"SCHW\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":66540,\"hod_attente\":0,\"hod_arrivee_theorique\":22500,\"hod_attente_theorique\":0,\"hod_rang\":20,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",},{\"pnt_id\":44,\"pnt_idu\":68,\"pnt_nom\":\"Haguenau Gare SNCF\",\"grp_nom\":\"HAGU\",\"pnt_x\":7.0,\"pnt_y\":48.0,\"psi_distance\":0,\"psp_gir\":0,\"hod_arrivee\":67020,\"hod_attente\":0,\"hod_arrivee_theorique\":22980,\"hod_attente_theorique\":0,\"hod_rang\":21,\"hod_statut\":0,\"class\":\"class org.avm.business.tft.model.POINT\",}],\"class\":\"class org.avm.business.tft.model.COURSE\",},\"messages\":[],\"class\":\"class org.avm.business.tft.model.SERVICE\",}" //$NON-NLS-1$
	};

	private static final String TftURL = "/tft/tft.do"; //$NON-NLS-1$

	private static final int TIMEOUT = 61000;// cote serveur on attend 59 sec avant de debloquer le thread
	// le present timer ne devrait donc jamais peter (sauf pb reseau).

	private final TftTimer _tftTimer;
	private TftRequestCallback _callback = new TftRequestCallback();

	private boolean _testJSON = false;
	private JSONTester _jsonTester;
	private final Site _site;
	public static boolean DEBUG;

	private static boolean ALERT_ON_ERROR = false;

	public static void debug(final String msg) {
		if (DEBUG) {
			Window.alert(msg);
		}
	}

	public static void error(final String msg) {
		if (ALERT_ON_ERROR) {
			Window.alert(msg);
		}
	}

	/*
	 * Permet de récupérer la valeur de "l'URLParameter" fourni Exemple :
	 * http://mamachine/servlet?param1=titi&param2=toto getParameter("param1")
	 * -> "titi"
	 */
	private String getParameter(final String urlparams, final String name) {
		final String[] ray = urlparams.substring(1, urlparams.length()).split(
				"&"); //$NON-NLS-1$
		for (int i = 0; i < ray.length; i++) {
			final String[] substrRay = ray[i].split("="); //$NON-NLS-1$
			final String key = substrRay[0];
			final String val = substrRay[1];
			if (key.equals(name)) {
				return val;
			}
		}
		return null;
	}

	public ExchangeControler(final String urlparams) {
		GWT.log("create object 'site'", null); //$NON-NLS-1$
		_site = Site.getInstance();
		_tftTimer = new TftTimer();
		_callback = new TftRequestCallback();

		final String testmode = getParameter(urlparams, "mode"); //$NON-NLS-1$
		if (testmode != null) {
			if (testmode.equals("standalone")) { //$NON-NLS-1$
				Window.alert("Test mode !"); //$NON-NLS-1$
				_testJSON = true;
			} else if (testmode.equals("debug")) { //$NON-NLS-1$
				Window.alert("debug mode !"); //$NON-NLS-1$
				DEBUG = true;
				ALERT_ON_ERROR = true;
			} else if (testmode.equals("validation")) { //$NON-NLS-1$
				Window.alert("validation mode !"); //$NON-NLS-1$
				DEBUG = false;
				ALERT_ON_ERROR = true;
			}
		}

		if (_testJSON) {
			_jsonTester = new JSONTester();
		} else {
			ask4model(true);
		}
	}

	private void ask4model(final boolean isFirstTime) {
		try {
			final String url = TftURL + "?init=" //$NON-NLS-1$
					+ new Boolean(isFirstTime).toString();
			final RequestBuilder builder = new RequestBuilder(
					RequestBuilder.GET, url);
			builder.setTimeoutMillis(TIMEOUT);
			builder.sendRequest(null, _callback);
		} catch (final RequestException e) {
			logerror(e.getMessage());
			e.printStackTrace(System.err);
			ExchangeControler.error("Erreur sur ask4model " + e.getMessage()); //$NON-NLS-1$
		}
	}

	private void refreshTft(final Object result) {
		_tftTimer.cancel();
		try {
			final JSONObject model = JSONParser.parse((String) result)
					.isObject();
			// debug("refresh Tft" + result);
			_site.setData(model);
		} catch (final RuntimeException e) {
			logerror(e.getMessage());
			System.err.println("Erreur manageModel"); //$NON-NLS-1$
			debug("Erreur parse JSON " + result + "\n ERROR:" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			e.printStackTrace(System.err);
		}
		ask4model(false);
		return;
	}

	public static void logerror(final String text) {
		final Element element = RootPanel.get("error").getElement(); //$NON-NLS-1$

		if (element != null) {
			String txt = ""; //$NON-NLS-1$
			if (ERRCOUNT < 20) {
				txt = DOM.getInnerHTML(element);
			} else {
				ERRCOUNT = 0;
			}
			ERRCOUNT++;
			txt += "<br/>\n"; //$NON-NLS-1$
			txt += "ERR" + ERRCOUNT + ":"; //$NON-NLS-1$ //$NON-NLS-2$
			txt += text;
			DOM.setInnerHTML(element, txt);

		}
	}

	private class TftRequestCallback implements RequestCallback {
		@Override
		public void onError(final Request request, final Throwable arg1) {
			System.err.println("Erreur sur la requete " + request.toString()); //$NON-NLS-1$
			arg1.printStackTrace(System.err);
			logerror(arg1.getMessage());
			if (!DEBUG) {
				home();
			}
		}

		@Override
		public void onResponseReceived(final Request request,
				final Response response) {
			if (_testJSON) {
				System.out
						.println("Traitement " + JSONs[_jsonTester.getCmpt()]); //$NON-NLS-1$
				final JSONObject model = JSONParser.parse(
						(String) JSONs[_jsonTester.getCmpt()]).isObject();

				_site.setData(model);
				return;
			}
			if (response.getStatusCode() == STATUS_CODE_OK) {
				refreshTft(response.getText());
				return;
			}
			_tftTimer.schedule(TIMEOUT);
		}

	}

	public static native void reload(String url)/*-{
		$wnd.location = url;
	}-*/;

	public static native void homeFF()/*-{
		$wnd.home();
	}-*/;

	public static native void homeWK()/*-{
		browser.home();
	}-*/;

	public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;

	public static void home() {
		if (getUserAgent().contains("firefox")) {
			homeFF();
		} else {
			homeWK();
		}
	}

	private class TftTimer extends Timer {
		@Override
		public void run() {
			// ask4model(true);
			logerror("timer : home!"); //$NON-NLS-1$
			if (!DEBUG) {
				home();
			}
			// ExchangeControler.reload(bootstrapURL);
		}
	}

	private class JSONTester extends Timer {
		public int cmpt = 0;
		boolean isFirstTime = true;

		public JSONTester() {
			scheduleRepeating(15000);
		}

		public int getCmpt() {
			return cmpt;
		}

		@Override
		public void run() {
			try {
				final JSONObject model = JSONParser.parse((String) JSONs[cmpt])
						.isObject();

				_site.setData(model);
				// ask4model(isFirstTime);
				isFirstTime = false;
			} catch (final Exception e) {
				e.printStackTrace(System.err);
			}
			cmpt++;
			if (cmpt >= JSONs.length) {
				cmpt = 0;
			}
		}
	}

}
