package org.avm.business.billettique.atoumod;


public interface BillettiqueConfig {
	public String getHost();
	
	public void setHost(String host);
	
	public int getPort();
	
	public void setPort(int port);
	
	public int getTSurv();
	
	public void setTSurv(int tsurv);
	
	
	public int getNSurv();
	
	public void setNSurv(int nsurv);

	public Integer getLocalPort();
	
	public void setLocalPort(Integer port);

}
