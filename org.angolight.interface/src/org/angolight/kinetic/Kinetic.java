package org.angolight.kinetic;

import org.apache.commons.pool.ObjectPool;

public class Kinetic {

	protected int _counter;
	protected ObjectPool _pool;

	protected double _speed;
	protected double _acceleration;
	protected double _period;
	protected double _odometer;

	public Kinetic() {
		_speed = 0d;
		_acceleration = 0d;
	}

	public Kinetic(double speed, double acceleration) {
		_speed = speed;
		_acceleration = acceleration;
	}

	public void dispose() {
		try {
			_counter--;
			if (_pool != null && _counter == 0)
				_pool.returnObject(this);
		} catch (Exception e) {

		}
	}

	public double getSpeed() {
		return _speed;
	}

	public void setSpeed(double speed) {
		_speed = speed;
	}

	public double getAcceleration() {
		return _acceleration;
	}

	public double get0dometer() {
		return _odometer;
	}

	public void setOdometer(double odometer) {
		_odometer = odometer;
	}

	public double getPeriod() {
		return _period;
	}

	public void setPeriod(double period) {
		_period = period;
	}

	public void setAcceleration(double acceleration) {
		_acceleration = acceleration;
	}

	public void setPool(ObjectPool pool) {
		_pool = pool;
	}

	public int getCounter() {
		return _counter;
	}

	public void setCounter(int counter) {
		_counter = counter;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("\"{speed\":");
		result.append(_speed);
		result.append("\",acceleration\":");
		result.append(_acceleration);
		result.append("\",odometer\":");
		result.append(_odometer);
		result.append("\",period\":");
		result.append(_period);
		result.append("}");
		return result.toString();
	}


}
