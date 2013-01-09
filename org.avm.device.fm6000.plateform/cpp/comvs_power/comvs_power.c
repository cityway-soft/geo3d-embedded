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

/**
 * @file
 * auteur : Grégory Pohu\n
 * version : 0.1
 * date : 08/12/2006\n
 * This file is integral part of project JNI_Power.\n
 * Functions interfacing itself between the LIB_PowerManagement and the JAVA objects.\n
 */

#include <string.h>

#include <windows.h>
#include <winbase.h>
#include <tchar.h>
#include <stdio.h>
#include <stdarg.h>
#include <jni.h>

#include <COMVS_loglib.h>
#include <COMVS_types.h>
#include <COMVS_PowerManagement.h>
#include "JNIHelper.h"
#include "comvs_power.h"


void ConvertSystemTimeToJavaCalendar(JNIEnv *jenv, SYSTEMTIME sTime, jobject jCalendar);
LPSYSTEMTIME ConvertJavaCalendarToSystemTime(JNIEnv *jenv, jobject jCalendar, LPSYSTEMTIME pst);
int COMVS_GetIntField(JNIEnv *jEnv, jclass jCls, char *jStr, jobject jObj);

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    SwitchOff
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_SwitchOff(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_SwitchOff();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    ModemSwitchOn
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_ModemSwitchOn(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_SwitchOff();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    ModemSwitchOff
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_ModemSwitchOff (JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_Modem_SwitchOff();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    GPSSwitchOn
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_GPSSwitchOn(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_GPS_SwitchOn();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    GPSSwitchOff
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_GPSSwitchOff(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_GPS_SwitchOff();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    AcceleroSwitchOn
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_AcceleroSwitchOn(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_Accelerometer_SwitchOn();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    AcceleroSwitchOff
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_AcceleroSwitchOff(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_Accelerometer_SwitchOff();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    CANSwitchOn
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_CANSwitchOn(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_CAN_SwitchOn();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    CANSwitchOff
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_CANSwitchOff(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_CAN_SwitchOff();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    GetPowerSource
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_GetPowerSource(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_GetPowerSource();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_StopPowerEvent
 * Method:    StopPowerSource
 * Signature: ()
 */
JNIEXPORT void JNICALL JNI_PowerNative_StopPowerEvent(JNIEnv *jenv, jclass jcls){
	ForceLogDebug("JNI Comvs_StopPowerEvent");
	Comvs_StopPowerEvent();
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    WaitPowerEvent
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_WaitPowerEvent(JNIEnv *jenv, jclass jcls, jint jtimeout){
	int timeout = jtimeout;
	
	ForceLogDebug("JNI launch Comvs_WaitPowerEvent");
	return (jint) Comvs_WaitPowerEvent((int) timeout);
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    SetRTCWakeUp
 * Signature: (Ljava/util/Date;I)I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_SetRTCWakeUp(JNIEnv *jenv, jclass jcls, jobject jCalendar, jint jmode){
	LPSYSTEMTIME pst = NULL;
	int status = 0;
	int mode = (int) jmode;
	
	#if defined __DEBUG
		ForceLogInfo("[JNI_POWER] SetRTCWakeUp in progress");
	#endif
	
	pst = LocalAlloc(LPTR, sizeof(SYSTEMTIME));
	JNI_REQUIRE_NULLERR(pst,"SYSTEMTIME allocation failed");
	
	//to cancel wakeup
	if((int) jmode == COMVS_SYSWAKE_NONE)
	{
		status = Comvs_SetRTCWakeUp(pst, jmode);
		#if defined __DEBUG
			ForceLogDebug("[JNi_POWER] no wakeup");
		#endif
	}else{
		pst = ConvertJavaCalendarToSystemTime(jenv, jCalendar, pst);
		status = Comvs_SetRTCWakeUp(pst, mode);
		#if defined __DEBUG
			ForceLogDebug("[JNi_POWER] wakeup actived");
		#endif
	}
	
	#if defined __DEBUG
		ForceLogInfo("[JNI_POWER] SetRTCWakeUp done");
	#endif
	
	return (jint) JNI_TRUE;
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    GetRTCWakeUp
 * Signature: (Ljava/util/Date;I)Z
 */
JNIEXPORT jboolean JNICALL JNI_PowerNative_GetRTCWakeUp(JNIEnv *jenv, jclass jcls, jobject jCalendar, jint  jmode){
	LPSYSTEMTIME st;
	DWORD RTCWakeUPMode = 0;
	boolean status = FALSE;
	
	st = LocalAlloc(LPTR, sizeof(SYSTEMTIME));
	JNI_REQUIRE_NULLERR_FALSE_RETURN(st,"[JNi POWER] bad allocation of SYSTEMTIME struct");
	
	status = Comvs_GetRTCWakeUp(st, &RTCWakeUPMode);
	#if defined __DEBUG
		ForceLogInfo("[JNI_POWER] status : %d", status);
		ForceLogInfo("[JNI_POWER] %d/%d/%d %d:%d:%d \nWakeUp mode: %d", st->wDay, st->wMonth, st->wYear, st->wHour,
			st->wMinute, st->wSecond, RTCWakeUPMode);
	#endif
	
	ConvertSystemTimeToJavaCalendar(jenv, *st, jCalendar);
	
	jmode = (jint) RTCWakeUPMode;
	
	#if defined __DEBUG
		ForceLogInfo("[JNI_POWER] GetRTCWakeUp done");
	#endif
	
	return (jboolean) JNI_TRUE;//status
}

/*
 * Class:     fr_b2i_comvs_jni_power_nativepart_PowerManagementNative
 * Method:    WakeUpSource
 * Signature: ()I
 */
JNIEXPORT jint JNICALL JNI_PowerNative_WakeUpSource(JNIEnv *jenv, jclass jcls){
	return (jint) Comvs_GetWakeupSource();
}


void ConvertSystemTimeToJavaCalendar(JNIEnv *jenv, SYSTEMTIME sTime, jobject jCalendar){
	jclass jclazz = 0;
	jmethodID jSetID = 0;
	
	//Get the Java Calendar Class
	jclazz = (*jenv)->FindClass(jenv, "fr/b2i/comvs/jni/power/nativepart/PowerManagementCalendar");
	JNI_REQUIRE_NULLERR_NO_RETURN(jclazz,"java.util.Calendar Class not found");
	
	//set java Calendar fields with SYSTEMTIME fields
	/*typedef struct _SYSTEMTIME {
		WORD wYear;
		WORD wMonth;
		WORD wDayOfWeek;
		WORD wDay;
		WORD wHour;
		WORD wMinute;
		WORD wSecond;
		WORD wMilliseconds;
	} SYSTEMTIME
	JAVA Method to set the values
	void 	set(int year, int month, int date, int hour, int minute, int second) 
	*/
	
	#if defined __DEBUG
		ForceLogInfo("[JNI_POWER] day: %d / dayOfWeek: %d / time: %d:%d:%d", sTime.wDay, sTime.wDayOfWeek, sTime.wHour,
			sTime.wMinute, sTime.wSecond);
	#endif
	
	//Call the JAVA setting method
	jSetID = (*jenv)->GetMethodID(jenv, jclazz, "setWakeUpDate", "(IIIII)V");
	JNI_REQUIRE_NULLERR_NO_RETURN(jSetID,"set() MethodID not found");
	(*jenv)->CallVoidMethod(jenv, jCalendar, jSetID, sTime.wDay, sTime.wDayOfWeek, sTime.wHour,
			sTime.wMinute, sTime.wSecond);
	
	#if defined __DEBUG
		ForceLogInfo("[JNI_POWER] PowerManagementCalendar.set() called");
	#endif
}

LPSYSTEMTIME ConvertJavaCalendarToSystemTime(JNIEnv *jenv, jobject jCalendar, LPSYSTEMTIME pst){
	jclass jclazz = 0;
	int status = 0;
	
	//Get the Java Calendar Class
	jclazz = (*jenv)->FindClass(jenv, "fr/b2i/comvs/jni/power/nativepart/PowerManagementCalendar");
	JNI_REQUIRE_NULLERR_NULL_RETURN(jclazz, "PowerManagementCalendar Class not found");
		
	pst->wHour = (int) COMVS_GetIntField(jenv, jclazz, "HOUR", jCalendar);
	pst->wMinute = (int) COMVS_GetIntField(jenv, jclazz, "MINUTE", jCalendar);
	pst->wSecond = (int) COMVS_GetIntField(jenv, jclazz, "SECOND", jCalendar);
	pst->wDay = (int) COMVS_GetIntField(jenv, jclazz, "DAY", jCalendar);
	pst->wDayOfWeek = (int) COMVS_GetIntField(jenv, jclazz, "DAYOFWEEK", jCalendar);
	
	#if defined __DEBUG
		ForceLogDebug("[JNI_POWER] %d", pst->wHour);
		ForceLogDebug("[JNI_POWER] %d", pst->wMinute);
		ForceLogDebug("[JNI_POWER] %d", pst->wSecond);
		ForceLogDebug("[JNI_POWER] %d", pst->wDay);
		ForceLogDebug("[JNI_POWER] %d", pst->wDayOfWeek);
	#endif
	
	return pst;
}

int COMVS_GetIntField(JNIEnv *jEnv, jclass jCls, char *jStr, jobject jObj)
{
	jint ji = 0;
	jfieldID fid = 0;
	
	fid = (*jEnv)->GetFieldID(jEnv, jCls, jStr, "I");
	if (fid == 0)
	{
		ForceLogDebug("JNI_WIFI -> bad %s object ID ", jStr);
		return 0;
	}
	ji = (*jEnv)->GetIntField(jEnv, jObj, fid);
	
	return (int) ji;
}