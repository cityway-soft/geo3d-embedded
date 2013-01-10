package org.avm.elementary.common;

public class IndexedPropertyChangeEvent extends PropertyChangeEvent {

	private int _index;

	public IndexedPropertyChangeEvent(Object source, String propertyName,
			Object oldValue, Object newValue, int index) {
		super(source, propertyName, oldValue, newValue);
		_index = index;
	}

	public int getIndex() {
		return _index;
	}
}
