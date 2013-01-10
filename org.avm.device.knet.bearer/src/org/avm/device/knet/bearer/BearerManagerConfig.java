package org.avm.device.knet.bearer;

public interface BearerManagerConfig {

	public String getBearer();

	public void setBearer(String bearer);

	public String getStatus();

	public void updateBearer(String bearer, String status);

}
