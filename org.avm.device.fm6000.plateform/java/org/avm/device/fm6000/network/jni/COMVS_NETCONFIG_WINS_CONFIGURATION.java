package org.avm.device.fm6000.network.jni;

public class COMVS_NETCONFIG_WINS_CONFIGURATION {
	private COMVS_NETCONFIG_IP primaryWINS;
	private COMVS_NETCONFIG_IP secondaryWINS;

	public COMVS_NETCONFIG_IP getPrimaryWINS() {
		return primaryWINS;
	}

	public void setPrimaryWINS(COMVS_NETCONFIG_IP primaryWINS) {
		this.primaryWINS = primaryWINS;
	}

	public COMVS_NETCONFIG_IP getSecondaryWINS() {
		return secondaryWINS;
	}

	public void setSecondaryWINS(COMVS_NETCONFIG_IP secondaryWINS) {
		this.secondaryWINS = secondaryWINS;
	}

	public COMVS_NETCONFIG_WINS_CONFIGURATION(COMVS_NETCONFIG_IP primaryWINS,
			COMVS_NETCONFIG_IP secondaryWINS) {
		super();
		// TODO Auto-generated constructor stub
		this.primaryWINS = primaryWINS;
		this.secondaryWINS = secondaryWINS;
	}

}
