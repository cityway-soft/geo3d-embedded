/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.34
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.avm.device.fm6000.wifi.jni;

public class COMVS_WIFI implements COMVS_WIFIConstants {
  public static short Comvs_IsWifiCard() {
    return COMVS_WIFIJNI.Comvs_IsWifiCard();
  }

  public static short Comvs_IsConnectedToNetwork() {
    return COMVS_WIFIJNI.Comvs_IsConnectedToNetwork();
  }

  public static int Comvs_SetWifiPreferedNetwork(COMVS_WIFIDEVICE_CONFIGURATION arg0) {
    return COMVS_WIFIJNI.Comvs_SetWifiPreferedNetwork(COMVS_WIFIDEVICE_CONFIGURATION.getCPtr(arg0), arg0);
  }

  public static int Comvs_DeleteWifiPreferedNetwork(String arg0) {
    return COMVS_WIFIJNI.Comvs_DeleteWifiPreferedNetwork(arg0);
  }

  public static int Comvs_DeleteWifiPreferedNetworks() {
    return COMVS_WIFIJNI.Comvs_DeleteWifiPreferedNetworks();
  }

  public static int Comvs_GetSignalStrength(SWIGTYPE_p_int OUTPUT) {
    return COMVS_WIFIJNI.Comvs_GetSignalStrength(SWIGTYPE_p_int.getCPtr(OUTPUT));
  }

}
