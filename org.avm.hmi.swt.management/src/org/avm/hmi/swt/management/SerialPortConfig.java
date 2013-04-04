package org.avm.hmi.swt.management;

import java.util.List;

public class SerialPortConfig {
	String driver;
	String port;
	String speed;
	String bitsperchar;
	String stopbits;
	String parity;
	List protocols;
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getBitsperchar() {
		return bitsperchar;
	}

	public void setBitsperchar(String bitsperchar) {
		this.bitsperchar = bitsperchar;
	}

	public String getStopbits() {
		return stopbits;
	}

	public void setStopbits(String stopbits) {
		this.stopbits = stopbits;
	}

	public String getParity() {
		return parity;
	}

	public void setParity(String parity) {
		this.parity = parity;
	}

	public List getProtocols() {
		return protocols;
	}

	public void setProtocols(List protocol) {
		this.protocols = protocol;
	}

}
