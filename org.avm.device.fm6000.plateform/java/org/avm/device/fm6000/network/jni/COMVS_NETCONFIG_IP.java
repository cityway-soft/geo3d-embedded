package org.avm.device.fm6000.network.jni;

public class COMVS_NETCONFIG_IP {

	private int a, b, c, d;

	public COMVS_NETCONFIG_IP(int a, int b, int c, int d) {
		initilize(a, b, c, d);
	}

	public String toString() {
		return (a + "." + b + "." + c + "." + d);
	}

	public void initilize(int a, int b, int c, int d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
}
