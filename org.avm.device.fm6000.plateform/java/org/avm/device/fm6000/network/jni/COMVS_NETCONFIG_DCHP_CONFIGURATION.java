package org.avm.device.fm6000.network.jni;

public class COMVS_NETCONFIG_DCHP_CONFIGURATION {
	private boolean enabled;
	private boolean automatic;
	private int maxRetryAttempt;

	public COMVS_NETCONFIG_DCHP_CONFIGURATION(boolean enabled,
			boolean automatic, int maxRetryAttempts) {
		super();
		this.enabled = enabled;
		this.automatic = automatic;
		this.maxRetryAttempt = maxRetryAttempts;
	}

	public COMVS_NETCONFIG_DCHP_CONFIGURATION() {
		super();
		this.enabled = false;
		this.automatic = false;
		this.maxRetryAttempt = 0;
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getMaxRetryAttempts() {
		return maxRetryAttempt;
	}

	public void setMaxRetryAttempts(int maxRetryAttempts) {
		this.maxRetryAttempt = maxRetryAttempts;
	}
}
