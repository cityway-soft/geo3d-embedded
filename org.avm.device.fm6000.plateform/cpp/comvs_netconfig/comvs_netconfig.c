/******************************************************************************
 * @copyright (c) 2007 B2i., All Rights Reserved
 *
 * B2i confidential file protected by international copyright laws
 * UNAUTHORIZED ACCESS, USE, REPRODUCTION OR DISTRIBUTION IS PROHIBITED.
 *****************************************************************************/

/******************************************************************************
 * @copyright (c) 2007 B2i., All Rights Reserved
 *
 * B2i confidential file protected by international copyright laws
 * UNAUTHORIZED ACCESS, USE, REPRODUCTION OR DISTRIBUTION IS PROHIBITED.
 * 
 * B2i makes no representations concerning either the merchantability 
 * of this software or the suitability of this software for any particular purpose.
 * It is provided "as is" without express or implied warranty of any kind.
 * 
 * These notices must be retained in any copies of any part of this 
 * documentation and/or software. 
 *****************************************************************************/

#include <jni.h>
#include <windows.h>
#include <COMVS_Loglib.h>
#include <COMVS_NetConfig.h>
#include "JNIHelper.h"
#include "comvs_netconfig.h"


int GetCOMVS_NETCONFIG_IPFromJavaObject(COMVS_NETCONFIG_IP * ip,JNIEnv * env, jclass cls,jobject object, char * field);
int CreateCOMVS_NETCONFIG_IPFromJavaObject(COMVS_NETCONFIG_IP * ip,JNIEnv * env, jclass netconfig_object,jobject object);
int CreateDHCPConfigurationFromJavaObject(COMVS_NETCONFIG_DHCP_CONFIGURATION * r, JNIEnv * env, jclass netconfig_object,jobject object);
int CreateDNSConfigurationFromJavaObject(COMVS_NETCONFIG_DNS_CONFIGURATION * r, JNIEnv * env, jclass netconfig_object,jobject object);
int CreateWINSConfigurationFromJavaObject(COMVS_NETCONFIG_WINS_CONFIGURATION * r, JNIEnv * env, jclass netconfig_object,jobject object);
int CreateRouteFromJavaObject(COMVS_NETCONFIG_ROUTE * r, JNIEnv * env, jclass netconfig_object,jobject object);
int GetCOMVS_NETCONFIG_IPFromCObject(COMVS_NETCONFIG_IP * ip,JNIEnv * env, jclass cls,jobject object, char * field);

int CreateCOMVS_NETCONFIG_IPFromCObject(COMVS_NETCONFIG_IP * ip, JNIEnv * env,jobject * ip_object);
int CreateRouteFromCObject(COMVS_NETCONFIG_ROUTE * r, JNIEnv * env,jobject * route_object);


/* Error messages in EXception if required */
int sendIntReplyOrThrowError(JNIEnv * env,int err);
void * sendPtrReplyOrThrowError(JNIEnv * env,int err,void * ptr);

int sendIntReplyOrThrowError(JNIEnv * env,int err){
  char * msg = Comvs_GetNetconfigErrorMessage((COMVS_NETCONFIG_ERROR)err);
  if (msg != NULL){
    sendExceptionWithPath(env,"org/avm/device/fm6000/network/jni/NetConfigException",msg);
    return err;
  } else {
    return err;
  }
}

void * sendPtrReplyOrThrowError(JNIEnv * env,int err,void * ptr){
  char * msg = Comvs_GetNetconfigErrorMessage((COMVS_NETCONFIG_ERROR)err);
  if (msg != NULL){
    sendExceptionWithPath(env,"org/avm/device/fm6000/network/jni/NetConfigException",msg);
    return NULL;
  }else {
    return ptr;
  }
}

jbyteArray CreateByteArrayFromCObject(byte mac[6], JNIEnv * env){
  int err = 0;
  int i = 0;
  jbyteArray mac_object = NULL;
  
  mac_object = (*env)->NewByteArray(env,6);
  if (mac_object == NULL){
	fprintf(stdout,"NewByteArray failed");
  }
  JNI_REQUIRE_NULLERR_NULL_RETURN(mac_object, " -> error in NewByteArray");
  
  (*env)->SetByteArrayRegion(env,mac_object,0,6,mac);
  if (mac_object == NULL){
	fprintf(stdout,"SetByteArrayRegion failed");
  }

  return mac_object;
}

