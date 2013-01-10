package org.avm.elementary.geofencing.bundle;

import org.avm.elementary.common.AbstractConfig;
import org.avm.elementary.geofencing.impl.GeoFencingConfig;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

public class ConfigImpl extends AbstractConfig implements GeoFencingConfig { 

	public static final String DIMENSION_TAG = "dimension";
	public static final String FILENAME_TAG = "filename";
	public static final String FILLFACTOR_TAG = "fillfactor";
	public static final String LEAFCAPACITY_TAG = "leafcapacity";
	public static final String INDEXCAPACITY_TAG = "indexcapacity";
	public static final String PAGESIZE_TAG = "pagesize";
	public static final String OVERWRITE_TAG = "overwrite";
	public static final String BUFFERCAPACITY_TAG = "buffercapacity";


	public ConfigImpl(ComponentContext context, ConfigurationAdmin cm) {
		super(context, cm);
	}

	public Integer getDimension() {
		return (Integer) _config.get(DIMENSION_TAG);
	}

	public void setDimension(Integer dimension) {
		_config.put(DIMENSION_TAG, dimension);
	}

	public String getFileName() {
		return (String) _config.get(FILENAME_TAG);
	}

	public void setFileName(String fileName) {
		_config.put(FILENAME_TAG, fileName);
	}

	public Double getFillFactor() {
		return (Double) _config.get(FILLFACTOR_TAG);
	}

	public void setFillFactor(Double fillFactor) {
		_config.put(FILLFACTOR_TAG, fillFactor);
	}

	public Integer getLeafCapacity() {
		return (Integer) _config.get(LEAFCAPACITY_TAG);
	}

	public void setLeafCapacity(Integer leafCapacity) {
		_config.put(LEAFCAPACITY_TAG, leafCapacity);
	}

	public Integer getIndexCapacity() {
		return (Integer) _config.get(INDEXCAPACITY_TAG);
	}

	public void setIndexCapacity(Integer indexCapacity) {
		_config.put(INDEXCAPACITY_TAG, indexCapacity);
	}

	public Boolean getOverwrite() {
		return (Boolean) _config.get(OVERWRITE_TAG);
	}

	public void setOverwrite(Boolean overwrite) {
		_config.put(OVERWRITE_TAG, overwrite);
	}

	public Integer getPageSize() {
		return (Integer) _config.get(PAGESIZE_TAG);
	}

	public void setPageSize(Integer pageSize) {
		_config.put(PAGESIZE_TAG, pageSize);
	}

	public Integer getBufferCapacity() {
		return (Integer) _config.get(BUFFERCAPACITY_TAG);
	}

	public void setBufferCapacity(Integer bufferCapacity) {
		_config.put(BUFFERCAPACITY_TAG, bufferCapacity);
	}
}
