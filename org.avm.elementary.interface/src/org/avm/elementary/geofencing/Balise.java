package org.avm.elementary.geofencing;

public class Balise {
	private int _id;
	private boolean _inside;
	private byte[] _data;

	public Balise(int id, boolean inside, byte[] data) {
		super();
		_id = id;
		_inside = inside;
		_data = data;
	}

	public Balise(int id, boolean inside) {
		this(id, inside, null);
	}

	public boolean isInside() {
		return _inside;
	}

	public boolean isOutside() {
		return (!_inside);
	}

	public int getId() {
		return _id;
	}

	public String toString() {
		return (_inside ? "Entree " : "Sortie ") + "Balise #" + _id;
	}

	public byte[] getData() {
		return _data;
	}

	public void setInside(boolean inside) {
		_inside = inside;
	}

	public int hashCode() {
		return _id;
	}

	public boolean equals(Object obj) {
		return (hashCode() == obj.hashCode());
	}
}
