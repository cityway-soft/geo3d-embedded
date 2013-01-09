package org.avm.device.fm6000.network.jni;

public class COMVS_NETCONFIG_ROUTE {
	private COMVS_NETCONFIG_IP destinationIp;
	private COMVS_NETCONFIG_IP mask;
	private COMVS_NETCONFIG_IP gateway;
	private int metric;
	private int interfaceIndex;

	public COMVS_NETCONFIG_ROUTE(COMVS_NETCONFIG_IP destinationIp,
			COMVS_NETCONFIG_IP mask, COMVS_NETCONFIG_IP gateway, int metric,
			int interfaceIndex) {
		super();
		this.destinationIp = destinationIp;
		this.mask = mask;
		this.gateway = gateway;
		this.metric = metric;
		this.interfaceIndex = interfaceIndex;
	}

	public COMVS_NETCONFIG_IP getDestinationIp() {
		return destinationIp;
	}

	public void setDestinationIp(COMVS_NETCONFIG_IP destinationIp) {
		this.destinationIp = destinationIp;
	}

	public COMVS_NETCONFIG_IP getMask() {
		return mask;
	}

	public void setMask(COMVS_NETCONFIG_IP mask) {
		this.mask = mask;
	}

	public COMVS_NETCONFIG_IP getGateway() {
		return gateway;
	}

	public void setGateway(COMVS_NETCONFIG_IP gateway) {
		this.gateway = gateway;
	}

	public int getMetric() {
		return metric;
	}

	public void setMetric(int metric) {
		this.metric = metric;
	}

	public int getInterfaceIndex() {
		return interfaceIndex;
	}

	public void setInterfaceIndex(int interfaceIndex) {
		this.interfaceIndex = interfaceIndex;
	}

	public String toString() {
		return "route to:" + destinationIp.toString() + "\n\t by: "
				+ gateway.toString() + "\n\t using mask: " + mask.toString()
				+ "\n\t with metric: " + metric + "\n\t on interface: "
				+ interfaceIndex;
	}
}
