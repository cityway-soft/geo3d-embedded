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

#include "JNIHelper.h"

static char* nom = "[JNI_HELPER]";


void sendNullException(JNIEnv* env, char* msg)
{
	jclass nullExceptionCls = (*env)->FindClass(env,"java/lang/NullPointerException");
	if (nullExceptionCls == 0)
	{
		/* Unable to find the new exception class, give up. */
		return;
	}
	(*env)->ThrowNew(env,nullExceptionCls, msg);
}

void sendException(JNIEnv* env, char* msg)
{
	jclass exceptionCls = (*env)->FindClass(env,"java/lang/Exception");
	if (exceptionCls == 0)
	{
		/* Unable to find the new exception class, give up. */
		return;
	}
	(*env)->ThrowNew(env,exceptionCls, msg);
}

void sendExceptionWithPath(JNIEnv* env, char * exceptionPath,char* msg)
{
	jclass exceptionCls = (*env)->FindClass(env,exceptionPath);
	if (exceptionCls == 0)
	{
		sendException(env,"The exception class required has not been found!");
		return;
	}
	(*env)->ThrowNew(env,exceptionCls, msg);
}


unsigned short * JNICreateString(JNIEnv *env,jstring theString,  unsigned int * outputStringSize){
	const jbyte * string = NULL;
	unsigned int size = 0;
	unsigned short * unicodeString = NULL;
	jboolean isCopy = JNI_FALSE;
	
	string = (*env)->GetStringUTFChars(env,theString,&isCopy);
	if (string != NULL){
		/* Get the lenght of the Java String*/
		*outputStringSize =  (*env)->GetStringUTFLength(env,theString);
		/* Get the buffer size required */
		*outputStringSize = MultiByteToWideChar(CP_ACP,MB_PRECOMPOSED, string,*outputStringSize,NULL,0);
		/* Alloc the requested buffer */
		unicodeString = LocalAlloc(LPTR,sizeof(unsigned short *)*(*outputStringSize));
		/* Get the String converted*/
		if (MultiByteToWideChar(CP_ACP,MB_COMPOSITE, string,(int)*outputStringSize,unicodeString,(int)*outputStringSize) == 0)
		{
			ForceLogDebug("[JNI_COMMON.dll] [JNICreateString] : MultiByteToWideChar failed");
			LocalFree(unicodeString);
			unicodeString = NULL;
		}
		(*env)->ReleaseStringUTFChars(env,theString,string);
	} else {
		ForceLogDebug("[JNI_COMMON.dll] [JNICreateString] : string provided is null");
	}
	return unicodeString;
}

/**
 *Convert a jstring to a unsigned short string.
 */
unsigned char JNIGetString (JNIEnv *env, jstring jstr, unsigned short *usOut, unsigned int maxSize)
{
	jboolean iscopy = JNI_FALSE;
	int written = 0;
	const char *name;

	name = ((*env)->GetStringUTFChars(env, jstr, &iscopy));
	if ( ! iscopy ){
		return -1;
	}
	
	if (strlen (name) > maxSize) return -1;
	
	written = MultiByteToWideChar(CP_ACP, MB_PRECOMPOSED, name, strlen(name), usOut, maxSize);
	if(written>(int)maxSize){
		return -1;
	}
	usOut[written]=L'\0';
	((*env)->ReleaseStringUTFChars(env, jstr, name));
	
	return 0;
}

/**
 * Get a java string field and convert it to char string.
 */
int JNIGetStringField(JNIEnv *env, jobject jobj, char *fieldName, char *outString, unsigned int sizeMaxOfString)
{	
	jclass cls = 0;
	jfieldID fid = 0;
	jstring jstr = 0;
	jint ji = 0;
	
	if(sizeMaxOfString>0)
	{
		fid = (*env)->GetFieldID(env, cls, fieldName, "Ljava/lang/String;");
		JNI_REQUIRE_NULLERR(fid, "bad srting object ID ");
	
		jstr = (*env)->GetObjectField(env, jobj, fid);
		JNI_REQUIRE_NULLERR(jstr, "bad jString object ");
		ji = (*env)->GetStringLength(env, jstr);
		if(ji==0)
		{
			ForceLogDebug("no string in field");
	  	}
	  	else if(ji<=sizeMaxOfString)
		{
			outString = (char *) (*env)->GetStringUTFChars(env, jstr, 0);
			(*env)->ReleaseStringUTFChars(env, jstr, outString);
			return ji;
	    }
	}
	return -1;
}

/**
 * Get a java string field and convert it to unicode (UTF16) string.
 */
