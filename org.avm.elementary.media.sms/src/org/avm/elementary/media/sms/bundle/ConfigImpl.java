package org.avm.elementary.media.sms.bundle;

import java.util.Dictionary;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.media.sms.MediaSMSConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements MediaSMSConfig {
	public static final String SMSCENTER_TAG = "org.avm.elementary.media.sms.smscenter";

	public static final String DEFAULT_SMSCENTER = "0609001390";

	/*
	 * Service center number: SFR: 0609001390 BGT: 0660003000 ORG: 0689004000
	 */

	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	protected Dictionary getDefault() {
		Dictionary result = super.getDefault();
		// result.put(SMSCENTER_TAG, DEFAULT_SMSCENTER);
		return result;
	}

	protected String getPid() {
		return Activator.PID;
	}

	public String getMediaId() {
		return "SMS_00000000";
	}

	public String getSMSCenter() {
		return (String) _config.get(SMSCENTER_TAG);
	}

	public void setSMSCenter(String smscenter) {
		_config.put(SMSCENTER_TAG, smscenter);
	}

}
