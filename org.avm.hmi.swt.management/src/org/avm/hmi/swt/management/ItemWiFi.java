package org.avm.hmi.swt.management;

import org.eclipse.swt.widgets.Composite;

public class ItemWiFi extends AbstractItemNetwork {

		private static final String WIFI_CONNECT = "wifi_connect";
		private static final String WIFI_DISCONNECT = "wifi_disconnect";
		private static final String WIFI_ISCONNECTED = "wifi_isconnected";


		public ItemWiFi(Composite parent, int style) {
			super(parent, style);
			setText(Messages.getString("ItemNetwork.wifi-ftp"));
			setServerTitle(Messages
			.getString("ItemNetwork.addr-server-ftp"));
			setClientTitle(Messages
			.getString("ItemNetwork.addr-wifi"));
		}


} // @jve:decl-index=0:visual-constraint="10,10"
