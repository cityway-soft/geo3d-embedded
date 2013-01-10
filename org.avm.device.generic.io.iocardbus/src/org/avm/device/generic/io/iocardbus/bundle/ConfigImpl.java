package org.avm.device.generic.io.iocardbus.bundle;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

import org.avm.device.generic.io.iocardbus.IOCardBusConfig;
import org.avm.device.io.IOCardInfo;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements IOCardBusConfig {

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public void add(String name, String category, String manufacturer,
			String model, String serial) {
		Vector v = new Vector();
		v.addElement(category);
		v.addElement(manufacturer);
		v.addElement(model);
		v.addElement(serial);
		_config.put(name, v);
	}

	public void remove(String name) {
		_config.remove(name);
	}

	public IOCardInfo getIOCCardInfo(String name) {

		IOCardInfo result = null;
		Object o = _config.get(name);
		if (o != null && o instanceof Vector) {
			Vector v = (Vector) o;
			String category = (String) v.elementAt(0);
			String manufacturer = (String) v.elementAt(1);
			String model = (String) v.elementAt(2);
			String serial = (String) v.elementAt(3);

			result = new IOCardInfo(category, manufacturer, model, serial);
		}
		return result;
	}

	public IOCardInfo[] getIOCCardInfos() {
		Vector v = new Vector();
		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String name = (String) it.nextElement();
			IOCardInfo info = getIOCCardInfo(name);
			if (info != null) {
				v.addElement(info);
			}
		}

		IOCardInfo[] result = new IOCardInfo[v.size()];
		v.copyInto(result);

		return result;
	}

	public String toString() {
		StringBuffer text = new StringBuffer();

		for (Enumeration it = _config.keys(); it.hasMoreElements();) {
			String name = (String) it.nextElement();
			IOCardInfo info = getIOCCardInfo(name);
			if (info != null) {
				text.append(name + ": " + info.toString() + "\n");
			}
		}
		return text.toString();
	}

}
