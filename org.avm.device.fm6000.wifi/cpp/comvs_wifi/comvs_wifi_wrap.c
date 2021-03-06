/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.34
 * 
 * This file is not intended to be easily readable and contains a number of 
 * coding conventions designed to improve portability and efficiency. Do not make
 * changes to this file unless you know what you are doing--modify the SWIG 
 * interface file instead. 
 * ----------------------------------------------------------------------------- */

/* -----------------------------------------------------------------------------
 *  This section contains generic SWIG labels for method/variable
 *  declarations/attributes, and other compiler dependent labels.
 * ----------------------------------------------------------------------------- */

/* template workaround for compilers that cannot correctly implement the C++ standard */
#ifndef SWIGTEMPLATEDISAMBIGUATOR
# if defined(__SUNPRO_CC) && (__SUNPRO_CC <= 0x560)
#  define SWIGTEMPLATEDISAMBIGUATOR template
# elif defined(__HP_aCC)
/* Needed even with `aCC -AA' when `aCC -V' reports HP ANSI C++ B3910B A.03.55 */
/* If we find a maximum version that requires this, the test would be __HP_aCC <= 35500 for A.03.55 */
#  define SWIGTEMPLATEDISAMBIGUATOR template
# else
#  define SWIGTEMPLATEDISAMBIGUATOR
# endif
#endif

/* inline attribute */
#ifndef SWIGINLINE
# if defined(__cplusplus) || (defined(__GNUC__) && !defined(__STRICT_ANSI__))
#   define SWIGINLINE inline
# else
#   define SWIGINLINE
# endif
#endif

/* attribute recognised by some compilers to avoid 'unused' warnings */
#ifndef SWIGUNUSED
# if defined(__GNUC__)
#   if !(defined(__cplusplus)) || (__GNUC__ > 3 || (__GNUC__ == 3 && __GNUC_MINOR__ >= 4))
#     define SWIGUNUSED __attribute__ ((__unused__)) 
#   else
#     define SWIGUNUSED
#   endif
# elif defined(__ICC)
#   define SWIGUNUSED __attribute__ ((__unused__)) 
# else
#   define SWIGUNUSED 
# endif
#endif

#ifndef SWIGUNUSEDPARM
# ifdef __cplusplus
#   define SWIGUNUSEDPARM(p)
# else
#   define SWIGUNUSEDPARM(p) p SWIGUNUSED 
# endif
#endif

/* internal SWIG method */
#ifndef SWIGINTERN
# define SWIGINTERN static SWIGUNUSED
#endif

/* internal inline SWIG method */
#ifndef SWIGINTERNINLINE
# define SWIGINTERNINLINE SWIGINTERN SWIGINLINE
#endif

/* exporting methods */
#if (__GNUC__ >= 4) || (__GNUC__ == 3 && __GNUC_MINOR__ >= 4)
#  ifndef GCC_HASCLASSVISIBILITY
#    define GCC_HASCLASSVISIBILITY
#  endif
#endif

#ifndef SWIGEXPORT
# if defined(_WIN32) || defined(__WIN32__) || defined(__CYGWIN__)
#   if defined(STATIC_LINKED)
#     define SWIGEXPORT
#   else
#     define SWIGEXPORT __declspec(dllexport)
#   endif
# else
#   if defined(__GNUC__) && defined(GCC_HASCLASSVISIBILITY)
#     define SWIGEXPORT __attribute__ ((visibility("default")))
#   else
#     define SWIGEXPORT
#   endif
# endif
#endif

/* calling conventions for Windows */
#ifndef SWIGSTDCALL
# if defined(_WIN32) || defined(__WIN32__) || defined(__CYGWIN__)
#   define SWIGSTDCALL __stdcall
# else
#   define SWIGSTDCALL
# endif 
#endif

/* Deal with Microsoft's attempt at deprecating C standard runtime functions */
#if !defined(SWIG_NO_CRT_SECURE_NO_DEPRECATE) && defined(_MSC_VER) && !defined(_CRT_SECURE_NO_DEPRECATE)
# define _CRT_SECURE_NO_DEPRECATE
#endif

/* Deal with Microsoft's attempt at deprecating methods in the standard C++ library */
#if !defined(SWIG_NO_SCL_SECURE_NO_DEPRECATE) && defined(_MSC_VER) && !defined(_SCL_SECURE_NO_DEPRECATE)
# define _SCL_SECURE_NO_DEPRECATE
#endif



