%module COMVS_WIFI

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("comvs_wifi");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}

%{
#include <windows.h>
#include <COMVS_WifiDevice.h>
%}

#include <COMVS_types.h>

#define WEP_KEY_SIZE_MAX	32
#define SSID_SIZE_MAX		32
#define MAC_SIZE			6

typedef enum {
	SUCCESS	= 0, 
	ERR_WIFI_NO_CARD = -1,
	ERR_WIFI_NO_INTERF = -2,
	ERR_WIFI_NO_CONFIG = -3,
	ERR_WIFI_BAD_SSID = -4,
	ERR_WIFI_SIZE_DATA = -5,
	ERR_WIFI_BAD_POINTER = -6,
	ERR_WIFI = -7,
} COMVS_WIFI_STATUS;
	
typedef struct _COMVS_WIFIDEVICE_EAPOL_PARAMS
{
  COMVS_INT     iEnable8021x;
  COMVS_ULONG   ulEapFlags;
  COMVS_ULONG   ulEapType;
  COMVS_ULONG   ulAuthDataLen;
  char   *pucAuthData;
  // COMVS_UCHAR   *pucAuthData;
  void	*reserved;
} COMVS_WIFIDEVICE_EAPOL_PARAMS;

typedef struct _COMVS_WIFIDEVICE_WEP_CONFIGURATION
{
	COMVS_ULONG  ulWEP_key_index;
	COMVS_ULONG   ulWEP_key_size;
	char   ucWEP_key[WEP_KEY_SIZE_MAX];
	//COMVS_UCHAR   ucWEP_key[WEP_KEY_SIZE_MAX];
	void	*reserved;
} COMVS_WIFIDEVICE_WEP_CONFIGURATION;


typedef struct _COMVS_WIFIDEVICE_CONFIGURATION
{
	char ucSSID[SSID_SIZE_MAX];
	//COMVS_UCHAR ucSSID[SSID_SIZE_MAX];
	COMVS_INT iWifi_mode_authentication;
	COMVS_INT iMode_infrastructure;
	COMVS_ULONG ulMode_cipher;
	COMVS_WIFIDEVICE_WEP_CONFIGURATION stWEP_config ;
	COMVS_WIFIDEVICE_EAPOL_PARAMS st802_1x_mode_authentication;
	COMVS_ULONG nbOfConfig;
	void	*reserved;
}COMVS_WIFIDEVICE_CONFIGURATION;

 
COMVS_BOOL Comvs_IsWifiCard(void);
COMVS_BOOL Comvs_IsConnectedToNetwork(void);
COMVS_INT Comvs_SetWifiPreferedNetwork(COMVS_WIFIDEVICE_CONFIGURATION *);
COMVS_INT Comvs_DeleteWifiPreferedNetwork(char *);
COMVS_INT Comvs_DeleteWifiPreferedNetworks(void);
COMVS_INT Comvs_GetSignalStrength(COMVS_INT* OUTPUT);

