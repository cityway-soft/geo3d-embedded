/*
 * Created on 11 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.avm.elementary.geofencing.geoconv;

/**
 * @author Administrateur
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Main {
	private void test(double x, double y, double dx, double dy) {
		double x1 = x / 3600000;
		double y1 = y / 3600000;
		double x2 = (x + dx) / 3600000;
		double y2 = (y + dy) / 3600000;
		WGS84 pw1 = new WGS84(x1, y1);
		LambertIIe pl1 = null;
		WGS84 pw2 = new WGS84(x2, y2);
		LambertIIe pl2 = null;
		try {
			pl1 = pw1.convert2Lambert2();
			pl2 = pw2.convert2Lambert2();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("--------------");
		System.out.println("pw1=" + pw1);
		System.out.println("pl1=" + pl1);
		System.out.println("--------------");
		System.out.println("pw2=" + pw2);
		System.out.println("pl2=" + pl2);
		System.out.println("--------------");
		System.out.println("dx=" + (pl2.getX() - pl1.getX()));
		System.out.println("dy=" + (pl2.getY() - pl1.getY()));
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.test((1.1 * 3600000), (49.03 * 3600000), 3600000, 3600000);
	}
	/*
	 * 
	 * public static void main(String[] args) { // WGS84 pw = new
	 * WGS84(48.874,2.295); WGS84 pw = new WGS84(2.295, 48.874); LambertIIe pl =
	 * null; try { pl = pw.convert2Lambert2(); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * System.out.println("pw=" + pw); System.out.println("pl=" + pl);
	 * 
	 * pl = new LambertIIe(596953.6670191097, 2430632.5139182517); try { pw =
	 * pl.convert2Wgs84(); } catch (Exception e1) { // TODO Auto-generated catch
	 * block e1.printStackTrace(); } System.out.println("--------------");
	 * System.out.println("pw=" + pw); System.out.println("pl=" + pl); }
	 */
}
