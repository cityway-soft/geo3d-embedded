package org.avm.device.wanesy;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KnetException;
import org.avm.elementary.common.ManageableService;

public class DeviceWanesyImpl implements DeviceWanesy, ManageableService,
		KnetConnectionListener {

	private KnetConnection knetConnection;

	private Logger log = Logger.getInstance(this.getClass());
	
	private List listeners;

	public DeviceWanesyImpl() {
//		knetConnection = new KnetConnection("192.168.2.1", 35035, "demo",
//				"demodemo");
		knetConnection = new KnetConnection("localhost", 35035, "demo",
		"demodemo");
		listeners = new ArrayList();
	}

	public void start() {
		knetConnection.setListener(this);
		knetConnection.start();
	}

	public void stop() {
		// todo stop
	}

	public boolean isConnected() {
		return knetConnection.isConnected();
	}

	public boolean send(KmsMarshaller kms) {
		KmsRoot kro = new KmsRoot();
		kro.addSubRoll(kms);
		try {
			kro.marshal(knetConnection.getOuputStream());
			return true;
		} catch (KnetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void removeDeviceListener(
			DeviceWanesyListener deviceWanesyInterfaceService) {
		listeners.remove(deviceWanesyInterfaceService);
	}

	public void addDeviceListener(
			DeviceWanesyListener deviceWanesyInterfaceService) {
		System.out.println("add listener : " + deviceWanesyInterfaceService);
		listeners.add(deviceWanesyInterfaceService);
	}

	public void onEvent(KmsMarshaller kms) {
		System.out.println("kms : "  + kms);
		String role = kms.getRole();
		if (role != null && role.equals("kms")) {
			KmsMarshaller sb = (KmsMarshaller) ((KmsRoot) kms).getSubKms();
			if (sb != null) {
				role = sb.getRole();
				log.info ("get kms with " + role + " role");
				for (int i = 0; i < listeners.size(); ++i) {
					DeviceWanesyListener elt = (DeviceWanesyListener) listeners
							.get(i);
					System.out.println("elt:" + elt);
					if (elt!=null &&elt.getListeningOn().indexOf(role) != -1) {
						elt.onDataReceived(sb);
					}
				}
			}
		}else if (role != null && role.equals("stopreq")){
			for (int i = 0; i < listeners.size(); ++i) {
				DeviceWanesyListener elt = (DeviceWanesyListener) listeners
						.get(i);
				System.out.println("elt:" + elt);
				if (elt!=null &&elt.getListeningOn().indexOf(role) != -1) {
					elt.onDataReceived(kms);
				}
			}
		}
	}
}