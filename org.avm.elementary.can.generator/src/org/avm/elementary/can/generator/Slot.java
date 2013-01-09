package org.avm.elementary.can.generator;

public class Slot {
	public static final String BYTE = "byte";

	public static final String BIT = "bit";

	private String name;

	private String type;

	private String unit;

	private double scaling;

	private double offset;

	private double rangeMin;

	private double rangeMax;

	private String valType;

	private int length;

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public double getRangeMax() {
		return rangeMax;
	}

	public void setRangeMax(double rangeMax) {
		this.rangeMax = rangeMax;
	}

	public double getRangeMin() {
		return rangeMin;
	}

	public void setRangeMin(double rangeMin) {
		this.rangeMin = rangeMin;
	}

	public double getScaling() {
		return scaling;
	}

	public void setScaling(double scaling) {
		this.scaling = scaling;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getValType() {
		return valType;
	}

	public void setValType(String valType) {
		this.valType = valType;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		buffer.append(" name:" + name);
		buffer.append(" type:" + type);
		buffer.append(" unit:" + unit);
		buffer.append(" scaling:" + scaling);
		buffer.append(" offset:" + offset);
		buffer.append(" range-min:" + rangeMin);
		buffer.append(" range-max:" + rangeMax);
		buffer.append(" val-type:" + valType);
		buffer.append(" lenght:" + length);
		buffer.append("}");
		return buffer.toString();
	}
}
