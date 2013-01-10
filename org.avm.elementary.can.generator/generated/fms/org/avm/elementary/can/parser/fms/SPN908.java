package org.avm.elementary.can.parser.fms;

import java.io.IOException;

import org.avm.elementary.can.parser.Bitstream;
import org.avm.elementary.can.parser.SPN;

public class SPN908 extends SPN {

    public static final String name = "SPN908";
	public static final String description = "Relative Speed, Rear Axle #1, Right Wheel";
	public static final String type = "Velocity_Linear";
	public static final String unit = "kph";
	public static final double min = -7.8125;
	public static final double max = 7.8125;
	public static final double scale = 0.0625;
	public static final double offset = -7.8125;


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
    	long v = bs.little_getbits(88, 8);
		value = scale * v + offset;
		valid = !(value < min || value > max);

    }

    public void put(Bitstream bs) throws IOException {
    	long v = (long) ((value - offset) / scale);
		bs.little_setbits(v, 88, 8);

    }
}