/* Fix for jlong on some versions of gcc on Windows */
#if defined(__GNUC__) && !defined(__INTELC__)
  typedef long long __int64;
#endif

/* Fix for jlong on 64-bit x86 Solaris */
#if defined(__x86_64)
# ifdef _LP64
#   undef _LP64
# endif
#endif

#include <jni.h>
#include <stdlib.h>
#include <string.h>


/* Support for throwing Java exceptions */
typedef enum {
  SWIG_JavaOutOfMemoryError = 1, 
  SWIG_JavaIOException, 
  SWIG_JavaRuntimeException, 
  SWIG_JavaIndexOutOfBoundsException,
  SWIG_JavaArithmeticException,
  SWIG_JavaIllegalArgumentException,
  SWIG_JavaNullPointerException,
  SWIG_JavaDirectorPureVirtual,
  SWIG_JavaUnknownError
} SWIG_JavaExceptionCodes;

typedef struct {
  SWIG_JavaExceptionCodes code;
  const char *java_exception;
} SWIG_JavaExceptions_t;


static void SWIGUNUSED SWIG_JavaThrowException(JNIEnv *jenv, SWIG_JavaExceptionCodes code, const char *msg) {
  jclass excep;
  static const SWIG_JavaExceptions_t java_exceptions[] = {
    { SWIG_JavaOutOfMemoryError, "java/lang/OutOfMemoryError" },
    { SWIG_JavaIOException, "java/io/IOException" },
    { SWIG_JavaRuntimeException, "java/lang/RuntimeException" },
    { SWIG_JavaIndexOutOfBoundsException, "java/lang/IndexOutOfBoundsException" },
    { SWIG_JavaArithmeticException, "java/lang/ArithmeticException" },
    { SWIG_JavaIllegalArgumentException, "java/lang/IllegalArgumentException" },
    { SWIG_JavaNullPointerException, "java/lang/NullPointerException" },
    { SWIG_JavaDirectorPureVirtual, "java/lang/RuntimeException" },
    { SWIG_JavaUnknownError,  "java/lang/UnknownError" },
    { (SWIG_JavaExceptionCodes)0,  "java/lang/UnknownError" } };
  const SWIG_JavaExceptions_t *except_ptr = java_exceptions;

  while (except_ptr->code != code && except_ptr->code)
    except_ptr++;

  (*jenv)->ExceptionClear(jenv);
  excep = (*jenv)->FindClass(jenv, except_ptr->java_exception);
  if (excep)
    (*jenv)->ThrowNew(jenv, excep, msg);
}


/* Contract support */

#define SWIG_contract_assert(nullreturn, expr, msg) if (!(expr)) {SWIG_JavaThrowException(jenv, SWIG_JavaIllegalArgumentException, msg); return nullreturn; } else


#include <windows.h>
#include <COMVS_WifiDevice.h>


