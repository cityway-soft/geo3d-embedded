package org.avm.device.generic.can.manager;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.avm.elementary.common.ComponentFactoryInjector;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

public class CanManagerImpl implements ConfigurableService, ManageableService,
		Constants, ComponentFactoryInjector {

	private CanManagerConfig _config;

	private ComponentFactory _factory;

	private Map _map = new HashMap();

	public void configure(Config config) {
		_config = (CanManagerConfig) config;
	}

	public void setComponentFactory(String target, ComponentFactory factory) {
		_factory = factory;
	}

	public void unsetComponentFactory(String target, ComponentFactory factory) {
		_factory = null;
	}

	public void start() {
		Dictionary map = _config.get();
		for (Enumeration iterator = map.elements(); iterator.hasMoreElements();) {
			Properties p = (Properties) iterator.nextElement();
			String name = (String) p.get(SERVICE_PID);
			ComponentInstance instance = _factory.newInstance(p);			
			ComponentInstance previous = (ComponentInstance) _map.put(name,
					instance);
			if (previous != null) {
				previous.dispose();
			}
		}

	}

	public void stop() {
		for (Iterator iterator = _map.values().iterator(); iterator.hasNext();) {
			ComponentInstance instance = (ComponentInstance) iterator.next();
			if (instance != null) {
				instance.dispose();
			}
		}
		_map.clear();
	}
}
