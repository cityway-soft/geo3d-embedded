package org.angolight.indicator;

public class Measure implements Comparable {
	double value;
	long time;
	final Unit unit;
	private final String name;

	public Measure(String name, Unit unit, double value, long time) {
		this.name = name;
		this.unit = (unit != null) ? unit : Unit.unity;
		this.value = value;
		this.time = time;
	}

	public Measure(Measure measure) {
		this(measure.name, measure.unit, measure.value, measure.time);
	}

	public Measure(String name, Unit unit, double value) {
		this(name, unit, value, System.currentTimeMillis());
	}

	public Measure(String name, Unit unit) {
		this(name, unit, 0d, 0l);
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Unit getUnit() {
		return unit;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Measure add(Measure measure) {
		this.value += measure.value;
		return this;
	}

	public Measure add(double d) {
		this.value += d;
		return this;
	}

	public Measure sub(Measure measure) {
		this.value -= measure.value;
		return this;
	}

	public Measure sub(double d) {
		this.value -= d;
		return this;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(value);
		String u = unit.toString();
		if (u.length() > 0) {
			sb.append(" ");
			sb.append(u);
		}
		return sb.toString();
	}

	public int compareTo(Object obj) {
		if (this == obj) {
			return 0;
		}
		Measure that = (Measure) obj;
		if (!unit.equals(that.unit)) {
			throw new ArithmeticException("Cannot compare " + this + " and "
					+ that);
		}
		if (value == that.value) {
			return 0;
		}
		return (value < that.value) ? -1 : 1;
	}

	public int hashCode() {
		long bits = Double.doubleToLongBits(value);
		return ((int) (bits ^ (bits >>> 32))) ^ unit.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Measure)) {
			return false;
		}
		Measure that = (Measure) obj;
		return (value == that.value) && unit.equals(that.unit);
	}
}