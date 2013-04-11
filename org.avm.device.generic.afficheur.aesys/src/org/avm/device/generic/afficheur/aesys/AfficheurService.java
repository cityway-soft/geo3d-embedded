package org.avm.device.generic.afficheur.aesys;

import org.avm.device.afficheur.AbstractAfficheurService;
import org.avm.device.afficheur.AfficheurProtocol;
import org.avm.device.generic.afficheur.aesys.protocol.AfficheurProtocolFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

/**
 * DEVICE_CATEGORY : org.avm.device.afficheur.Afficheur DEVICE_DESCRIPTION :
 * Controleur d'afficheur Aesys DEVICE_MANUFACTURER : aesys. DEVICE_MODEL :
 * org.avm.device.generic.afficheur.aesys DEVICE_NAME :
 * org.avm.device.generic.afficheur.aesys DEVICE_SERIAL : DEVICE_VERSION : 1.0.0
 * url : rs485:0;baudrate=9600;bitsperchar=8;stopbits=1;parity=none;autocts=off;
 * autorts=off
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
