package org.avm.elementary.can.parser;

import java.io.IOException;
import java.util.Iterator;

public abstract class SPN {

//	public boolean available = true;
	public boolean valid = true;
	public double value = 0d;

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getType();

	public abstract String getUnit();

	public abstract void get(Bitstream bs) throws IOException;

	public abstract void put(Bitstream bs) throws IOException;

//	public boolean isAvailable() {
//		return available;
//	}

//	public void setAvailable(boolean available) {
//		this.available = available;
//	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SPN other = (SPN) obj;
//		if (available != other.available)
//			return false;
		if (valid != other.valid)
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))		
			return false;
		return true;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("\"");
		result.append(getName());
		result.append("\":");
		result.append(getValue());
		return result.toString();
	}

}