#ifdef __cplusplus
extern "C" {
#endif

SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1TRUE_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  result = (int) 0xff;
  
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1FALSE_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  result = (int) 0x0;
  
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_WEP_1KEY_1SIZE_1MAX_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  result = (int) 32;
  
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_SSID_1SIZE_1MAX_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  result = (int) 32;
  
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_MAC_1SIZE_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  result = (int) 6;
  
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_SUCCESS_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  COMVS_WIFI_STATUS result;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFI_STATUS)SUCCESS;
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_ERR_1WIFI_1NO_1CARD_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  COMVS_WIFI_STATUS result;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFI_STATUS)ERR_WIFI_NO_CARD;
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_ERR_1WIFI_1NO_1INTERF_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  COMVS_WIFI_STATUS result;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFI_STATUS)ERR_WIFI_NO_INTERF;
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_ERR_1WIFI_1NO_1CONFIG_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  COMVS_WIFI_STATUS result;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFI_STATUS)ERR_WIFI_NO_CONFIG;
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_ERR_1WIFI_1BAD_1SSID_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  COMVS_WIFI_STATUS result;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFI_STATUS)ERR_WIFI_BAD_SSID;
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_ERR_1WIFI_1SIZE_1DATA_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  COMVS_WIFI_STATUS result;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFI_STATUS)ERR_WIFI_SIZE_DATA;
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_ERR_1WIFI_1BAD_1POINTER_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  COMVS_WIFI_STATUS result;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFI_STATUS)ERR_WIFI_BAD_POINTER;
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_ERR_1WIFI_1get(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  COMVS_WIFI_STATUS result;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFI_STATUS)ERR_WIFI;
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1iEnable8021x_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jint jarg2) {
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  int arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  arg2 = (int)jarg2; 
  if (arg1) (arg1)->iEnable8021x = arg2;
  
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1iEnable8021x_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jint jresult = 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  result = (int) ((arg1)->iEnable8021x);
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1ulEapFlags_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  unsigned long arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  arg2 = (unsigned long)jarg2; 
  if (arg1) (arg1)->ulEapFlags = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1ulEapFlags_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  unsigned long result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  result = (unsigned long) ((arg1)->ulEapFlags);
  jresult = (jlong)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1ulEapType_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  unsigned long arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  arg2 = (unsigned long)jarg2; 
  if (arg1) (arg1)->ulEapType = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1ulEapType_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  unsigned long result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  result = (unsigned long) ((arg1)->ulEapType);
  jresult = (jlong)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1ulAuthDataLen_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  unsigned long arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  arg2 = (unsigned long)jarg2; 
  if (arg1) (arg1)->ulAuthDataLen = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1ulAuthDataLen_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  unsigned long result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  result = (unsigned long) ((arg1)->ulAuthDataLen);
  jresult = (jlong)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1pucAuthData_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jstring jarg2) {
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  char *arg2 = (char *) 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  arg2 = 0;
  if (jarg2) {
    arg2 = (char *)(*jenv)->GetStringUTFChars(jenv, jarg2, 0);
    if (!arg2) return ;
  }
  {
    if (arg1->pucAuthData) free((char *)arg1->pucAuthData);
    if (arg2) {
      arg1->pucAuthData = (char *) malloc(strlen((const char *)arg2)+1);
      strcpy((char *)arg1->pucAuthData, (const char *)arg2);
    } else {
      arg1->pucAuthData = 0;
    }
  }
  if (arg2) (*jenv)->ReleaseStringUTFChars(jenv, jarg2, (const char *)arg2);
}


SWIGEXPORT jstring JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1pucAuthData_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jstring jresult = 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  char *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  result = (char *) ((arg1)->pucAuthData);
  if(result) jresult = (*jenv)->NewStringUTF(jenv, (const char *)result);
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1reserved_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  void *arg2 = (void *) 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  arg2 = *(void **)&jarg2; 
  if (arg1) (arg1)->reserved = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1EAPOL_1PARAMS_1reserved_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  void *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  result = (void *) ((arg1)->reserved);
  *(void **)&jresult = result; 
  return jresult;
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_new_1COMVS_1WIFIDEVICE_1EAPOL_1PARAMS(JNIEnv *jenv, jclass jcls) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFIDEVICE_EAPOL_PARAMS *)(COMVS_WIFIDEVICE_EAPOL_PARAMS *) calloc(1, sizeof(COMVS_WIFIDEVICE_EAPOL_PARAMS));
  *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jresult = result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_delete_1COMVS_1WIFIDEVICE_1EAPOL_1PARAMS(JNIEnv *jenv, jclass jcls, jlong jarg1) {
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg1 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  
  (void)jenv;
  (void)jcls;
  arg1 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg1; 
  free((char *) arg1);
  
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION_1ulWEP_1key_1index_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  unsigned long arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  arg2 = (unsigned long)jarg2; 
  if (arg1) (arg1)->ulWEP_key_index = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION_1ulWEP_1key_1index_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  unsigned long result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  result = (unsigned long) ((arg1)->ulWEP_key_index);
  jresult = (jlong)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION_1ulWEP_1key_1size_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  unsigned long arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  arg2 = (unsigned long)jarg2; 
  if (arg1) (arg1)->ulWEP_key_size = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION_1ulWEP_1key_1size_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  unsigned long result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  result = (unsigned long) ((arg1)->ulWEP_key_size);
  jresult = (jlong)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION_1ucWEP_1key_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jstring jarg2) {
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  char *arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  arg2 = 0;
  if (jarg2) {
    arg2 = (char *)(*jenv)->GetStringUTFChars(jenv, jarg2, 0);
    if (!arg2) return ;
  }
  {
    if (arg2) strncpy((char *)arg1->ucWEP_key, (const char *)arg2, 32);
    else arg1->ucWEP_key[0] = 0;
  }
  
  if (arg2) (*jenv)->ReleaseStringUTFChars(jenv, jarg2, (const char *)arg2);
}


