package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.SPN;

public class SPN174 extends SPN {

    public static final String name = "SPN174";
	public static final String description = "Engine Fuel Temperature";
	public static final String type = "Temperature";
	public static final String unit = "C";
	public static final double min = -40.0;
	public static final double max = 210.0;
	public static final double scale = 1.0;
	public static final double offset = -40.0;


	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getUnit() {
		return unit;
	}
	
	public static double getMin() {
		return min;
	}

	public static double getMax() {
		return max;
	}

	public static double getScale() {
		return scale;
	}

	public static double getOffset() {
		return offset;
	}
	
    public void get(Bitstream bs) throws IOException {              
    	long v = bs.getbits(56, 8);
		value = scale * v + offset;
		valid = !(value < min || value > max);

    }

    public void put(Bitstream bs) throws IOException {
    	long v = (long) ((value - offset) / scale);
		bs.setbits(v, 56, 8);

    }
}

