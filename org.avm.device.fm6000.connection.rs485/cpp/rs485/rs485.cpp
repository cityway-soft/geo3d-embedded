#include <jni.h>
#include <windows.h>
#include "rs485.h"

extern "C" {
	jint Java_com_ibm_oti_connection_comm_Connection_openImpl(JNIEnv *,jobject, jint);
	void Java_com_ibm_oti_connection_comm_Connection_configureImpl(JNIEnv *, jobject, jint, jint, jint, jint, jint, jboolean, jboolean,jint);
	void Java_com_ibm_oti_connection_comm_Connection_closeImpl(JNIEnv *,jobject, jint);
	jint Java_com_ibm_oti_connection_comm_Connection_readImpl(JNIEnv *,jobject, jint, jbyteArray, jint, jint);
	jint Java_com_ibm_oti_connection_comm_Connection_writeImpl(JNIEnv *,jobject, jint, jbyteArray, jint, jint);
	jint Java_com_ibm_oti_connection_comm_Connection_availableImpl(JNIEnv *, jobject, jint);
}

#define info printf

#ifdef _DEBUG
#define debug printf
#else
#define debug  //
#endif

void setrts(HANDLE handle, int on) {
	int result;
	unsigned long rts = (on) ? SETRTS : CLRRTS;
	info("[DSU] setrts(%d)\n",rts);
	result = EscapeCommFunction(handle, rts);
}


JNIEXPORT jint JNICALL Java_org_avm_device_connection_rs485_Connection_openImpl(JNIEnv *jenv,
		jobject jobj, jint port) {
	info("[DSU] call openImpl()\n");
	return Java_com_ibm_oti_connection_comm_Connection_openImpl(jenv, jobj,port);
}

JNIEXPORT void JNICALL Java_org_avm_device_connection_rs485_Connection_configureImpl(
		JNIEnv *jenv, jobject jobj, jint handle, jint baudrate, jint bits,
		jint stopbits, jint parity, jboolean rts, jboolean cts, jint timeout) {
	DCB dcb;
	int result;
	info("[DSU] initilize \n");
	Java_com_ibm_oti_connection_comm_Connection_configureImpl(jenv, jobj,handle, baudrate, bits, stopbits, parity, rts, cts, timeout);

	if (result=GetCommState((HANDLE)handle,&dcb))
    {
		dcb.fRtsControl = RTS_CONTROL_DISABLE;
		result= SetCommState((HANDLE)handle, &dcb );   
	}

}

JNIEXPORT void JNICALL Java_org_avm_device_connection_rs485_Connection_closeImpl(JNIEnv *jenv,
		jobject jobj, jint handle) {
	info("[DSU] call closeImpl()\n");
	Java_com_ibm_oti_connection_comm_Connection_closeImpl(jenv, jobj, handle);
}

JNIEXPORT jint JNICALL Java_org_avm_device_connection_rs485_Connection_readImpl(JNIEnv *jenv,
		jobject jobj, jint handle, jbyteArray buffer, jint off, jint len) {
	info("[DSU] call readImpl()\n");
	return Java_com_ibm_oti_connection_comm_Connection_readImpl(jenv, jobj,handle, buffer, off, len);
}

JNIEXPORT jint JNICALL Java_org_avm_device_connection_rs485_Connection_writeImpl(JNIEnv *jenv,
		jobject jobj, jint handle, jbyteArray buffer, jint off, jint len) {
	jint result;
	

	Sleep(200);
	setrts((HANDLE)handle,0);
	info("[DSU] write %d bytes\n",len);
	result =  Java_com_ibm_oti_connection_comm_Connection_writeImpl(jenv, jobj,
			handle, buffer, off, len);
	Sleep(200 + len*10);
	setrts((HANDLE)handle,1);
	return result;
}

JNIEXPORT jint JNICALL Java_org_avm_device_connection_rs485_Connection_availableImpl(
		JNIEnv *jenv, jobject jobj, jint handle) {
	info("[DSU] call availableImpl()\n");
	return Java_com_ibm_oti_connection_comm_Connection_availableImpl(jenv, jobj, handle);
}

BOOL WINAPI DllMain (HANDLE hinstDLL, DWORD dwReason, LPVOID lpvReserved)
{
    switch (dwReason)
	{
        case DLL_PROCESS_ATTACH:
			info("[DSU] library loaded\n");
			break;

    	 case DLL_PROCESS_DETACH:  
			info("[DSU] library unloaded\n");
			break;
		
		case DLL_THREAD_ATTACH:
			break;
			
		case DLL_THREAD_DETACH:
			break;
	}
	
    return TRUE;
}