SWIGEXPORT jstring JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION_1ucWEP_1key_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jstring jresult = 0 ;
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  char *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  result = (char *)(char *) ((arg1)->ucWEP_key);
  if(result) jresult = (*jenv)->NewStringUTF(jenv, (const char *)result);
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION_1reserved_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  void *arg2 = (void *) 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  arg2 = *(void **)&jarg2; 
  if (arg1) (arg1)->reserved = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION_1reserved_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  void *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  result = (void *) ((arg1)->reserved);
  *(void **)&jresult = result; 
  return jresult;
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_new_1COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION(JNIEnv *jenv, jclass jcls) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *)(COMVS_WIFIDEVICE_WEP_CONFIGURATION *) calloc(1, sizeof(COMVS_WIFIDEVICE_WEP_CONFIGURATION));
  *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jresult = result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_delete_1COMVS_1WIFIDEVICE_1WEP_1CONFIGURATION(JNIEnv *jenv, jclass jcls, jlong jarg1) {
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  
  (void)jenv;
  (void)jcls;
  arg1 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg1; 
  free((char *) arg1);
  
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1ucSSID_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jstring jarg2) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  char *arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  arg2 = 0;
  if (jarg2) {
    arg2 = (char *)(*jenv)->GetStringUTFChars(jenv, jarg2, 0);
    if (!arg2) return ;
  }
  {
    if (arg2) strncpy((char *)arg1->ucSSID, (const char *)arg2, 32);
    else arg1->ucSSID[0] = 0;
  }
  
  if (arg2) (*jenv)->ReleaseStringUTFChars(jenv, jarg2, (const char *)arg2);
}


SWIGEXPORT jstring JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1ucSSID_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jstring jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  char *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (char *)(char *) ((arg1)->ucSSID);
  if(result) jresult = (*jenv)->NewStringUTF(jenv, (const char *)result);
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1iWifi_1mode_1authentication_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jint jarg2) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  int arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  arg2 = (int)jarg2; 
  if (arg1) (arg1)->iWifi_mode_authentication = arg2;
  
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1iWifi_1mode_1authentication_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jint jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (int) ((arg1)->iWifi_mode_authentication);
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1iMode_1infrastructure_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jint jarg2) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  int arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  arg2 = (int)jarg2; 
  if (arg1) (arg1)->iMode_infrastructure = arg2;
  
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1iMode_1infrastructure_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jint jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (int) ((arg1)->iMode_infrastructure);
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1ulMode_1cipher_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  unsigned long arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  arg2 = (unsigned long)jarg2; 
  if (arg1) (arg1)->ulMode_cipher = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1ulMode_1cipher_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  unsigned long result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (unsigned long) ((arg1)->ulMode_cipher);
  jresult = (jlong)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1stWEP_1config_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2, jobject jarg2_) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *arg2 = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *) 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  (void)jarg2_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  arg2 = *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jarg2; 
  if (arg1) (arg1)->stWEP_config = *arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1stWEP_1config_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  COMVS_WIFIDEVICE_WEP_CONFIGURATION *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (COMVS_WIFIDEVICE_WEP_CONFIGURATION *)& ((arg1)->stWEP_config);
  *(COMVS_WIFIDEVICE_WEP_CONFIGURATION **)&jresult = result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1st802_11x_1mode_1authentication_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2, jobject jarg2_) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *arg2 = (COMVS_WIFIDEVICE_EAPOL_PARAMS *) 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  (void)jarg2_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  arg2 = *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jarg2; 
  if (arg1) (arg1)->st802_1x_mode_authentication = *arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1st802_11x_1mode_1authentication_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  COMVS_WIFIDEVICE_EAPOL_PARAMS *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (COMVS_WIFIDEVICE_EAPOL_PARAMS *)& ((arg1)->st802_1x_mode_authentication);
  *(COMVS_WIFIDEVICE_EAPOL_PARAMS **)&jresult = result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1nbOfConfig_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  unsigned long arg2 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  arg2 = (unsigned long)jarg2; 
  if (arg1) (arg1)->nbOfConfig = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1nbOfConfig_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  unsigned long result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (unsigned long) ((arg1)->nbOfConfig);
  jresult = (jlong)result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1reserved_1set(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_, jlong jarg2) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  void *arg2 = (void *) 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  arg2 = *(void **)&jarg2; 
  if (arg1) (arg1)->reserved = arg2;
  
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_COMVS_1WIFIDEVICE_1CONFIGURATION_1reserved_1get(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  void *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (void *) ((arg1)->reserved);
  *(void **)&jresult = result; 
  return jresult;
}


