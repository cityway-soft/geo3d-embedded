package org.avm.hmi.swt.management;

public interface ItemNetworkListener {

	void adressChanged(String oldad, String newad);

	void connect();

	void disconnect();

	void isConnected();

}
