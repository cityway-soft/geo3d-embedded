package org.avm.device.knet.media.bundle;

import java.util.Dictionary;

import org.apache.log4j.Logger;
import org.avm.device.knet.media.MediaKnetConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements MediaKnetConfig {
	public static final String MEDIA_ID_TAG = "MEDIA_ID";

	public static final String DEFAULT_MEDIA = "KNET";

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
		_log = Logger.getInstance(this.getClass());
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public String getMediaId() {
		return (String) _config.get(MEDIA_ID_TAG);
	}

}