int CreateRouteFromCObject(COMVS_NETCONFIG_ROUTE * r, JNIEnv * env, jobject * route_object){
	jclass route_class = 0;
	jmethodID method_id = 0;
	COMVS_INT metric = 0;
	COMVS_INT index = 0;
	jobject ip;
	jobject mask;
	jobject gateway;
	int err = 0;
	char *msgHeader = NULL;
	
	
	route_class = (*env)->FindClass(env, "org/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE");
	JNI_REQUIRE_NULLERR(route_class, " -> error in FindClass");
	
	err = CreateCOMVS_NETCONFIG_IPFromCObject(&(r->destinationIp), env, &ip);
	JNI_REQUIRE_NOERR(err, " -> error in CreateCOMVS_NETCONFIG_IPFromCObject");
	
	err = CreateCOMVS_NETCONFIG_IPFromCObject(&(r->mask),env,&mask);
	JNI_REQUIRE_NOERR(err, " -> error in CreateCOMVS_NETCONFIG_IPFromCObject");
	
	err = CreateCOMVS_NETCONFIG_IPFromCObject(&(r->gateway),env,&gateway);
	JNI_REQUIRE_NOERR(err,  " -> error in CreateCOMVS_NETCONFIG_IPFromCObject");
	
	method_id = (*env)->GetMethodID(env, route_class, "<init>", "(Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;II)V");
	JNI_REQUIRE_NULLERR(method_id,  " -> error in GetMethodID");
	
	*route_object = (*env)->NewObject(env, route_class, method_id,ip,mask,gateway,r->metric,r->interfaceIndex);
	JNI_REQUIRE_NULLERR(*route_object,  " -> error in NewObject");
	return 0;
}

int CreateCOMVS_NETCONFIG_IPFromCObject(COMVS_NETCONFIG_IP * ip, JNIEnv * env,jobject * ip_object){
	jclass ip_class = 0;
	jmethodID method_id = 0;
	jint a = 0;jint b = 0;jint c = 0;jint d = 0;
	char *msgHeader = NULL;
	
	
	ip_class = (*env)->FindClass(env, "org/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP");
	JNI_REQUIRE_NULLERR(ip_class,  " -> error in FindClass");
	
	method_id = (*env)->GetMethodID(env, ip_class, "<init>", "(IIII)V");
	JNI_REQUIRE_NULLERR(method_id,  " -> error in GetMethodID");
	
	DottedIPFromInt(ip->ipv4,a,b,c,d);
	*ip_object = (*env)->NewObject(env, ip_class, method_id,a,b,c,d);
	JNI_REQUIRE_NULLERR(*ip_object,  " -> error in NewObject");
	
	if (ip_class == NULL) {
     	return -1;
	}
	return 0;
 
}

int CreateIntegerObject( JNIEnv * env,jobject * integer_object,int value){
	jclass integer_class = 0;
	jmethodID method_id = 0;
	jint a = 0;
	char *msgHeader = NULL;
	
	
	integer_class = (*env)->FindClass(env, "java/lang/Integer");
	JNI_REQUIRE_NULLERR(integer_class,  " -> error in FindClass");
	
	method_id = (*env)->GetMethodID(env, integer_class, "<init>", "(I)V");
	JNI_REQUIRE_NULLERR(method_id,  " -> error in GetMethodID");
	
	*integer_object = (*env)->NewObject(env, integer_class, method_id,value);
	JNI_REQUIRE_NULLERR(integer_object,  " -> error in NewObject");
	
	if (integer_object == NULL) {
     	return -1;
	}
	return 0;
}


/* Map a JAVA COMVS_NETCONFIG_IP Object to a C COMVS_NETCONFIG_IP Object */
int CreateCOMVS_NETCONFIG_IPFromJavaObject(COMVS_NETCONFIG_IP * ip,JNIEnv * env, jclass ip_class,jobject ip_object){
	int a,b,c,d;
	int err = 0;
	
	err = JNIGetIntField(env,ip_class,"a",ip_object,&a);
	err = JNIGetIntField(env,ip_class,"b",ip_object,&b)&err;
	err = JNIGetIntField(env,ip_class,"c",ip_object,&c)&err;
	err = JNIGetIntField(env,ip_class,"d",ip_object,&d)&err;
	ip->ipv4 = IntFromDottedIP(a,b,c,d);
	
	return sendIntReplyOrThrowError(env,err);
}


/* Map a JAVA DHCP COnfiguration Object to a C DHCP Configuration Object */
int CreateDHCPConfigurationFromJavaObject(COMVS_NETCONFIG_DHCP_CONFIGURATION * r, JNIEnv * env, jclass dhcp_class,jobject object){
	int err = 0;
	char *msgHeader = NULL;
	
	
	err = JNIGetBoolField(env,dhcp_class,"enabled",object,&(r->enabled));
	JNI_REQUIRE_NOERR(err, " -> error in JNIGetBoolField \"enabled\"");
	
	err = JNIGetBoolField(env,dhcp_class,"automatic",object,&(r->automatic));
	JNI_REQUIRE_NOERR(err, " -> error in JNIGetBoolField \"automatic\"");
	
	err = JNIGetIntField(env,dhcp_class,"maxRetryAttempt",object,&(r->maxRetryAttempt));
	JNI_REQUIRE_NOERR(err,  " -> error in JNIGetIntField");
	return sendIntReplyOrThrowError(env,err);
}

