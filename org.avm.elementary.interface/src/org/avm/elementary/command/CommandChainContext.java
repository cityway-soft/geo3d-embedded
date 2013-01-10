package org.avm.elementary.command;

import java.util.HashMap;

import org.apache.commons.chain.Context;
import org.osgi.service.component.ComponentContext;

public class CommandChainContext extends HashMap implements Context {
	public static String COMPONENT_CONTEXT_KEY = "component-context";

	public ComponentContext getComponentContext() {
		return (ComponentContext) get(COMPONENT_CONTEXT_KEY);
	}

	public void setComponentContext(ComponentContext context) {
		put(COMPONENT_CONTEXT_KEY, context);
	}
}
