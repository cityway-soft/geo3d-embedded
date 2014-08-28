package org.avm.device.wanesy;

import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KmsSendSms;
import org.avm.device.knet.model.KmsSmsTrig;
import org.avm.device.knet.model.KnetException;

public class Main {
	public static void main(String[] args) {
		//SocketConnection sc= new SocketConnection("localhost", 7777);
		
		SocketConnection sc= new KnetConnection("10.1.1.174", 35035, "demo","demodemo");
		sc.start();
		
		Object obj = new Object();
		synchronized (obj) {
			try {
				obj.wait(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		KmsRoot kro = new KmsRoot ();
		KmsSendSms sms = new KmsSendSms();
		sms.setDest("+33681968891");
		sms.setText("Youplaboum");
		KmsSmsTrig trig = new KmsSmsTrig();
		trig.setActivateMode(true);
		kro.addSubRoll(trig);
		
		KmsRoot kro2 = new KmsRoot ();
		kro2.addSubRoll(sms);
		System.out.println(kro.toXMLString());
		try {
			
			kro.marshal(sc.getOuputStream());
			kro2.marshal(sc.getOuputStream());
			//kro.marshal(sc.getOuputStream());
		} catch (KnetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		synchronized (obj) {
			try {
				obj.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