/* Map a JAVA DNS COnfiguration to a C DNS Configuration Object */
int CreateDNSConfigurationFromJavaObject(COMVS_NETCONFIG_DNS_CONFIGURATION * r, JNIEnv * env, jclass netconfig_object,jobject object){
	int err = 0;
	char *msgHeader = NULL;

	
	err = GetCOMVS_NETCONFIG_IPFromJavaObject(&(r->primaryDNS),env,netconfig_object,object,"primaryDNS");
	JNI_REQUIRE_NOERR(err,  " -> error in w²");
	
	err = GetCOMVS_NETCONFIG_IPFromJavaObject(&(r->secondaryDNS),env, netconfig_object,object,"secondaryDNS");
	JNI_REQUIRE_NOERR(err,  " -> error in GetCOMVS_NETCONFIG_IPFromJavaObject");
	
	err = JNIGetIntField(env,netconfig_object,"DNSTimeout",object,&(r->DNSTimeout));
	JNI_REQUIRE_NOERR(err,  " -> error in JNIGetIntField");
	
	return sendIntReplyOrThrowError(env,err);
}

/* Map a JAVA WINS COnfiguration Object to a C WINS Configuration Object */
int CreateWINSConfigurationFromJavaObject(COMVS_NETCONFIG_WINS_CONFIGURATION * r, JNIEnv * env, jclass netconfig_object,jobject object){
	int err = 0;
	char *msgHeader = NULL;
	
	err = GetCOMVS_NETCONFIG_IPFromJavaObject(&(r->primaryWINS),env,netconfig_object,object,"primaryWINS");
	JNI_REQUIRE_NOERR(err,  " -> error in GetCOMVS_NETCONFIG_IPFromJavaObject");
	
	err = GetCOMVS_NETCONFIG_IPFromJavaObject(&(r->secondaryWINS),env, netconfig_object,object,"secondaryWINS");
	JNI_REQUIRE_NOERR(err,  " -> error in GetCOMVS_NETCONFIG_IPFromJavaObject");
	
	return sendIntReplyOrThrowError(env,err);
}

/* Map a JAVA Route to a C Route Object */
int CreateRouteFromJavaObject(COMVS_NETCONFIG_ROUTE * r, JNIEnv * env, jclass route_class,jobject route_object){
	int err = 0;
	char *msgHeader = NULL;

	
	/* Create the destintation COMVS_NETCONFIG_IP C Object from the JAVA Object*/
	err = GetCOMVS_NETCONFIG_IPFromJavaObject(&(r->destinationIp),env,route_class,route_object,"destinationCOMVS_NETCONFIG_IP");
	JNI_REQUIRE_NOERR(err, " -> error in GetCOMVS_NETCONFIG_IPFromJavaObject");
		
	/* Create the mask COMVS_NETCONFIG_IP C Object from the Java Object*/
	err = GetCOMVS_NETCONFIG_IPFromJavaObject(&(r->mask),env,route_class,route_object,"mask");
	JNI_REQUIRE_NOERR(err, " -> error in GetCOMVS_NETCONFIG_IPFromJavaObject");
		
	/* Create the gateway COMVS_NETCONFIG_IP C object from the Java Object */
	err = GetCOMVS_NETCONFIG_IPFromJavaObject(&(r->gateway),env, route_class,route_object,"gateway");
	JNI_REQUIRE_NOERR(err, " -> error in GetCOMVS_NETCONFIG_IPFromJavaObject");
		
	/* Get the int metric field from the Java Object */
	err = JNIGetIntField(env,route_class,"metric",route_object,&(r->metric));
	JNI_REQUIRE_NOERR(err, " -> error in JNIGetIntField");
	
	/* Get the interface index int field from the Java Object */
	err = JNIGetIntField(env,route_class,"interfaceIndex",route_object,&(r->interfaceIndex));
	JNI_REQUIRE_NOERR(err, " -> error in JNIGetIntField");
		
	return sendIntReplyOrThrowError(env,err);
}

/* Retrieve an COMVS_NETCONFIG_IP Object from a Java Object*/
int GetCOMVS_NETCONFIG_IPFromJavaObject(COMVS_NETCONFIG_IP * ip,JNIEnv * env, jclass route_cls,jobject route_object, char * field){
	jfieldID fid = 0;
	jobject ipobj = 0;
	jclass ipclass = 0;
	int err = 0;
	char *msgHeader = NULL;

	
	fid = (*env)->GetFieldID(env, route_cls, field, "Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;");
	JNI_REQUIRE_NULLERR(fid,  " -> error in GetFieldID");
	
	ipobj = (*env)->GetObjectField(env, route_object, fid);
	JNI_REQUIRE_NULLERR(ipobj,  " -> error in GetObjectField");

	ipclass = (*env)->GetObjectClass(env,ipobj);
	JNI_REQUIRE_NULLERR(ipclass,  " -> error in GetObjectClass");
	
	err = CreateCOMVS_NETCONFIG_IPFromJavaObject(ip,env,ipclass,ipobj);
	JNI_REQUIRE_NOERR(err," -> error in CreateCOMVS_NETCONFIG_IPFromJavaObject");
	
	return 0;
}


/* *********************************** */
/*      Interface with JAVA STUFF      */
/* *********************************** */

/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    addRoute
 * Signature: (Ljava/lang/String;Lfr/b2i/comvs/jni/roaming/Route;)I
 */
 
