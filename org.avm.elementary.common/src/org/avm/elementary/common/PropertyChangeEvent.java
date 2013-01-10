package org.avm.elementary.common;

import java.util.EventObject;

public class PropertyChangeEvent extends EventObject {

	private String _propertyName;

	private Object _newValue;

	private Object _oldValue;

	public PropertyChangeEvent(Object source, String propertyName,
			Object oldValue, Object newValue) {
		super(source);
		_propertyName = propertyName;
		_newValue = newValue;
		_oldValue = oldValue;
	}

	public String getPropertyName() {
		return _propertyName;
	}

	public Object getNewValue() {
		return _newValue;
	}

	public Object getOldValue() {
		return _oldValue;
	}
}
