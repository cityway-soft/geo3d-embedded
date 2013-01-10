package org.avm.elementary.can.generator;

import java.math.BigDecimal;
import java.text.ParseException;

public class Main {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {	
		double v = 1829339222 *  0.0000001 -210;
		System.out.println((float)v);
		
		// double v = 1829339222.0 / 10000000.0 - 210.0;
		java.text.DecimalFormat df = new
		java.text.DecimalFormat("###.########");
		System.out.println(df.format(v));
		double v2  =new BigDecimal(v).doubleValue();
		System.out.println(v2);
		double val =new BigDecimal(1829339222).multiply(new BigDecimal( 0.0000001)).add(new BigDecimal(-210)).doubleValue();
		System.out.println(val);

		System.out.println(Double.doubleToLongBits(-27.066077800000016));
		System.out.println(Double.doubleToLongBits(-27.0660778));
	}

}
