package org.avm.device.wanesy;

import java.io.ByteArrayInputStream;

import org.avm.device.knet.model.Kms;
import org.avm.device.knet.model.KmsAuth;
import org.avm.device.knet.model.KmsFactory;
import org.avm.device.knet.model.KmsMarshaller;
import org.avm.device.knet.model.KmsRoot;
import org.avm.device.knet.model.KnetException;

public class KnetConnection extends SocketConnection {

	private String login;
	private String password;
	byte bigBuffer[] = new byte[8192];
	int count2 = 0;
	
	private KnetConnectionListener listener = null;

	public KnetConnectionListener getListener() {
		return listener;
	}

	public void setListener(KnetConnectionListener listener) {
		this.listener = listener;
	}

	public KnetConnection(String address, int port, String login,
			String password) {
		super(address, port);
		this.login = login;
		this.password = password;
	}

	public void onRead(byte[] buffer, int count) {

		System.arraycopy(buffer, 0, bigBuffer, count2, count);
		// ByteArrayInputStream in = new
		// ByteArrayInputStream(buffer,
		// 0, count);
		count2 += count;
		if (buffer[count - 1] == 0) {
			displayBuffer(bigBuffer, count2);

			ByteArrayInputStream in = new ByteArrayInputStream(bigBuffer, 0,
					count2 - 1);
			count2 = 0;
			try {
				Kms result = KmsFactory.unmarshal(in);
				KmsMarshaller km = (KmsMarshaller) result;
				if (listener != null){
					listener.onEvent(km);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void onInit() {
		KmsAuth auth = new KmsAuth(login, password, "1");
		try {
			auth.marshal(this.getOuputStream());
		} catch (KnetException e) {
			e.printStackTrace();
		}
	}

	
}
