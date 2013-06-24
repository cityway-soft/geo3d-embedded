package org.avm.business.core.bundle;

import java.util.Dictionary;

import org.avm.business.core.AvmConfig;
import org.avm.elementary.common.AbstractConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements AvmConfig {

	public static final String TAG_CHEVAUCHEMENT = "org.avm.business.core.chevauchement";
	
	public static final String TAG_AMPLITUDE = "org.avm.business.core.amplitude";

	public static final String TAG_MAXTRANCHE = "org.avm.business.core.maxtranche";

	public static final String TAG_TOLERANCEDEVIATION = "org.avm.business.core.tolerancedeviation";

	public static final String TAG_SUIVICRSPERIOD = "org.avm.business.core.suivicrsperiod";

	public static final String TAG_CHECKVALIDITE = "org.avm.business.core.checkvalidite";
	
	public static final String TAG_AUTOMATICSALABEL = "org.avm.business.core.sa.automatic.label";
	
	public static final String TAG_AUTOMATICCOURSEMODE = "org.avm.business.core.sa.automatic.course";

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

	public Integer getAmplitude() {
		return (Integer) _config.get(TAG_AMPLITUDE);
	}

	public void setAmplitude(Integer amplitude) {
		_config.put(TAG_AMPLITUDE, amplitude);
	}

	public Integer getChevauchement() {
		return (Integer) _config.get(TAG_CHEVAUCHEMENT);
	}

	public void setChevauchement(Integer chevauchement) {
		_config.put(TAG_CHEVAUCHEMENT, chevauchement);
	}

	public Integer getMaxTranche() {
		return (Integer) _config.get(TAG_MAXTRANCHE);
	}

	public void setMaxTranche(Integer maxtranche) {
		_config.put(TAG_MAXTRANCHE, maxtranche);
	}

	public Integer getToleranceDev() {
		return (Integer) _config.get(TAG_TOLERANCEDEVIATION);
	}

	public void setToleranceDev(Integer tolerancedev) {
		_config.put(TAG_TOLERANCEDEVIATION, tolerancedev);
	}

	public Integer getSuiviCrsPeriod() {
		return (Integer) _config.get(TAG_SUIVICRSPERIOD);
	}

	public void setSuiviCrsPeriod(Integer suivicrsperiod) {
		_config.put(TAG_SUIVICRSPERIOD, suivicrsperiod);
	}

	public boolean isCheckValidite() {
		String val = (String)_config.get(TAG_CHECKVALIDITE);
		return (val != null && val.equals("true"));
	}

	public void setCheckValidite(boolean check) {
		_config.put(TAG_CHECKVALIDITE, check?"true":"false");
	}

	public String getAutomaticSALabel() {
		return (String) _config.get(TAG_AUTOMATICSALABEL);
	}

	public void setAutomaticSALabel(String label) {
		_config.put(TAG_AUTOMATICSALABEL, label);
		
	}

	public boolean isAutomaticCourseMode() {
		String val = (String)_config.get(TAG_AUTOMATICCOURSEMODE);
		return (val != null && val.equals("true"));
	}

	public void setAutomaticCourseMode(boolean valid) {
		_config.put(TAG_AUTOMATICCOURSEMODE, valid?"true":"false");
	}
}
