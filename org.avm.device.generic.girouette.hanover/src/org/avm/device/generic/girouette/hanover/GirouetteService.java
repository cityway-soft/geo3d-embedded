package org.avm.device.generic.girouette.hanover;

import org.avm.device.generic.girouette.hanover.protocol.GirouetteProtocolFactory;
import org.avm.device.girouette.AbstractGirouetteService;
import org.avm.device.girouette.GirouetteProtocol;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class GirouetteService extends AbstractGirouetteService {

	public GirouetteService(ComponentContext context, ServiceReference device) {
		super(context, device);
	}

	public GirouetteProtocol getGirouetteProtocol(final String protocolName) {
		return GirouetteProtocolFactory.create(protocolName);
	}

}
