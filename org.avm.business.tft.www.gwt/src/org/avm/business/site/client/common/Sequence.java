package org.avm.business.site.client.common;

import java.util.Iterator;
import java.util.Vector;

public class Sequence {

	private Vector _zones = new Vector();

	private Vector _staticZones = new Vector();

	private int _index = 0;

	private Zone _currentZone;

	private String _name;

	public Sequence(String name) {
		_name = name;
	}

	public Vector getStaticZones() {
		return _staticZones;
	}

	public Zone getCurrentZone() {
		return _currentZone;
	}

	public void setData(AVMModel model) {
		Iterator i = _staticZones.iterator();
		while (i.hasNext()) {
			Zone z = (Zone) i.next();
			z.setData(model);
		}
	}

	public void addZone(Zone zone) {
		_zones.add(zone);
		if (_currentZone == null) {
			init();
		}
	}

	public void addStaticZone(Zone zone) {
		_staticZones.add(zone);
	}

	public Zone getNext() {
		Zone zone = null;
		try {
			if (_zones.size() > _index) {
				zone = (Zone) _zones.elementAt(_index);
				_index++;
			} else {
				_index = 0;
				zone = (Zone) _zones.elementAt(_index);
			}
		} catch (Throwable t) {

		}
		return zone;
	}

	private void init() {
		_currentZone = (Zone) _zones.elementAt(0);
	}

	public void activate(boolean b) {
		if (b) {
			Iterator i = _staticZones.iterator();
			while (i.hasNext()) {
				Zone z = (Zone) i.next();
				z.activate(true);
			}
		} else {
			Iterator i = _staticZones.iterator();
			while (i.hasNext()) {
				AbstractZone z = (AbstractZone) i.next();
				z.activate(false);
			}
		}
	}

	public String toString() {
		Iterator i = _zones.iterator();
		StringBuffer buf = new StringBuffer();

		buf.append(_name);
		buf.append(":");
		while (i.hasNext()) {
			AbstractZone z = (AbstractZone) i.next();
			buf.append(z.toString());
			buf.append(" - ");
		}
		return buf.toString();
	}

}
