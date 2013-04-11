package org.avm.device.generic.afficheur.mobitec;

import org.avm.device.afficheur.AbstractAfficheurService;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.generic.afficheur.mobitec.protocol.AfficheurProtocolFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur DEVICE_DESCRIPTION :
 * Controleur d'afficheur Mobitec DEVICE_MANUFACTURER : mobitec DEVICE_MODEL :
 * org.avm.device.afficheur.mobitec DEVICE_NAME :
 * org.avm.device.afficheur.mobitec DEVICE_SERIAL :
 * 4df3687a-9b67-46c5-b83f-b581c98feff2 DEVICE_VERSION : 1.0.0 url :
 * rs485:2;baudrate
 * =1200;stopbits=2;parity=even;bitsperchar=7;autocts=off;autorts=off protocol :
 * NSI
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