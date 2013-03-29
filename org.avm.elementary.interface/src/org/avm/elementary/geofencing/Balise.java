package org.avm.elementary.geofencing;

import java.io.Serializable;

public class Balise implements Serializable {

	private static final long serialVersionUID = 1L;

	private int _id;
	private boolean _inside;
	private byte[] _data;

	private double _longitude;
	private double _latitude;
	private double _offset = 35; // metres

	public Balise(int id, boolean inside) {
		this(id, inside, null, 0, 0, 35);
	}

	public Balise(int id, boolean inside, byte[] data) {
		this(id, inside, data, 0, 0, 35);
	}

	public Balise(int id, boolean inside, byte[] data, double longitude,
			double latitude, double offset) {
		super();
		_id = id;
		_inside = inside;
		_data = data;
		_longitude = longitude;
		_latitude = latitude;
		_offset = offset;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public boolean isInside() {
		return _inside;
	}

	public void setInside(boolean inside) {
		_inside = inside;
	}

	public byte[] getData() {
		return _data;
	}

	public void setData(byte[] data) {
		_data = data;
	}

	public double getLongitude() {
		return _longitude;
	}

	public void setLongitude(double longitude) {
		_longitude = longitude;
	}

	public double getLatitude() {
		return _latitude;
	}

	public void setLatitude(double latitude) {
		_latitude = latitude;
	}

	public double getOffset() {
		return _offset;
	}

	public void setOffset(double offset) {
		_offset = offset;
	}

	public String toString() {
		return (_inside ? "Entree " : "Sortie ") + "Balise #" + _id;
	}

	public int hashCode() {
		return _id;
	}

	public boolean equals(Object obj) {
		return (hashCode() == obj.hashCode());
	}

}
