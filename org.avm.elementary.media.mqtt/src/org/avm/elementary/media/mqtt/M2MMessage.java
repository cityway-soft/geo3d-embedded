package org.avm.elementary.media.mqtt;

public class M2MMessage {
	BaseData base;
	LocalisedData location;
	String data;

	
	

	public BaseData getBase() {
		return base;
	}

	public void setBase(BaseData base) {
		this.base = base;
	}

	public LocalisedData getLocation() {
		return location;
	}

	public void setLocation(LocalisedData location) {
		this.location = location;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	

}
