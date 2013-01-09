/**
 * @(#) Geoconv.java	 13/11/00
 * Copyright :	  Copyright EUROLUM (c) 2000
 * @Titre :	  GeoConv
 * @Version :	  1.0
 * @Auteur :	  Stacad Team
 * @Soci�t� :	  Eurolum
 * @Description : 
 * ***************************************************************************
 * Historique des modifications du pr�sent fichier:
 * 13/11/2000 : Cr�ation						  V1.0
 */
package org.avm.elementary.geofencing.geoconv;

/**
 * classe utilis�e par Lambert2 et Wgs84
 * 
 * @see stacad.common.geoconv.LambertIIe
 * @see stacad.common.geoconv.WGS84
 */
public class Geoconv {
	/**
	 * Constantes locales
	 */
	public static final double CSTMOLO = 206265.0;
	public static final double PARIS = 2.337229167;

	/**
	 * molodenski_simple() But : Application de la formule de Molodenski
	 * simplifi�e sur des coordonn�es Lambda, Phi afin d'effectuer un changement
	 * d'ellipso�de Source de la formule : F. Duquenne - IGN Param�tres :
	 * Lambda, Phi origine du point (en dg) a,b param�tres de l'ellipso�de
	 * d'origine (en m) tx,ty,tz Dif. d'origine des ellipso�des (translation)
	 * da, df Dif. des param�tres des ellipso�des Remarque : Fonction r�versible
	 */
	public static WGS84 molodenski_simple(WGS84 coordWgs84, double a, double b,
			double tx, double ty, double tz, double da, double df)
			throws Exception {
		// -- d�claration et initialisation des variables
		double lambda_r;
		double phi_r;
		double dlambda;
		double dphi;
		double f;
		double e;
		double n, r;
		double tmp;
		// -- convrersion dg -> rad
		lambda_r = (coordWgs84.getLongitude() / 180.0) * Math.PI;
		phi_r = (coordWgs84.getLatitude() / 180.0) * Math.PI;
		// -- Calculs
		f = (a - b) / a;
		e = Math.sqrt((Math.pow(a, 2.0) - Math.pow(b, 2.0)) / Math.pow(a, 2.0));
		tmp = Math.pow(
				1.0 - (Math.pow(e, 2.0) * Math.pow(Math.sin(phi_r), 2.0)), 0.5);
		if (tmp == 0.0)
			throw new Exception("molodenski simple exception");
		n = a / tmp;
		tmp = Math.pow(
				1.0 - (Math.pow(e, 2.0) * Math.pow(Math.sin(phi_r), 2.0)), 1.5);
		if (tmp == 0.0)
			throw new Exception("molodenski simple exception");
		r = (a * (1 - Math.pow(e, 2.0))) / tmp;
		tmp = n * Math.cos(phi_r);
		if (tmp == 0.0)
			throw new Exception("molodenski simple exception");
		dlambda = (CSTMOLO / tmp)
				* ((-tx * Math.sin(lambda_r)) + (ty * Math.cos(lambda_r)));
		if (r == 0.0)
			throw new Exception("molodenski simple exception");
		dphi = (CSTMOLO / r)
				* ((-tx * Math.sin(phi_r) * Math.cos(lambda_r))
						- (ty * Math.sin(phi_r) * Math.sin(lambda_r))
						+ (tz * Math.cos(phi_r)) + (((a * df) + (f * da)) * Math
						.sin(2 * phi_r)));
		// -- Conversion en degr�s d�cimaux
		double newLambda = coordWgs84.getLongitude() + (dlambda / 3600.0);
		double newPhi = coordWgs84.getLatitude() + (dphi / 3600.0);
		return new WGS84(newLambda, newPhi);
	} /* molodenski() */

	/**
	 * prj_lcc_plan_geo_calcul_phi() Fonction interne de calcul (recursif)
	 */
	public static double prj_lcc_plan_geo_calcul_phi(double phi_prec, double e,
			double l) {
		double phi;
		phi = (2.0 * Math.atan((Math
				.pow(((1 + (e * Math.sin(phi_prec))) / (1 - (e * Math
						.sin(phi_prec)))), e / 2.0) * Math.exp(l))))
				- (Math.PI / 2.0);
		if (Math.abs(phi - phi_prec) > 10e-6)
			phi = prj_lcc_plan_geo_calcul_phi(phi, e, l);
		return (phi);
	} /* prj_lcc_plan_geo_calcul_phi() */
}// fin Geoconv
/*******************************************************************************
 *********************************************************************************/
