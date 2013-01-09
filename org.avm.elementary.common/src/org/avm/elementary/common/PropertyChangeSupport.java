package org.avm.elementary.common;

public class PropertyChangeSupport {

	transient private java.util.Vector _listeners;

	private java.util.Hashtable _children;

	private Object _source;

	public PropertyChangeSupport(Object source) {
		if (source == null) {
			throw new NullPointerException();
		}
		_source = source;
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if (_listeners == null) {
			_listeners = new java.util.Vector();
		}
		_listeners.addElement(listener);
	}

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if (_listeners == null) {
			return;
		}
		_listeners.removeElement(listener);
	}

	public synchronized void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		if (_children == null) {
			_children = new java.util.Hashtable();
		}
		PropertyChangeSupport child = (PropertyChangeSupport) _children
				.get(propertyName);
		if (child == null) {
			child = new PropertyChangeSupport(_source);
			_children.put(propertyName, child);
		}
		child.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		if (_children == null) {
			return;
		}
		PropertyChangeSupport child = (PropertyChangeSupport) _children
				.get(propertyName);
		if (child == null) {
			return;
		}
		child.removePropertyChangeListener(listener);
	}

	public void firePropertyChange(PropertyChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propertyName = evt.getPropertyName();
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}

		java.util.Vector targets = null;
		PropertyChangeSupport child = null;
		synchronized (this) {
			if (_listeners != null) {
				targets = (java.util.Vector) _listeners.clone();
			}
			if (_children != null && propertyName != null) {
				child = (PropertyChangeSupport) _children.get(propertyName);
			}
		}

		if (targets != null) {
			for (int i = 0; i < targets.size(); i++) {
				PropertyChangeListener target = (PropertyChangeListener) targets
						.elementAt(i);
				target.propertyChange(evt);
			}
		}
		if (child != null) {
			child.firePropertyChange(evt);
		}
	}

	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		firePropertyChange(new PropertyChangeEvent(_source, propertyName,
				oldValue, newValue));
	}

	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		if (oldValue == newValue) {
			return;
		}
		firePropertyChange(propertyName, new Integer(oldValue), new Integer(
				newValue));
	}

	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		if (oldValue == newValue) {
			return;
		}
		firePropertyChange(propertyName, new Boolean(oldValue), new Boolean(
				newValue));
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			Object oldValue, Object newValue) {
		firePropertyChange(new IndexedPropertyChangeEvent(_source,
				propertyName, oldValue, newValue, index));
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			int oldValue, int newValue) {
		if (oldValue == newValue) {
			return;
		}
		fireIndexedPropertyChange(propertyName, index, new Integer(oldValue),
				new Integer(newValue));
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			boolean oldValue, boolean newValue) {
		if (oldValue == newValue) {
			return;
		}
		fireIndexedPropertyChange(propertyName, index, new Boolean(oldValue),
				new Boolean(newValue));
	}

}
