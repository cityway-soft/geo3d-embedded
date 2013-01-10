package org.avm.device.fm6000.network.jni;

public interface COMVS_NETCONFIGConstants {

	// Global Network Settings
	public static int COMVS_ROAMING_GLOBAL_PROPERTY_DHCP = 1;

	// Set to 1 for enabling Auto COnfiguration on all interfaces, 0 else
	public static int COMVS_ROAMING_GLOBAL_PROPERTY_AUTO_CONFIGURE = 2;

	// Set to 1 for enabling dead gateway detection, 0 else
	public static int COMVS_ROAMING_GLOBAL_PROPERTY_DEAD_GATEWAY_DETECTION = 3;

	// Set to 1 for enabling routing via the plateform
	public static int COMVS_ROAMING_GLOBAL_PROPERTY_IP_ENABLE_ROUTER = 4;

	// Interface Settings

	// READ-ONLY properties

	// Index of the interface
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_INDEX = 1;

	// Status
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_STATUS = 2;

	// Network speed
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_SPEED = 3;

	// Kind
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_TYPE = 4;

	// Mac Address
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_MAC = 5;

	// READ-WRITE Properties

	// MTU
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_MTU = 7;

	// Subnet-mask
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_SUBNET_MASK = 8;

	// Auto-configure
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_AUTO_CONFIGURE = 9;

	// Auto_interval
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_AUTO_INTERVAL = 10;

	// Auto-ip
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_AUTO_IP = 11;

	// Auto-mask
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_AUTO_MASK = 12;

	// Default-gateway
	public static int COMVS_ROAMING_INTERFACE_PROPERTY_DEFAULT_GATEWAY = 13;

	// Values that can be taken by the COMVS_ROAMING_INTERFACE_PROPERTY_STATUS
	public static int COMVS_ROAMING_INTERFACE_STATUS_NON_OPERATIONAL = 1;

	public static int COMVS_ROAMING_INTERFACE_STATUS_UNREACHABLE = 2;

	public static int COMVS_ROAMING_INTERFACE_STATUS_DISCONNECTED = 3;

	public static int COMVS_ROAMING_INTERFACE_STATUS_CONNECTING = 4;

	public static int COMVS_ROAMING_INTERFACE_STATUS_CONNECTED = 5;

	public static int COMVS_ROAMING_INTERFACE_STATUS_OPERATIONAL = 6;

	public static int COMVS_ROAMING_INTERFACE_STATUS_UNKNOWN = 7;

	// Values that can be taken by the COMVS_ROAMING_INTERFACE_PROPERTY_TYPE
	public static int COMVS_ROAMING_INTERFACE_TYPE_ETHERNET = 1;

	public static int COMVS_ROAMING_INTERFACE_TYPE_TOKENRING = 2;

	public static int COMVS_ROAMING_INTERFACE_TYPE_FDDI = 3;

	public static int COMVS_ROAMING_INTERFACE_TYPE_PPP = 4;

	public static int COMVS_ROAMING_INTERFACE_TYPE_LOOPBACK = 5;

	public static int COMVS_ROAMING_INTERFACE_TYPE_SLIP = 6;

	public static int COMVS_ROAMING_INTERFACE_TYPE_UNKNOWN = 7;

}
