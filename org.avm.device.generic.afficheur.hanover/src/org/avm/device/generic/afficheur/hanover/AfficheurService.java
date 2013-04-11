package org.avm.device.generic.afficheur.hanover;

import org.avm.device.afficheur.AbstractAfficheurService;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.generic.afficheur.hanover.protocol.AfficheurProtocolFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

public class AfficheurService extends AbstractAfficheurService {

	public AfficheurService(ComponentContext context, ServiceReference device) {
		super(context, device);
	}

	public AfficheurProtocol getAfficheurProtocol(final String protocolName) {
		return AfficheurProtocolFactory.create(protocolName);
	}

}
