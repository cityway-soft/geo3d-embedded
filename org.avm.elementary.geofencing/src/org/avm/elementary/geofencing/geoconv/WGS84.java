package org.avm.elementary.geofencing.geoconv;

/**
 * Classe permettant de traiter les conversions Wgs84 => Lambert 2
 * 
 * @see stacad.common.geoconv.LambertIIe
 */
public class WGS84 {
	private double _lambda;
	private double _phi;

	public double getLongitude() {
		return _lambda;
	}

	public double getLatitude() {
		return _phi;
	}

	/**
	 * constructeur
	 * 
	 * @param longitude
	 * @param latitude
	 */
	public WGS84(double longitude, double latitude) {
		_lambda = longitude;
		_phi = latitude;
	}

	public String toString() {
		return "lambda=" + _lambda + "; phi=" + _phi;
	}

	/**
	 * permet de convertir des coordonn�es exprim�es en Wgs84 en coordonn�es
	 * Lambert 2
	 */
	public LambertIIe convert2Lambert2() throws Exception {
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
		/* WGS84 -> CLARKE 1880 IGN */
		tx = 168.0;
		ty = 60.0;
		tz = -320.0;
		da = 112.2;
		df = 5.4738855e-5;
		/* lambda,phi -> x,y (NTF) */
		phi_o = 52.0;
		k_o = 0.99987742;
		cx = 600000.0;
		cy = 2200000.0;
		/** ********************************* */
		/* changement de syst�me g�od�sique */
		/** ********************************* */
		WGS84 coordWgs84 = Geoconv.molodenski_simple(this, agrs, bgrs, tx, ty,
				tz, da, df);
		/** *********** */
		/* projection */
		/** *********** */
		LambertIIe coordLambert2 = projection_lcc_geo_plan(coordWgs84, phi_o,
				k_o, cx, cy, aclk, bclk);
		return coordLambert2;
	}

	/***************************************************************************
	 * projection_lcc_geo_plan() *
	 * ***************************************************************************
	 * But : * Application de la projection lambert conique conforme sur des *
	 * coordonn�es Lambda, Phi afin d'obtenier les coordonn�es x,y * Source de
	 * la formule : F. Duquenne - IGN * Param�tres : lambda,phi coordonn�es
	 * sources (en dg) * x,y coordonn�es projet�es (en m) * phi_o,k_o origine
	 * (gr) + echelle * cx,cy d�calage � l'origine (en m) * a,b param�tre de
	 * l'ellipso�de (en m) * Remarque : Fonction non r�versible *
	 **************************************************************************/
	private LambertIIe projection_lcc_geo_plan(WGS84 coordWgs83, double phi_o,
			double k_o, double cx, double cy, double a, double b)
			throws Exception {
		double gama;
		double l;
		double l_o;
		double e;
		double tmp1, tmp2;
		double r_o, r;
		double lambda_r;
		double phi_r;
		double phi_o_r;
		/** ****************************** */
		/* d�calage du m�ridien de paris */
		/** ****************************** */
		double lambda = coordWgs83.getLongitude() - Geoconv.PARIS;
		/** ********************** */
		/* convrersion dg -> rad */
		/** ********************** */
		lambda_r = (lambda / 180.0) * Math.PI;
		phi_r = (coordWgs83.getLatitude() / 180.0) * Math.PI;
		/** ********************* */
		/* conversion gr -> rad */
		/** ********************* */
		phi_o_r = (phi_o / 200.0) * Math.PI;
		/** ******** */
		/* Calculs */
		/** ******** */
		e = Math.sqrt((Math.pow(a, 2.0) - Math.pow(b, 2.0)) / Math.pow(a, 2.0));
		gama = lambda_r * Math.sin(phi_o_r);
		tmp1 = Math.tan((phi_r / 2.0) + (Math.PI / 4.0));
		if (tmp1 <= 0.0)
			throw new Exception("projection_lcc_geo_plan exception");
		tmp2 = (1.0 - (e * Math.sin(phi_r))) / (1.0 + (e * Math.sin(phi_r)));
		if (tmp2 <= 0.0)
			throw new Exception("projection_lcc_geo_plan exception");
		l = Math.log(tmp1) + ((e / 2.0) * Math.log(tmp2));
		tmp1 = Math.tan((phi_o_r / 2.0) + (Math.PI / 4.0));
		if (tmp1 <= 0.0)
			throw new Exception("projection_lcc_geo_plan exception");
		tmp2 = (1.0 - (e * Math.sin(phi_o_r)))
				/ (1.0 + (e * Math.sin(phi_o_r)));
		if (tmp2 <= 0.0)
			throw new Exception("projection_lcc_geo_plan exception");
		l_o = Math.log(tmp1) + ((e / 2.0) * Math.log(tmp2));
		r_o = k_o
				* a
				* Math.pow(1.0 - (Math.pow(e, 2.0) * Math.pow(
						Math.sin(phi_o_r), 2.0)), -0.5)
				* (1.0 / Math.tan(phi_o_r));
		r = r_o * Math.exp((-Math.sin(phi_o_r)) * (l - l_o));
		double x = (r * Math.sin(gama)) + cx;
		double y = (r_o - (r * Math.cos(gama))) + cy;
		return new LambertIIe(x, y);
	} /* projection_lcc_geo_plan() */
}// fin Wgs84
/*******************************************************************************
 *********************************************************************************/
