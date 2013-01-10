package org.avm.device.fm6000.network.jni;

public class COMVS_NETCONFIG_DNS_CONFIGURATION {
	private COMVS_NETCONFIG_IP primaryDNS;
	private COMVS_NETCONFIG_IP secondaryDNS;
	private int DNSTimeout;

	public COMVS_NETCONFIG_DNS_CONFIGURATION(COMVS_NETCONFIG_IP primaryDNS,
			COMVS_NETCONFIG_IP secondaryDNS, int timeout) {
		this.primaryDNS = primaryDNS;
		this.secondaryDNS = secondaryDNS;
		this.DNSTimeout = timeout;
	}

	public COMVS_NETCONFIG_IP getPrimaryDNS() {
		return primaryDNS;
	}

	public void setPrimaryDNS(COMVS_NETCONFIG_IP primaryDNS) {
		this.primaryDNS = primaryDNS;
	}

	public COMVS_NETCONFIG_IP getSecondaryDNS() {
		return secondaryDNS;
	}

	public void setSecondaryDNS(COMVS_NETCONFIG_IP secondaryDNS) {
		this.secondaryDNS = secondaryDNS;
	}

	public int getDNSTimeout() {
		return DNSTimeout;
	}

	public void setDNSTimeout(int timeout) {
		DNSTimeout = timeout;
	}

}
