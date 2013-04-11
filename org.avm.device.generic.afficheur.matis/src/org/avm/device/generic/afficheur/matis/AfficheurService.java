package org.avm.device.generic.afficheur.matis;

import org.avm.device.afficheur.AbstractAfficheurService;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.generic.afficheur.matis.protocol.AfficheurProtocolFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur DEVICE_DESCRIPTION :
 * Controleur d'afficheur Matis DEVICE_MANUFACTURER : matis DEVICE_MODEL :
 * org.avm.device.generic.afficheur.matis DEVICE_NAME :
 * org.avm.device.generic.afficheur.matis DEVICE_SERIAL :
 * 4df3687a-9b67-46c5-b83f-b581c98feff2 DEVICE_VERSION : 1.0.0 url :
 * rs485:2;baudrate
 * =1200;bitsperchar=8;stopbits=1;parity=even;autocts=off;autorts=off
 * 
 */
public class AfficheurService extends AbstractAfficheurService {

	public AfficheurService(ComponentContext context, ServiceReference device) {
		super(context, device);
	}

	public AfficheurProtocol getAfficheurProtocol(final String protocolName) {
		return AfficheurProtocolFactory.create(protocolName);
	}

}
