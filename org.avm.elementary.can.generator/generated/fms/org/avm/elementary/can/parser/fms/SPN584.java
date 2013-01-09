package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.SPN;

public class SPN584 extends SPN {

    public static final String name = "SPN584";
	public static final String description = "Latitude";
	public static final String type = "Angle Direction";
	public static final String unit = "deg";
	public static final double min = -210.0;
	public static final double max = 211.1008122;
	public static final double scale = 0.0000001;
	public static final double offset = -210.0;


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
    	long v = bs.getbits(48, 32);
		value = scale * v + offset;
		valid = !(value < min || value > max);

    }

    public void put(Bitstream bs) throws IOException {
    	long v = (long) ((value - offset) / scale);
		bs.setbits(v, 48, 32);

    }
}

