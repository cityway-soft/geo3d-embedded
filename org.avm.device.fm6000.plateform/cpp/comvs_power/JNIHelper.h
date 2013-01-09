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
#include <string.h>
#include "COMVS_Loglib.h"
#include "comvs_types.h"

#ifndef __JNI_HELPER_H_
#define __JNI_HELPER_H_

#define JNI_REQUIRE_NULLERR(err,msg)				if(err==0){ForceLogInfo("%s %d %s",__FILE__,__LINE__,msg); return -1;}
#define JNI_REQUIRE_NULLERR_NO_RETURN(err,msg)		if(err==0){ForceLogInfo("%s %d %s",__FILE__,__LINE__,msg); return;}
#define JNI_REQUIRE_NULLERR_NULL_RETURN(err,msg)	if(err==NULL){ForceLogInfo("%s %d %s",__FILE__,__LINE__,msg); return NULL;}
#define JNI_REQUIRE_NULLERR_FALSE_RETURN(err,msg)	if(err==0){ForceLogInfo("%s %d %s",__FILE__,__LINE__,msg); return JNI_FALSE;}

#define JNI_REQUIRE_NOERR(err,msg)					if(err!=0){ForceLogInfo("%s %d %s",__FILE__,__LINE__,msg); return -1;}
#define JNI_REQUIRE_NOERR_NO_RETURN(err,msg)		if(err!=0){ForceLogInfo("%s %d %s",__FILE__,__LINE__,msg); return;}
#define JNI_REQUIRE_NOERR_NULL_RETURN(err,msg)		if(err!=0){ForceLogInfo("%s %d %s",__FILE__,__LINE__,msg); return NULL;}
#define JNI_REQUIRE_NOERR_FALSE_RETURN(err,msg)		if(err!=0){ForceLogInfo("%s %d %s",__FILE__,__LINE__,msg); return JNI_FALSE;}


#ifdef __cplusplus
extern "C" {
#endif

char *JNIInitErrorMsg(char *dllName, char *fctName);

unsigned short * JNICreateString(JNIEnv *env,jstring theString,  unsigned int * outputStringSize);

unsigned char JNIGetString (JNIEnv *env, jstring jstr, unsigned short *usOut, unsigned int maxSize);
jstring JNICreateJavaString(JNIEnv *env,WCHAR * str);

/**
 * Get a String object from java to return it as a string of char.
 * @arg JNIEnv *: pointer on the java environemet
 * @arg jobj : current object
 * @arg char * : pointer on a string. It contains the name of the researched java field.
 * @arg char * : pointer on the out string of char buffer
 * @arg unsigned int : size of pre-allocated buffer
 * @return -1 on error, 0 on success
 */
int JNIGetStringField(JNIEnv *env, jobject jobj, char *fieldName, char *outString, unsigned int sizeMaxOfString);


/**
 * Get a String object from java to return it as a string of unicode character.
 * @arg JNIEnv *: pointer on the java environemet
 * @arg jobj : current object
 * @arg char * : pointer on a string. It contains the name of the researched java field.
 * @arg char * : pointer on the out string of char buffer
 * @arg unsigned int : size of pre-allocated buffer
 * @return -1 on error, 0 on success
 */
int JNIGetUTF16StringField(JNIEnv *env, jobject jobj, char *fieldName, unsigned short *outString, unsigned int sizeMaxOfString);

/* Digging inside JAVA Objects*/
int JNIGetIntField(JNIEnv *jEnv, jclass jCls, char *jStr, jobject jObj, COMVS_INT * ji);
int JNIGetLongField(JNIEnv *jEnv, jclass jCls, char *jStr, jobject jObj,COMVS_LONG * jl);
int JNIGetBoolField(JNIEnv *jEnv, jclass jCls, char *jStr, jobject jObj,COMVS_BOOL * jc);
int JNIGetObjectField(JNIEnv *jEnv, jclass jCls, char *str, jobject jObj, char *objName, jobject *ret);

int JNISetIntField(JNIEnv *pjEnv, jclass jClz, char *pStr, jobject jOb, jint jInteger);
int JNISetLongField(JNIEnv *jEnv, jclass jCls, char *str, jobject jObj, jlong jLo);
int JNISetDoubleField(JNIEnv *jEnv, jclass jCls, char *str, jobject jObj, jdouble jDo);
int JNISetObjectField(JNIEnv *jEnv, jclass jCls, char *str, jobject jObj, jobject jFieldObj, char *objName);
int JNISetCharField(JNIEnv *jEnv, jclass jCls, char *str, jobject jObj, jchar jChar);
int JNISetByteField(JNIEnv *jEnv, jclass jCls, char *str, jobject jObj, jbyte jByte);
int JNISetStringField(JNIEnv *pjEnv, jclass jClz, char *pStr, jobject jOb, char *str);

void sendNullException(JNIEnv* env, char* msg);
void sendException(JNIEnv* env, char* msg);
void sendExceptionWithPath(JNIEnv* env, char * execeptionPath,char* msg);

#ifdef __cplusplus
}
#endif

#endif /* __JNI_HELPER_H_ */
