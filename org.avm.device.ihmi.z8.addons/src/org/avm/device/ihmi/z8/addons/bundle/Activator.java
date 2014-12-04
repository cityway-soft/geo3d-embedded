package org.avm.device.ihmi.z8.addons.bundle;

import org.avm.elementary.common.AbstractActivator;
import org.osgi.service.component.ComponentContext;

public class Activator extends AbstractActivator {

	private CommandGroupImpl commandGroup;

	protected void start(ComponentContext context) {
		commandGroup = new CommandGroupImpl(context);
		commandGroup.start();
	}

	protected void stop(ComponentContext context) {
		commandGroup.stop();
	}

}
