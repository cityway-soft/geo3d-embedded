package org.avm.elementary.common;

import org.osgi.service.component.ComponentFactory;

public interface ComponentFactoryInjector {

	public void setComponentFactory(String target, ComponentFactory factory);

	public void unsetComponentFactory(String target, ComponentFactory factory);
	
}
