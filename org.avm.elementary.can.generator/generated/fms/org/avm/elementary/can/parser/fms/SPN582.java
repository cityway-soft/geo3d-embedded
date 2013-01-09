package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.SPN;

public class SPN582 extends SPN {

    public static final String name = "SPN582";
	public static final String description = "Axle Weight";
	public static final String type = "Mass_Cargo";
	public static final String unit = "kg";
	public static final double min = 0.0;
	public static final double max = 32127.5;
	public static final double scale = 0.5;
	public static final double offset = 0.0;


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
    	long v = bs.little_getbits(56, 16);
		value = scale * v + offset;
		valid = !(value < min || value > max);

    }

    public void put(Bitstream bs) throws IOException {
    	long v = (long) ((value - offset) / scale);
		bs.little_setbits(v, 56, 16);

    }
}

