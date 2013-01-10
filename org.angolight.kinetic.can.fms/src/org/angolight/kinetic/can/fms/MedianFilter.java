package org.angolight.kinetic.can.fms;

public class MedianFilter {
	protected double[] _medians;
	protected double[] _values;
	protected int _index = 0;
	protected int _length = 0;

	public MedianFilter(int size) {
		_values = new double[size];
		_medians = new double[_values.length];
	}

	public double getValue() {
		System.arraycopy(_values, 0, _medians, 0, _medians.length);
		java.util.Arrays.sort(_medians);

		double value;
		if (_length % 2 == 1) {
			value = _medians[_length / 2];
		} else {
			double value1 = _medians[_length / 2];
			double value2 = _medians[_length / 2 - 1];
			value = (value1 + value2) / 2;
		}
		return value;
	}

	public void update(double value) {
		if (_index >= _values.length)
			_index = 0;
		_values[_index] = value;
		_index++;
		if (_length < _values.length) {
			_length = _index;
		}
	}

}