int JNIGetUTF16StringField(JNIEnv *env, jobject jobj, char *fieldName, unsigned short *outString, unsigned int sizeMaxOfString)
{
	jclass cls = 0;
	jfieldID fid = 0;
	jstring jstr = 0;
	jint ji = 0;
	unsigned short *str = NULL;
	
	if(sizeMaxOfString<0){
		return -1;
	}
	
	cls = (*env)->GetObjectClass(env, jobj);
	JNI_REQUIRE_NULLERR(cls, " -> error on class ");
	
	fid = (*env)->GetFieldID(env, cls, fieldName, "Ljava/lang/String;");
	JNI_REQUIRE_NULLERR(fid, " -> bad field object ID ");			
	
	jstr = (*env)->GetObjectField(env, jobj, fid);
	JNI_REQUIRE_NULLERR(jstr, " -> bad jString object ");
	
	ji = (*env)->GetStringLength(env, jstr);
	if(ji==0){
		ForceLogDebug("[JNI_Helper] %s -> no string field ", fieldName);
  	}
	else if(ji>sizeMaxOfString){
		ForceLogDebug("[JNI_Helper] %s -> string to get is too long", fieldName);
	}
  	else{
    	str = (unsigned short*) LocalAlloc(LPTR, sizeof(WCHAR)*(ji+1));
		JNI_REQUIRE_NULLERR(str, " -> can not allocate string ");
	  	str = (unsigned short*) (*env)->GetStringChars(env, jstr, 0);
		memcpy(outString, str, sizeof(WCHAR)*(ji+1));
		(*env)->ReleaseStringChars(env, jstr, str);
		return 0;
    }
	return -1;
}

int  JNIGetIntField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj, COMVS_INT * ji){
	jfieldID fid = 0;

	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, "I");
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	*ji = (*jEnv)->GetIntField(jEnv, jObj, fid);
	return 0;
}

int   JNIGetLongField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj,COMVS_LONG * jl){
	jfieldID fid = 0;

	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, "J");
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	*jl = (*jEnv)->GetIntField(jEnv, jObj, fid);
	
	return 0;	
}

int   JNIGetBoolField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj,COMVS_BOOL * jc){
	jfieldID fid = 0;
	jboolean jbool = 0;

	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, "Z");
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	jbool = (*jEnv)->GetBooleanField(jEnv, jObj, fid);
	*jc = (char)jbool;
	if (jbool == JNI_TRUE){
		*jc = COMVS_TRUE;
	} else {
		*jc = COMVS_FALSE;
	}	
	return 0;	
}

int JNIGetObjectField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj, char *objName, jobject *ret)
{
  jfieldID fid = 0;
		
	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, objName);
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	*ret = (*jEnv)->GetObjectField(jEnv, jObj, fid);	
	return 0;
}




int	JNISetIntField(JNIEnv *pjEnv, jclass jClz, char *fieldName, jobject jOb, jint jInteger)
{
	jfieldID fid = 0;

	fid = (*pjEnv)->GetFieldID(pjEnv, jClz, fieldName, "I");
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	(*pjEnv)->SetIntField(pjEnv, jOb, fid, jInteger);
	return 0;
}

int	JNISetLongField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj, jlong jLo)
{
	jfieldID fid = 0;

	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, "J");
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	(*jEnv)->SetLongField(jEnv, jObj, fid, jLo);
	return 0;
}


int	JNISetDoubleField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj, jdouble jDo)
{
	jfieldID fid = 0;

	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, "D");
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	(*jEnv)->SetDoubleField(jEnv, jObj, fid, jDo);
	return 0;
}

int JNISetObjectField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj, jobject jFieldObj, char *objName)
{
	jfieldID fid = 0;

	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, objName);
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	(*jEnv)->SetObjectField(jEnv, jObj, fid, jFieldObj);
	return 0;
}

int JNISetCharField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj, jchar jChar)
{
	jfieldID fid = 0;

	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, "C");
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	(*jEnv)->SetCharField(jEnv, jObj, fid, jChar);
	return 0;
}

int JNISetByteField(JNIEnv *jEnv, jclass jCls, char *fieldName, jobject jObj, jbyte jByte)
{
	jfieldID fid = 0;

	fid = (*jEnv)->GetFieldID(jEnv, jCls, fieldName, "B");
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
	
	(*jEnv)->SetByteField(jEnv, jObj, fid, jByte);
	return 0;
}

int JNISetStringField(JNIEnv *pjEnv, jclass jClz, char *pStr, jobject jOb, char *str)
{
  jfieldID fid = 0;
	jobject jString = NULL;
	jmethodID cstrId = 0;
	jclass clsString = 0;
	
	fid = (*pjEnv)->GetFieldID(pjEnv, jClz, pStr, "Ljava/lang/String;");
	jString = (*pjEnv)->NewStringUTF (pjEnv, str);
	JNI_REQUIRE_NULLERR(fid, " -> bad object ID ");
		
	(*pjEnv)->SetObjectField(pjEnv, jOb, fid, jString);
	return 0;
}

jstring JNICreateJavaString(JNIEnv * pEnv,WCHAR * string){
	jstring str = NULL;
	char * data = NULL;
	data = (char *)LocalAlloc(LPTR,sizeof(char) * wcslen(string));
	if( data != NULL){
		sprintf(data,"%S",string);
		str = (*pEnv)->NewStringUTF(pEnv,data);
		LocalFree(data);
	} else {
		ForceLogError("We are short on memory in JNI_COMMON(%s,%d), but who cares?",__FILE__,__LINE__);	
	}
	return str;
}
	
