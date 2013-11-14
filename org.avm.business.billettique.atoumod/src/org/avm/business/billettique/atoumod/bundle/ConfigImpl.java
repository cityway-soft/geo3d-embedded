package org.avm.business.billettique.atoumod.bundle;

import java.util.Dictionary;

import org.avm.business.billettique.atoumod.BillettiqueConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements BillettiqueConfig {

	private static final String PORT = "port";
	private static final String LOCALPORT = "local-port";
	private static final String TSURV = "tsurv";
	private static final String NSURV = "nsurv";
	private static final String HOST = "host";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		return result;
	}

	protected String getPid() {
		return Activator.getDefault().getPid();
	}

	public int getPort() {
		return ((Integer) _config.get(PORT)).intValue();
	}

	public void setPort(int port) {
		_config.put(PORT, Integer.toString(port));
	}
	
	
	public Integer getLocalPort() {
		return ((Integer) _config.get(LOCALPORT));
	}

	public void setLocalPort(Integer port) {
		_config.put(LOCALPORT, port);
	}


	public int getTSurv() {
		return ((Integer) _config.get(TSURV)).intValue();
	}

	public void setTSurv(int tsuv) {
		_config.put(TSURV, new Integer(tsuv));
	}
	
	public void setNSurv(int tsuv) {
		_config.put(NSURV, new Integer(tsuv));
	}
	
	public int getNSurv() {
		return ((Integer) _config.get(NSURV)).intValue();
	}

	public String getHost() {
		return (String)_config.get(HOST);
	}

	public void setHost(String host) {
		_config.put(HOST, host);
	}

}