SWIGEXPORT jlong JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_new_1COMVS_1WIFIDEVICE_1CONFIGURATION(JNIEnv *jenv, jclass jcls) {
  jlong jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *result = 0 ;
  
  (void)jenv;
  (void)jcls;
  result = (COMVS_WIFIDEVICE_CONFIGURATION *)(COMVS_WIFIDEVICE_CONFIGURATION *) calloc(1, sizeof(COMVS_WIFIDEVICE_CONFIGURATION));
  *(COMVS_WIFIDEVICE_CONFIGURATION **)&jresult = result; 
  return jresult;
}


SWIGEXPORT void JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_delete_1COMVS_1WIFIDEVICE_1CONFIGURATION(JNIEnv *jenv, jclass jcls, jlong jarg1) {
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  
  (void)jenv;
  (void)jcls;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  free((char *) arg1);
  
}


SWIGEXPORT jshort JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_Comvs_1IsWifiCard(JNIEnv *jenv, jclass jcls) {
  jshort jresult = 0 ;
  unsigned char result;
  
  (void)jenv;
  (void)jcls;
  result = (unsigned char)Comvs_IsWifiCard();
  jresult = (jshort)result; 
  return jresult;
}


SWIGEXPORT jshort JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_Comvs_1IsConnectedToNetwork(JNIEnv *jenv, jclass jcls) {
  jshort jresult = 0 ;
  unsigned char result;
  
  (void)jenv;
  (void)jcls;
  result = (unsigned char)Comvs_IsConnectedToNetwork();
  jresult = (jshort)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_Comvs_1SetWifiPreferedNetwork(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg1_) {
  jint jresult = 0 ;
  COMVS_WIFIDEVICE_CONFIGURATION *arg1 = (COMVS_WIFIDEVICE_CONFIGURATION *) 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  (void)jarg1_;
  arg1 = *(COMVS_WIFIDEVICE_CONFIGURATION **)&jarg1; 
  result = (int)Comvs_SetWifiPreferedNetwork(arg1);
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_Comvs_1DeleteWifiPreferedNetwork(JNIEnv *jenv, jclass jcls, jstring jarg1) {
  jint jresult = 0 ;
  char *arg1 = (char *) 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  arg1 = 0;
  if (jarg1) {
    arg1 = (char *)(*jenv)->GetStringUTFChars(jenv, jarg1, 0);
    if (!arg1) return 0;
  }
  result = (int)Comvs_DeleteWifiPreferedNetwork(arg1);
  jresult = (jint)result; 
  if (arg1) (*jenv)->ReleaseStringUTFChars(jenv, jarg1, (const char *)arg1);
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_Comvs_1DeleteWifiPreferedNetworks(JNIEnv *jenv, jclass jcls) {
  jint jresult = 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  result = (int)Comvs_DeleteWifiPreferedNetworks();
  jresult = (jint)result; 
  return jresult;
}


SWIGEXPORT jint JNICALL Java_org_avm_device_fm6000_wifi_jni_COMVS_1WIFIJNI_Comvs_1GetSignalStrength(JNIEnv *jenv, jclass jcls, jlong jarg1) {
  jint jresult = 0 ;
  int *arg1 = (int *) 0 ;
  int result;
  
  (void)jenv;
  (void)jcls;
  arg1 = *(int **)&jarg1; 
  result = (int)Comvs_GetSignalStrength(arg1);
  jresult = (jint)result; 
  return jresult;
}


#ifdef __cplusplus
}
#endif

