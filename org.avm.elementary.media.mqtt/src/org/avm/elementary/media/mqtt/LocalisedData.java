package org.avm.elementary.media.mqtt;

import java.util.Date;

public class LocalisedData {

	double lon, lat, speed, track;

	Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getTrack() {
		return track;
	}

	public void setTrack(double track) {
		this.track = track;
	}

}
