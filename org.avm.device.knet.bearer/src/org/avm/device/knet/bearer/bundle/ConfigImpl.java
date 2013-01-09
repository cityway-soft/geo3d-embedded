package org.avm.device.knet.bearer.bundle;

import java.util.Dictionary;

import org.apache.log4j.Logger;
import org.avm.device.knet.KnetAgent;
import org.avm.device.knet.bearer.BearerManager;
import org.avm.device.knet.bearer.BearerManagerConfig;
import org.avm.device.knet.bearer.BearerManagerImpl;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements BearerManagerConfig {
	public static final String BEARER_ID_TAG = "BEARER_ID";

	public static final String DEFAULT_BEARER = BearerManager.BEARER_none;

	public static final String STATUS_ID_TAG = "STATUS_ID";

	public static final String DEFAULT_STATUS = KnetAgent.STATUS_DECONN;

	private BearerManagerImpl _bearer;

	private Logger _log;

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
		_log = Logger.getInstance(this.getClass());
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		result.put(BEARER_ID_TAG, DEFAULT_BEARER);
		result.put(STATUS_ID_TAG, DEFAULT_STATUS);
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public String getBearer() {
		return (String) _config.get(BEARER_ID_TAG);
	}

	public void setBearer(String bearer) {
		_config.put(BEARER_ID_TAG, bearer);
	}

	public String getStatus() {
		return (String) _config.get(STATUS_ID_TAG);
	}

	public void updateBearer(String bearer, String status) {
		_config.put(BEARER_ID_TAG, bearer);
		_config.put(STATUS_ID_TAG, status);

		// _log.debug("update Bearer : " + bearer + " -> " + status);
		_log.debug("updateBearer : " + bearer + " -> " + status);
	}
}
