package org.avm.elementary.variable;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.util.measurement.Unit;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class AbstractVariable implements Variable,
		ServiceTrackerCustomizer {

	protected Logger _log = Logger.getInstance(this.getClass());

	protected ComponentContext _context;

	protected Dictionary _properties;

	protected ServiceTracker _tracker;

	public static final String UNIT = "org.avm.elementary.variable.unit";

	private static final Unit[] allUnits = new Unit[] { Unit.m, Unit.s,
			Unit.kg, Unit.K, Unit.A, Unit.mol, Unit.cd, Unit.rad, Unit.m_s,
			Unit.m_s2, Unit.m2, Unit.m3, Unit.Hz, Unit.N, Unit.Pa, Unit.J,
			Unit.W, Unit.C, Unit.V, Unit.F, Unit.Ohm, Unit.S, Unit.Wb, Unit.T,
			Unit.lx, Unit.Gy, Unit.kat, Unit.unity };

	private static Hashtable _map = new Hashtable();

	static {
		for (int i = 0; i < allUnits.length; i++) {
			_map.put(allUnits[i].toString(), allUnits[i]);
		}
	}

	public static Unit getUnit(String name) {
		Unit result = null;
		if (name == null)
			return Unit.unity;
		result = (Unit) _map.get(name);
		if (result == null)
			return Unit.unity;

		return result;
	}

	public AbstractVariable() {
	}

	public Unit getUnit() {
		return getUnit(getProperty(UNIT, ""));
	}

	public String getName() {
		return (String) _properties.get(NAME);
	}

	public String getType() {
		return (String) _properties.get(TYPE);
	}

	public String getDeviceCategory() {
		return (String) _properties.get(DEVICE_CATEGORY);
	}

	public String getDeviceSerial() {
		return (String) _properties.get(DEVICE_SERIAL);
	}

	public int getDeviceIndex() {
		return Integer.parseInt((String) _properties.get(DEVICE_INDEX));
	}

	public Object addingService(ServiceReference reference) {
		return _context.getBundleContext().getService(reference);
	}

	public void modifiedService(ServiceReference reference, Object service) {
	}

	public void removedService(ServiceReference reference, Object service) {
		_context.getBundleContext().ungetService(reference);
	}

	protected void activate(ComponentContext context) {
		_log.debug("Components activating");
		_context = context;
		_properties = context.getProperties();

		String filter = "(&" + " (" + org.osgi.framework.Constants.OBJECTCLASS
				+ "=" + Device.class.getName() + ")" + " ("
				+ Constants.DEVICE_CATEGORY + "=" + getDeviceCategory() + ")"
				+ " (" + Constants.DEVICE_SERIAL + "=" + getDeviceSerial()
				+ ")" + ")";
		try {
			Filter f = _context.getBundleContext().createFilter(filter);
			_log.debug("Filter: " + f);
			_tracker = new ServiceTracker(_context.getBundleContext(), f, this);
			_tracker.open();
		} catch (InvalidSyntaxException e) {
			_log.error(e.getMessage());
		}
		_log.debug("Components activated");
	}

	protected void deactivate(ComponentContext context) {
		_log.info("Component deactivated");
		_tracker.close();
	}

	protected String getProperty(String name, String defaultValue) {
		if (_properties == null)
			return defaultValue;
		String result = (String) _properties.get(name);
		if (result == null)
			return defaultValue;
		return result;
	}

}