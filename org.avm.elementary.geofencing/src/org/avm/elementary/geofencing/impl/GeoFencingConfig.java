package org.avm.elementary.geofencing.impl;

public interface GeoFencingConfig {
	public Integer getDimension();

	public void setDimension(Integer dimension);

	public String getFileName();

	public void setFileName(String fileName);

	public Double getFillFactor();

	public void setFillFactor(Double fillFactor);

	public Integer getLeafCapacity();

	public void setLeafCapacity(Integer leafCapacity);

	public Integer getIndexCapacity();

	public void setIndexCapacity(Integer indexCapacity);

	public Boolean getOverwrite();

	public void setOverwrite(Boolean overwrite);

	public Integer getPageSize();

	public void setPageSize(Integer pageSize);

	public Integer getBufferCapacity();

	public void setBufferCapacity(Integer bufferCapacity);
}
