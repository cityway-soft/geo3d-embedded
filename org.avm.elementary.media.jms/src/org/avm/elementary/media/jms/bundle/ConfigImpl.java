package org.avm.elementary.media.jms.bundle;

import java.text.MessageFormat;
import java.util.Dictionary;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.media.jms.MediaJMSConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements MediaJMSConfig {

	public static final String UIL_ADDRESS_TAG = "UIL_ADDRESS";

	public static final String DESTINATION_TAG = "DESTINATION";

	public static final String SERVER_IL_FACTORY_TAG = "SERVER_IL_FACTORY";

	public static final String CLIENT_IL_SERVICE_KEY_TAG = "CLIENT_IL_SERVICE_KEY";

	public static final String PING_PERIOD_TAG = "PING_PERIOD";

	public static final String UIL_PORT_KEY_TAG = "UIL_PORT_KEY";

	public static final String UIL_TCPNODELAY_TAG = "UIL_TCPNODELAY";

	public static final String UIL_CHUNKSIZE_TAG = "UIL_CHUNKSIZE";

	public static final String UIL_BUFFERSIZE_TAG = "UIL_BUFFERSIZE";

	public static final String DEFAULT_UIL_ADDRESS = "sam.mercur.fr";

	public static final String DEFAULT_DESTINATION = "media-gprs";

	public static final String DEFAULT_SERVER_IL_FACTORY = "org.jboss.mq.il.uil2.UILServerILFactory";

	public static final String DEFAULT_CLIENT_IL_SERVICE_KEY = "org.jboss.mq.il.uil2.UILClientILService";

	public static final Integer DEFAULT_PING_PERIOD = new Integer(120000);

	public static final Integer DEFAULT_UIL_PORT_KEY = new Integer(8094);

	public static final Boolean DEFAULT_UIL_TCPNODELAY = Boolean.TRUE;

	public static final Integer DEFAULT_UIL_CHUNKSIZE = new Integer(1000000);

	public static final Integer DEFAULT_UIL_BUFFERSIZE = new Integer(2048);

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public String getDestination() {
		return (String) _config.get(DESTINATION_TAG);
	}

	public void setDestination(String destination) {
		_config.put(DESTINATION_TAG, destination);
	}

	public String getClientILService() {
		return (String) _config.get(CLIENT_IL_SERVICE_KEY_TAG);
	}

	public void setClientILService(String clientILService) {
		_config.put(CLIENT_IL_SERVICE_KEY_TAG, clientILService);
	}

	public Integer getPingPeriod() {
		return (Integer) _config.get(PING_PERIOD_TAG);
	}

	public void setPingPeriod(Integer pingPeriod) {
		_config.put(PING_PERIOD_TAG, pingPeriod);
	}

	public String getServerILFactory() {
		return (String) _config.get(SERVER_IL_FACTORY_TAG);
	}

	public void setServerILFactory(String serverILFactory) {
		_config.put(SERVER_IL_FACTORY_TAG, serverILFactory);
	}

	public Integer getUilBufferSize() {
		return (Integer) _config.get(UIL_BUFFERSIZE_TAG);
	}

	public void setUilBufferSize(Integer uilBufferSize) {
		_config.put(UIL_BUFFERSIZE_TAG, uilBufferSize);
	}

	public Integer getUilChrunkSize() {
		return (Integer) _config.get(UIL_CHUNKSIZE_TAG);
	}

	public void setUilChrunkSize(Integer uilChrunkSize) {
		_config.put(UIL_CHUNKSIZE_TAG, uilChrunkSize);
	}

	public Integer getUilPort() {
		return (Integer) _config.get(UIL_PORT_KEY_TAG);
	}

	public void setUilPort(Integer uilPort) {
		_config.put(UIL_PORT_KEY_TAG, uilPort);
	}

	public Boolean getUilTCPNoDelay() {
		return (Boolean) _config.get(UIL_TCPNODELAY_TAG);
	}

	public void setUilTCPNoDelay(Boolean uilTCPNoDelay) {
		_config.put(UIL_TCPNODELAY_TAG, uilTCPNoDelay);
	}

	public String getMediaId() {
		// TODO [DSU]
		Integer owner = new Integer(getProperty("org.avm.exploitation.id", "0"));
		Integer id = new Integer(getProperty("org.avm.vehicule.id", "0"));
		Object[] args = { owner, id };
		return MessageFormat
				.format("GPRS_{0,number,000}{1,number,00000}", args);
	}

	public String getUilAddress() {
		return (String) _config.get(UIL_ADDRESS_TAG);
	}

	public void setUilAddress(String uilAddress) {
		_config.put(UIL_ADDRESS_TAG, uilAddress);
	}
}
