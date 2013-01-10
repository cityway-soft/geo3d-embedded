package org.avm.device.fm6000.network.jni;

public class COMVS_NETCONFIGJNI {

	static {
		try {
			System.loadLibrary("comvs_netconfig");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load. \n" + e);
		}
	}

	public native static int Comvs_AddRouteToInterface(String adpaterName,
			COMVS_NETCONFIG_ROUTE r) throws NetConfigException;

	public native static int Comvs_DeleteRouteOfInterface(String adpaterName,
			COMVS_NETCONFIG_ROUTE r) throws NetConfigException;

	public native static int Comvs_ModifyRouteOfInterface(String adpaterName,
			COMVS_NETCONFIG_ROUTE oldRoute, COMVS_NETCONFIG_ROUTE newRoute)
			throws NetConfigException;

	public native static int Comvs_RenewDHCPLease(String adapterName)
			throws NetConfigException;

	public native static int Comvs_ReleaseDHCPLease(String adapterName)
			throws NetConfigException;

	public native static int Comvs_SetGlobalProperty(int property, Object obj)
			throws NetConfigException;

	public native static int Comvs_GetGlobalProperty(int property, Object obj)
			throws NetConfigException;

	public native static int Comvs_SetDHCPConfiguration(String adapterName,
			COMVS_NETCONFIG_DCHP_CONFIGURATION config)
			throws NetConfigException;

	public native static int Comvs_SetDNSConfiguration(String adapterName,
			COMVS_NETCONFIG_DNS_CONFIGURATION config) throws NetConfigException;

	public native static int Comvs_SetWINSConfiguration(String adapterName,
			COMVS_NETCONFIG_WINS_CONFIGURATION config)
			throws NetConfigException;

	public native static int Comvs_SetProperty(String adapterName,
			int property, Object obj) throws NetConfigException;

	public native static Object Comvs_GetProperty(String adapterName,
			int property) throws NetConfigException;

	public native static int Comvs_ReleaseIPAddress(String adapterName,
			COMVS_NETCONFIG_IP ip) throws NetConfigException;

	public native static int Comvs_RegisterIPAddress(String adapterName,
			COMVS_NETCONFIG_IP ip, COMVS_NETCONFIG_IP mask)
			throws NetConfigException;

	public native static COMVS_NETCONFIG_ROUTE[] Comvs_ListRoutesOfInterface(
			String adapterName) throws NetConfigException;

	public native static COMVS_NETCONFIG_ROUTE[] Comvs_ListRoutes()
			throws NetConfigException;

	public native static COMVS_NETCONFIG_IP[] Comvs_ListIPAddresses(
			String adapterName) throws NetConfigException;

	public native static COMVS_NETCONFIG_IP[] Comvs_ListIP()
			throws NetConfigException;

	public native static String[] Comvs_ListInterfaces()
			throws NetConfigException;
}