JNIEXPORT jint JNICALL JNI_netconfig_addRoute(JNIEnv * env, jclass netconfig_object, jstring fieldName, jobject route_object){
	jclass route_class = 0;
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ROUTE route;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	char *msgHeader = NULL;

	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res,  " -> error in JNIGetString");
		
	route_class = (*env)->GetObjectClass(env, route_object);
	JNI_REQUIRE_NULLERR(route_class,  " -> error in GetObjectClass");
	
	res = CreateRouteFromJavaObject(&route, env, route_class, route_object);
	JNI_REQUIRE_NOERR(res,  " -> error in CreateRouteFromJavaObject");
	
	err = Comvs_AddRouteToInterface(adapter, &route);
	return sendIntReplyOrThrowError(env, err);
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    deleteRoute
 * Signature: (Ljava/lang/String;Lfr/b2i/comvs/jni/roaming/Route;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_deleteRoute(JNIEnv * env, jclass netconfig_object, jstring fieldName, jobject route_object){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	COMVS_NETCONFIG_ROUTE route;
	jclass route_class = 0;
	char *msgHeader = NULL;

	memset(adapter,0,sizeof(WCHAR)*256);

	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res," -> error in JNIGetString");
		
	route_class = (*env)->GetObjectClass(env,route_object);
	JNI_REQUIRE_NULLERR(route_class,  " -> error in GetObjectClass");

	res = CreateRouteFromJavaObject(&route,env,route_class,route_object);
	JNI_REQUIRE_NOERR(res," -> error in CreateRouteFromJavaObject");
	
	err = Comvs_DeleteRouteOfInterface(adapter,&route);
	return sendIntReplyOrThrowError(env,err);
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    modifiyRoute
 * Signature: (Ljava/lang/String;Lfr/b2i/comvs/jni/roaming/Route;Lfr/b2i/comvs/jni/roaming/Route;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_modifyRoute(JNIEnv * env, jclass netconfig_object, jstring fieldName, jobject old_route_object, jobject new_route_object){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ROUTE old_route;
	COMVS_NETCONFIG_ROUTE new_route;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jclass route_class = 0;
	char *msgHeader = NULL;
	

	res = JNIGetString(env, fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res," -> error in JNIGetString");
	
	route_class = (*env)->GetObjectClass(env,old_route_object);
	JNI_REQUIRE_NULLERR(route_class,  " -> error in GetObjectClass");
	
	res = CreateRouteFromJavaObject(&old_route,env,route_class,old_route_object);
	JNI_REQUIRE_NULLERR(res," -> error in CreateRouteFromJavaObject");
	
	route_class = (*env)->GetObjectClass(env,new_route_object);
	JNI_REQUIRE_NULLERR(route_class,  " -> error in GetObjectClass");
	
	res = CreateRouteFromJavaObject(&new_route,env,route_class,new_route_object);
	JNI_REQUIRE_NOERR(res," -> error in CreateRouteFromJavaObject");
	
	err = Comvs_DeleteRouteOfInterface(adapter,&old_route);	
	err = Comvs_AddRouteToInterface(adapter,&new_route);
	
	return sendIntReplyOrThrowError(env,err);
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    renewDHCPLease
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_renewDHCPLease(JNIEnv * env, jclass netconfig_object, jstring fieldName){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	char *msgHeader = NULL;

	
	res = JNIGetString(env, fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res, " -> error in JNIGetString");
		
	err = Comvs_RenewDHCPLease(adapter);
	return sendIntReplyOrThrowError(env,err);
}
/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    releaseDHCPLease
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_releaseDHCPLease(JNIEnv * env, jclass netconfig_object, jstring fieldName){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	char *msgHeader = NULL;
	
	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res, " -> error in JNIGetString");
	
	err = Comvs_ReleaseDHCPLease(adapter);
	return sendIntReplyOrThrowError(env,err);
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    setDHCPConfiguration
 * Signature: (Ljava/lang/String;Lfr/b2i/comvs/jni/roaming/configuration/DHCPConfiguration;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_setDHCPConfiguration(JNIEnv * env, jclass netconfig_object, jstring fieldName, jobject dhcp_conf_object){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_DHCP_CONFIGURATION dhcpConfiguration;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jclass dhcp_class = 0;
	char *msgHeader = NULL;
	
	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res, " -> error in JNIGetString");
	
	dhcp_class = (*env)->GetObjectClass(env,dhcp_conf_object);
	JNI_REQUIRE_NULLERR(dhcp_class,  " -> error in GetObjectClass");
		
	res = CreateDHCPConfigurationFromJavaObject(&dhcpConfiguration,env,dhcp_class,dhcp_conf_object);
	JNI_REQUIRE_NULLERR(res, " -> error in CreateDHCPConfigurationFromJavaObject");
	
	err = Comvs_SetDHCPConfiguration(adapter,&dhcpConfiguration);	
	return sendIntReplyOrThrowError(env,err);
}
/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    setDNSConfiguration
 * Signature: (Ljava/lang/String;Lfr/b2i/comvs/jni/roaming/configuration/DNSConfiguration;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_setDNSConfiguration(JNIEnv * env, jclass netconfig_object, jstring fieldName, jobject dns_conf_object){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_DNS_CONFIGURATION dnsConfiguration;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jclass dns_class = 0;
	char *msgHeader = NULL;

	
	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res," -> error in JNIGetString");
	
	dns_class = (*env)->GetObjectClass(env,dns_conf_object);
	JNI_REQUIRE_NULLERR(dns_class,  " -> error in GetObjectClass");
	
	res = CreateDNSConfigurationFromJavaObject(&dnsConfiguration,env,dns_class,dns_conf_object);
	JNI_REQUIRE_NOERR(res," -> error in CreateDNSConfigurationFromJavaObject");
	
	err = Comvs_SetDNSConfiguration(adapter,&dnsConfiguration);	
	return sendIntReplyOrThrowError(env,err);
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    setDNSConfiguration
 * Signature: (Ljava/lang/String;Lfr/b2i/comvs/jni/roaming/configuration/DNSConfiguration;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_setWINSConfiguration(JNIEnv * env, jclass netconfig_object, jstring fieldname, jobject wins_conf_object){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_WINS_CONFIGURATION winsConfiguration;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jclass wins_class = 0;
	char *msgHeader = NULL;
	
	res = JNIGetString(env,  fieldname,(unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res," -> error in JNIGetString");
	
	wins_class = (*env)->GetObjectClass(env,wins_conf_object);
	JNI_REQUIRE_NULLERR(wins_class,  " -> error in GetObjectClass");
		
	res = CreateWINSConfigurationFromJavaObject(&winsConfiguration,env,wins_class,wins_conf_object);
	JNI_REQUIRE_NOERR(res," -> error in Comvs_SetWINSConfiguration");
		
	err = Comvs_SetWINSConfiguration(adapter,&winsConfiguration);
	return sendIntReplyOrThrowError(env,err);
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    setInterfaceProperty
 * Signature: (Ljava/lang/String;ILjava/lang/Object;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_setInterfaceProperty(JNIEnv * env, jclass netconfig_object, jstring fieldName, jint property, jobject object){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	char *msgHeader = NULL;

	
	res = JNIGetString(env,  fieldName,(unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res," -> error in JNIGetString");
	
	if (res != 1) return -1;
		switch(property){
			case COMVS_NETCONFIG_INTERFACE_PROPERTY_MTU:
			
			case COMVS_NETCONFIG_INTERFACE_PROPERTY_AUTO_CONFIGURE:
			case COMVS_NETCONFIG_INTERFACE_PROPERTY_AUTO_INTERVAL:
			case COMVS_NETCONFIG_INTERFACE_PROPERTY_AUTO_IP:
			case COMVS_NETCONFIG_INTERFACE_PROPERTY_AUTO_MASK:{
        COMVS_INT propertyInt = 0;
        err = Comvs_SetProperty(adapter,property,&propertyInt);
      }
      
			case COMVS_NETCONFIG_INTERFACE_PROPERTY_SUBNET_MASK:
			case COMVS_NETCONFIG_INTERFACE_PROPERTY_DEFAULT_GATEWAY:;
			
			default:;
	}
	return -1;
}
/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    getInterfaceProperty
 * Signature: (Ljava/lang/String;ILjava/lang/Object;)I
 */
JNIEXPORT jobject JNICALL JNI_netconfig_getInterfaceProperty(JNIEnv * env, jclass netconfig_object, jstring fieldName, jint property)
{
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jobject final_object = NULL;
	char *msgHeader = NULL;
	
	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR_NULL_RETURN(res," -> error in JNIGetString");
	
	switch(property){
		case COMVS_NETCONFIG_INTERFACE_PROPERTY_STATUS:
		case COMVS_NETCONFIG_INTERFACE_PROPERTY_TYPE:
		case COMVS_NETCONFIG_INTERFACE_PROPERTY_MTU:
		case COMVS_NETCONFIG_INTERFACE_PROPERTY_INDEX:
		case COMVS_NETCONFIG_INTERFACE_PROPERTY_SPEED:
		{
			COMVS_INT propertyInt = 0;
			err = Comvs_GetProperty(adapter,property,&propertyInt);
			if (err == kNetConfigNoError){
				err = CreateIntegerObject(env,&final_object,propertyInt);
			} else  {
				sendPtrReplyOrThrowError(env,err,NULL);   
			}
		};break;
		
	    case COMVS_NETCONFIG_INTERFACE_PROPERTY_MAC:
	    {
		    jbyteArray  mac_array = NULL;
		    int i = 0;
		    byte mac[6]= {0,0,0,0,0,0};
		    err = Comvs_GetProperty(adapter,property,mac);
		    if (err == kNetConfigNoError){
				mac_array = (*env)->NewByteArray(env,6);
				(*env)->SetByteArrayRegion(env,mac_array,0,6,mac);
		    } else  {
		        ForceLogInfo("COMVS_NETCONFIG_INTERFACE_PROPERTY_MAC is not ok! ");
		        sendPtrReplyOrThrowError(env,err,NULL);   
		    }
		    return mac_array;
	    }; break;
    /*
		COMVS_NETCONFIG_IP
	*/
		case COMVS_NETCONFIG_INTERFACE_PROPERTY_SUBNET_MASK:
		case COMVS_NETCONFIG_INTERFACE_PROPERTY_DEFAULT_GATEWAY:
		{
			COMVS_NETCONFIG_IP ip;
			err = Comvs_GetProperty(adapter,property,&ip);
			if (err == kNetConfigNoError){
				CreateCOMVS_NETCONFIG_IPFromCObject(&ip,env,&final_object);
			} else {
				return NULL;
			}
		}
			
    default:;// throws an exceptino here;
	};
	 
  return final_object;
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    setGlobalProperty
 * Signature: (ILjava/lang/Object;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_setGlobalProperty(JNIEnv * env, jclass netconfig_object, jint property, jobject object){
  COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
  char *msgHeader = NULL;

	
 	switch(property){
		case COMVS_NETCONFIG_GLOBAL_PROPERTY_DHCP:
		case COMVS_NETCONFIG_GLOBAL_PROPERTY_AUTO_CONFIGURE:
		case COMVS_NETCONFIG_GLOBAL_PROPERTY_DEAD_GATEWAY_DETECTION:
		case COMVS_NETCONFIG_GLOBAL_PROPERTY_IP_ENABLE_ROUTER:
    {
      jclass cls = 0;
      jmethodID mid = 0; 
      jint value = 0;
      jobject object = 0;
       
      cls = (*env)->FindClass(env, "java/lang/Integer");
      JNI_REQUIRE_NULLERR(cls,  " -> error in FindClass");
      
      mid = (*env)->GetMethodID(env, cls, "<init>", "(Ljava/lang/Object)V");
      JNI_REQUIRE_NULLERR(mid,  " -> error in GetMethodID");
      
      object = (*env)->NewObject(env, cls, mid,object);
      JNI_REQUIRE_NULLERR(mid,  " -> error in NewObject");
      
      cls = (*env)->GetObjectClass(env,object);
      JNI_REQUIRE_NULLERR(mid,  " -> error in GetObjectClass");
      
      mid = (*env)->GetMethodID(env, cls, "intValue", "()I");
      JNI_REQUIRE_NULLERR(mid,  " -> error in GetMethodID");
      
      value = (*env)->CallIntMethod(env, cls, mid);
      
      err = Comvs_SetGlobalProperty(property,&value);
    }
		default:;
	}
	return err;
}
/*
 * Class:     fr_b2i_comvs_jni_netconfig_Roaming
 * Method:    getGlobalProperty
 * Signature: (ILjava/lang/Object;)I
 */
JNIEXPORT jobject JNICALL JNI_netconfig_getGlobalProperty(JNIEnv * env, jclass netconfig_object, jint property){
  COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
  jobject final_object = NULL;
  char *msgHeader = NULL;

	
	switch(property){
	
		case COMVS_NETCONFIG_GLOBAL_PROPERTY_DHCP:
		case COMVS_NETCONFIG_GLOBAL_PROPERTY_AUTO_CONFIGURE:
		case COMVS_NETCONFIG_GLOBAL_PROPERTY_DEAD_GATEWAY_DETECTION:
		case COMVS_NETCONFIG_GLOBAL_PROPERTY_IP_ENABLE_ROUTER:
		{
      COMVS_INT propertyInt = 0;
      err = Comvs_GetGlobalProperty(property,&propertyInt);
      if (err == kNetConfigNoError){
        err = CreateIntegerObject(env,&final_object,propertyInt);
        JNI_REQUIRE_NOERR_NULL_RETURN(err,  " -> error in CreateIntegerObject");
      } else {
        final_object = NULL;
      }
    };break;
   
		default:;
	}
	return final_object;
}
/*
 * Class:     fr_b2i_comvs_jni_netconfig_NetConfig
 * Method:    releaseCOMVS_NETCONFIG_IPAdress
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_releaseIPAdress(JNIEnv * env, jclass netconfig_object, jstring fieldName, jobject ip_obj){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_IP ip;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jclass ip_class = 0;
	char *msgHeader = NULL;

	
	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res," -> error in JNIGetString");
		
	ip_class = (*env)->GetObjectClass(env,ip_obj);
	JNI_REQUIRE_NULLERR(ip_class,  " -> error in GetObjectClass");
	
	res =  CreateCOMVS_NETCONFIG_IPFromJavaObject(&ip,env, ip_class, ip_obj);
	JNI_REQUIRE_NOERR(res, " -> error in CreateCOMVS_NETCONFIG_IPFromJavaObject");

	err = Comvs_ReleaseIPAddress(adapter,&ip);	
	return sendIntReplyOrThrowError(env,err);
}


/*
 * Class:     fr_b2i_comvs_jni_netconfig_NetConfig
 * Method:    registerCOMVS_NETCONFIG_IPAdress
 * Signature: (Ljava/lang/String;Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;)I
 */
JNIEXPORT jint JNICALL JNI_netconfig_registerIPAdress(JNIEnv * env, jclass netconfig_object, jstring fieldName, jobject ip_obj, jobject mask_obj){
	unsigned char res = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_IP ip,mask;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jclass ip_class,ip_mask_class = 0;
	char *msgHeader = NULL;

	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR(res, " -> error in JNIGetString");

	ip_class = (*env)->GetObjectClass(env,ip_obj);
	JNI_REQUIRE_NULLERR(ip_class,  " -> error in GetObjectClass");
	
	res =  CreateCOMVS_NETCONFIG_IPFromJavaObject(&ip,env, ip_class, ip_obj);
	JNI_REQUIRE_NOERR(res, " -> error in CreateCOMVS_NETCONFIG_IPFromJavaObject");
	
	ip_mask_class = (*env)->GetObjectClass(env,mask_obj);
	JNI_REQUIRE_NULLERR(ip_mask_class,  " -> error in GetObjectClass");
	
	res =  CreateCOMVS_NETCONFIG_IPFromJavaObject(&mask,env, ip_mask_class, mask_obj);
	JNI_REQUIRE_NOERR(res, " -> error in CreateCOMVS_NETCONFIG_IPFromJavaObject");

	err = Comvs_RegisterIPAddress(adapter,&ip,&mask);	
	return sendIntReplyOrThrowError(env,err);
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_NetConfig
 * Method:    listRoutesOfInterface
 * Signature: (Ljava/lang/String;)[Lorg/avm/device/fm6000/network/jni/Route;
 */

JNIEXPORT jobjectArray JNICALL JNI_netconfig_listRoutesOfInterface(JNIEnv *env, jclass netconfig_class, jstring fieldName){

	unsigned char res = 0;
	COMVS_NETCONFIG_ROUTE * routes = NULL;
	COMVS_INT count = 0;
	int i  = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jobjectArray jroutes;
	jclass jroute_class = 0;
	unsigned int outputStringSize = 0;
	char *msgHeader = NULL;
	
	jroute_class = (*env)->FindClass(env, "org/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE");
	JNI_REQUIRE_NULLERR_NULL_RETURN(jroute_class," -> error in JNIGetString"); 
	
	res = JNIGetString(env, fieldName, (unsigned short *)adapter, 256);
	JNI_REQUIRE_NOERR_NULL_RETURN(res," -> error in JNIGetString"); 
	
	routes = Comvs_ListRoutesOfInterface(adapter,&count);
	
	if (count > 0){
		jobject * new_object_array = (jobject *) LocalAlloc(LPTR,sizeof(jobject)*count);
		if (new_object_array == NULL){
			ForceLogInfo("Memory alllocation failed");return NULL;
		}
		jroutes = (*env)->NewObjectArray(env,count, jroute_class, NULL);
		if (jroutes == 0){
			ForceLogDebug("error in JNI_netconfig_listRoutesOfInterface NewObjectArray ");return NULL;
		}
		
		for(i=0; i<count; i++){
			CreateRouteFromCObject(&(routes[i]),env,&(new_object_array[i]));
			(*env)->SetObjectArrayElement(env, jroutes,i,(new_object_array[i]));
		}
		/*TODO memory leaks on new_object_array*/
		return jroutes;
	} else {
		ForceLogInfo("routes is NULL");
	    return sendPtrReplyOrThrowError(env,count,NULL);
	}
}



/*
 * Class:     fr_b2i_comvs_jni_netconfig_NetConfig
 * Method:    listRoutes
 * Signature: ()[Lorg/avm/device/fm6000/network/jni/Route;
 */
JNIEXPORT jobjectArray JNICALL JNI_netconfig_listRoutes(JNIEnv *env, jclass netconfig_class ){
	unsigned char res = 0;
	COMVS_NETCONFIG_ROUTE * routes = NULL;
	COMVS_INT count = 0;
	int i  = 0;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jobjectArray jroutes;
	jclass jroute_class = 0;
	char *msgHeader = NULL;
	
	jroute_class = (*env)->FindClass(env, "org/avm/device/fm6000/network/jni/COMVS_NETCONFIG_ROUTE");
	if  (jroute_class == 0){
		ForceLogDebug("error dans JNI_netconfig_listRoutes ");return NULL;
	}
	
	routes = Comvs_ListRoutes(&count);
	
	if (count > 0){
	 /* TODO CHECK WHO MUST BE RELEASING THIS MEMORY*/
		jobject * new_object_array = (jobject *) LocalAlloc(LPTR,sizeof(jobject)*count);
		jroutes = (*env)->NewObjectArray(env,count, jroute_class, NULL);
		if (jroutes == 0){
			ForceLogDebug("error dans JNI_netconfig_listRoutes NewObjectArray ");return NULL;
		}
		for(i=0; i<count; i++){
			CreateRouteFromCObject(&(routes[i]),env,&(new_object_array[i]));
			(*env)->SetObjectArrayElement(env, jroutes,i,(new_object_array[i]));
		}
		return jroutes;
	} else {
    return sendPtrReplyOrThrowError(env,count,NULL);
  }
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_NetConfig
 * Method:    listCOMVS_NETCONFIG_IPAdresses
 * Signature: (Ljava/lang/String;)[Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;
 */
JNIEXPORT jobjectArray JNICALL JNI_netconfig_listIPAdresses(JNIEnv *env, jclass netconfig_class, jstring fieldName)
{
	unsigned char res = 0;
	COMVS_NETCONFIG_IP * ips = NULL;
	COMVS_INT count = 0;
	int i  = 0;
	unsigned short adapter[256];
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jobjectArray jips;
	jclass jip_class = 0;
	char *msgHeader = NULL;
	
	jip_class = (*env)->FindClass(env, "org/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP");
	if  (jip_class == 0){
		ForceLogDebug("error dans Comvs_ListIPAddresses ");return NULL;
	}
	
	res = JNIGetString(env,  fieldName, (unsigned short *)adapter, 256);
	if (res != 0){
		ForceLogDebug("error dans JNIGetString in  JNI_netconfig_listIPAdresses");return NULL;
	}
	
	ips = Comvs_ListIPAddresses(adapter,&count);

	if (count > 0){
	 /* TODO CHECK WHO MUST BE RELEASING THIS MEMORY*/
		jobject * new_object_array = (jobject *) LocalAlloc(LPTR,sizeof(jobject)*count);
		jips = (*env)->NewObjectArray(env,count, jip_class, NULL);
		if (jips == 0){
			ForceLogDebug("error dans JNI_netconfig_listIPAdresses NewObjectArray ");return NULL;
		}
		for(i=0; i<count; i++){
			CreateCOMVS_NETCONFIG_IPFromCObject(&(ips[i]),env,&(new_object_array[i]));
			(*env)->SetObjectArrayElement(env, jips,i,(new_object_array[i]));
		}
		//if(ips != NULL){
		//}

      //LocalFree(ips);
    
		return jips;
	} else {
	   return sendPtrReplyOrThrowError(env,count,NULL);
	}
	return sendPtrReplyOrThrowError(env,-1,NULL);
}

/*
 * Class:     fr_b2i_comvs_jni_netconfig_NetConfig
 * Method:    listCOMVS_NETCONFIG_IP
 * Signature: ()[Lorg/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP;
 */
JNIEXPORT jobjectArray JNICALL JNI_netconfig_listIP(JNIEnv *env, jclass netconfig_class){
	unsigned char res = 0;
	COMVS_NETCONFIG_IP * ips;
	COMVS_INT count = 0;
	int i  = 0;
	COMVS_NETCONFIG_ERROR err = kNetConfigNoError;
	jobjectArray jips;
	jclass jip_class = (*env)->FindClass(env, "org/avm/device/fm6000/network/jni/COMVS_NETCONFIG_IP");

	ips = Comvs_ListIP(&count);
	
	if (count > 0){
		jobject * new_object_array = (jobject *)LocalAlloc(LPTR,sizeof(jobject)*count);
		jips = (*env)->NewObjectArray(env,count, jip_class, NULL);
		if (jips == 0){
			ForceLogDebug("error dans JNI_netconfig_listIP NewObjectArray ");return NULL;
		}
		for(i=0; i<count; i++){
			CreateCOMVS_NETCONFIG_IPFromCObject(&(ips[i]),env,&(new_object_array[i]));
			(*env)->SetObjectArrayElement(env, jips,i,new_object_array[i]);
		}
		if (ips != NULL){
			/*TODO memory leaks on new_object_array*/
			/* TODO memory leaks on COMVS_NETCONFIG_IPS*/
		}
      //free(ips);
		return jips;
	}else {
	   return sendPtrReplyOrThrowError(env,count,NULL);
	}
}


/*
 * Class:     fr_b2i_comvs_jni_netconfig_NetConfig
 * Method:    listInterface
 * Signature: ()[Ljava/lang/String;
 * Java Method : public native static String[] listInterface()throws NetConfigException ; 
 */
JNIEXPORT jobjectArray JNICALL JNI_netconfig_listInterface(JNIEnv *env, jclass jNetconfig_class)
{
	int i = 0;
  WCHAR ** interfaces  = NULL;
  COMVS_INT count = 0;
  jobjectArray jitf;
  jclass jStr_class = (*env)->FindClass(env, "java/lang/String");
  char *msgHeader = NULL;
	
  interfaces = Comvs_ListInterfaces(&count);
  if (count!=0){
    jitf = (*env)->NewObjectArray(env, (jint) count, jStr_class, (*env)->NewStringUTF(env,"")); 
    JNI_REQUIRE_NULLERR_NULL_RETURN(jitf,  " -> error in NewObjectArray");
    
    for(i=0 ; i<count ; i++){
      (*env)->SetObjectArrayElement(env, jitf, i, (*env)->NewStringUTF(env, (const char*) interfaces[i]));
    } 
    if(interfaces!=NULL){
        //free(interfaces); 
    }
    return jitf;
  } else {
    return sendPtrReplyOrThrowError(env,count,NULL);
  }  
}


#ifdef __cplusplus
}
#endif
