/**
 * @(#) Lambert2.java	 13/11/00
 * Copyright :	  Copyright EUROLUM (c) 2000
 * @Titre :	  Composant
 * @Version :	  1.0
 * @Auteur :	  Stacad Team
 * @Soci�t� :	  Eurolum
 * @Description :
 * ***************************************************************************
 * Historique des modifications du pr�sent fichier:
 * 13/11/2000 : Cr�ation						  V1.0
 */
package org.avm.device.generic.gps.geoconv;

/**
 * Classe permettant de traiter les conversions Wgs84 => Lambert 2
 * 
 * @see stacad.common.geoconv.WGS84
 */
public class LambertIIe {

	private double _x;
	private double _y;

	public double getX() {
		return _x;
	}

	public double getY() {
		return _y;
	}

	public LambertIIe(double x, double y) {
		_x = x;
		_y = y;
	}

	public String toString() {
		return "x=" + _x + "; y=" + _y;
	}

	/***************************************************************************
	 * convert2Wgs84() *
	 * ***************************************************************************
	 * But : * Projection d'une coordonn�e x,y de la repr�sentation Lambert
	 * France * (NTF) zone 2 vers une coordonn�e lambda,phi du syst�mes
	 * g�od�sique WGS84*
	 **************************************************************************/
	public WGS84 convert2Wgs84() throws Exception {
		double agrs, bgrs;
		double aclk, bclk;
		double tx, ty, tz;
		double da, df;
		double phi_o;
		double k_o;
		double cx, cy;
		/** ***************************** */
		/* initialisation des variables */
		/** ***************************** */
		/* GRS80 */
		agrs = 6378137.0;
		bgrs = 6356752.314;
		/* CLARKE 1880 IGN */
		aclk = 6378249.2;
		bclk = 6356515.0;
		/* CLARKE 1880 IGN -> WGS84 */
		tx = -168.0;
		ty = -60.0;
		tz = 320.0;
		da = -112.2;
		df = -5.4738855e-5;
		/* lambda,phi -> x,y (NTF) */
		phi_o = 52.0;
		k_o = 0.99987742;
		cx = 600000.0;
		cy = 2200000.0;
		/** ******************* */
		/* projection inverse */
		/** ******************* */
		WGS84 coordWgs84;
		coordWgs84 = projection_lcc_plan_geo(this, phi_o, k_o, cx, cy, aclk,
				bclk);
		/** ********************************* */
		/* changement de syst�me g�od�sique */
		/** ********************************* */
		return Geoconv.molodenski_simple(coordWgs84, aclk, bclk, tx, ty, tz,
				da, df);
	}

	/***************************************************************************
	 * projection_lcc_plan_geo()
	 * ************************************************************************
	 * But : <BR>
	 * "d�projection" de coordonn�es lambert conique conforme en Lambda,Phi <BR> �
	 * partir de coordonn�es x,y <BR>
	 * Source de la formule : F. Duquenne - IGN <BR>
	 * Param�tres : <BR>
	 * coordLambert2 coordonn�es projet�es (en m) <BR>
	 * phi_o,k_o origine (gr) + echelle <BR>
	 * cx,cy d�calage � l'origine (en m) <BR>
	 * a,b param�tre de l'ellipso�de (en m) <BR>
	 * Remarque : Fonction non r�versible
	 **************************************************************************/
	private WGS84 projection_lcc_plan_geo(LambertIIe coordLambert2,
			double phi_o, double k_o, double cx, double cy, double a, double b)
			throws Exception {
		double lambda_r;
		double phi_r;
		double phi_o_r;
		double gama;
		double tmp;
		double r_o;
		double e;
		double r;
		double l;
		double tmp1, tmp2;
		double l_o;
		/** ********************* */
		/* conversion gr -> rad */
		/** ********************* */
		phi_o_r = (phi_o / 200.0) * Math.PI;
		/** ******** */
		/* Calculs */
		/** ******** */
		e = Math.sqrt((Math.pow(a, 2.0) - Math.pow(b, 2.0)) / Math.pow(a, 2.0));
		r_o = k_o
				* a
				* Math.pow(1.0 - (Math.pow(e, 2.0) * Math.pow(
						Math.sin(phi_o_r), 2.0)), -0.5)
				* (1.0 / Math.tan(phi_o_r));
		tmp = (r_o - coordLambert2.getY()) + cy;
		if (tmp == 0.0)
			throw new Exception("projection_lcc_plan_geo Exception");
		gama = Math.atan((coordLambert2.getX() - cx) / tmp);
		lambda_r = gama / (Math.sin(phi_o_r));
		r = (coordLambert2.getX() - cx) / Math.sin(gama);
		tmp1 = Math.tan((phi_o_r / 2.0) + (Math.PI / 4.0));
		if (tmp1 <= 0.0)
			throw new Exception("projection_lcc_plan_geo Exception");
		tmp2 = (1.0 - (e * Math.sin(phi_o_r)))
				/ (1.0 + (e * Math.sin(phi_o_r)));
		if (tmp2 <= 0.0)
			throw new Exception("projection_lcc_plan_geo Exception");
		l_o = Math.log(tmp1) + ((e / 2.0) * Math.log(tmp2));
		l = l_o - ((1.0 / Math.sin(phi_o_r)) * Math.log(r / r_o));
		phi_r = (2.0 * Math.atan(Math.exp(l))) - (Math.PI / 2.0);
		phi_r = Geoconv.prj_lcc_plan_geo_calcul_phi(phi_r, e, l);
		/** ********************** */
		/* convrersion dg -> rad */
		/** ********************** */
		double newLambda = (lambda_r / Math.PI) * 180.0;
		double newPhi = (phi_r / Math.PI) * 180.0;
		/** ****************************** */
		/* d�calage du m�ridien de paris */
		/** ****************************** */
		newLambda += Geoconv.PARIS;
		return new WGS84(newLambda, newPhi);
	} /* projection_lcc_plan_geo() */
}// fin Geoconv
/*******************************************************************************
 *********************************************************************************/
