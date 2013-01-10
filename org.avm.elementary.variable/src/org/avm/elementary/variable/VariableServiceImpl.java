package org.avm.elementary.variable;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.common.ComponentFactoryInjector;
import org.avm.elementary.common.Config;
import org.avm.elementary.common.ConfigurableService;
import org.avm.elementary.common.ManageableService;
import org.avm.elementary.variable.bundle.ConfigImpl;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.util.measurement.Measurement;

public class VariableServiceImpl implements VariableService,
		ConfigurableService, ManageableService, ComponentFactoryInjector {

	private Logger _log = Logger.getInstance(this.getClass());

	private Hashtable _variables = new Hashtable();

	private Hashtable _factories = new Hashtable();

	private ConfigImpl _config;

	public Measurement read(String name) {
		ComponentInstance instance = (ComponentInstance) _variables.get(name);
		Variable v = (Variable) instance.getInstance();
		return v.getValue();
	}

	public void write(String name, Measurement value) {
		ComponentInstance instance = (ComponentInstance) _variables.get(name);
		Variable v = (Variable) instance.getInstance();
		v.setValue(value);
	}

	private void declareVariables() {
		Dictionary infos = _config.get();
		_log.debug("[DSU] declareVariables " + infos);
		for (Enumeration it = infos.elements(); it.hasMoreElements();) {
			Properties info = (Properties) it.nextElement();
			declareVariable(info);
		}
	}

	private void declareVariable(Properties p) {
		String type = p.getProperty(Variable.TYPE);
		if (type != null) {
			ComponentFactory factory = (ComponentFactory) _factories.get(type);
			ComponentInstance instance = factory.newInstance(p);
			_log.debug("[DSU] declareVariable : "
					+ p.getProperty(Variable.NAME) + " " + instance);
			_variables.put(p.getProperty(Variable.NAME), instance);
		}
	}

	private void undeclareVariables() {
		_log.debug("[DSU] undeclareVariables " + _variables);

		for (Enumeration iter = _variables.elements(); iter.hasMoreElements();) {
			ComponentInstance instance = (ComponentInstance) iter.nextElement();
			_log.debug("[DSU] undeclareVariable : " + instance);
			instance.dispose();
		}
		_variables.clear();
		_log.debug("[DSU] undeclareVariables : " + _variables);
	}

	public void start() {
		declareVariables();
	}

	public void stop() {
		undeclareVariables();
	}

	public void configure(Config config) {
		_config = (ConfigImpl) config;
	}

	public void setComponentFactory(String target, ComponentFactory factory) {
		_factories.put(target, factory);
	}

	public void unsetComponentFactory(String target, ComponentFactory factory) {
		_factories.remove(target);
	}
}
