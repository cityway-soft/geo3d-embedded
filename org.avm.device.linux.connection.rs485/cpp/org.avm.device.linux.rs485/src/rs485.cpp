#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/ioctl.h>

#include "rs485.h"


#ifdef __cplusplus
extern "C" {
#endif

int Java_com_ibm_oti_connection_comm_Connection_openImpl(JNIEnv *, jobject,
		jint);
void Java_com_ibm_oti_connection_comm_Connection_configureImpl(JNIEnv *,
		jobject, jint, jint, jint, jint, jint, jboolean, jboolean, jint);
void Java_com_ibm_oti_connection_comm_Connection_closeImpl(JNIEnv *, jobject,
		jint);
jint Java_com_ibm_oti_connection_comm_Connection_readImpl(JNIEnv *, jobject,
		jint, jbyteArray, jint, jint);
jint Java_com_ibm_oti_connection_comm_Connection_writeImpl(JNIEnv *, jobject,
		jint, jbyteArray, jint, jint);
jint Java_com_ibm_oti_connection_comm_Connection_availableImpl(JNIEnv *,jobject, jint);

#ifdef __cplusplus
}
#endif

#define info printf

#ifdef _DEBUG
#define debug printf
#else
#define debug  //
#endif

# define HANDLE long

void setrts(HANDLE handle, int on) {
	int result;

	info("[DSU] setrts(%d)\n",on);

	int status;

	if (ioctl(handle, TIOCMGET, &status) == -1) {
		perror("setRTS(): TIOCMGET");
	}

	if (on == 0)
		status |= TIOCM_RTS;
	else
		status &= ~TIOCM_RTS;

	if (ioctl(handle, TIOCMSET, &status) == -1) {
		perror("setRTS(): TIOCMSET");
	}

}


JNIEXPORT jint JNICALL Java_org_avm_device_connection_rs485_Connection_openImpl(JNIEnv *jenv,
		jobject jobj, jint port) {
	info("[DSU] call openImpl()\n");
	return Java_com_ibm_oti_connection_comm_Connection_openImpl(jenv, jobj,port);
}

JNIEXPORT void JNICALL Java_org_avm_device_connection_rs485_Connection_configureImpl(
		JNIEnv *jenv, jobject jobj, jint handle, jint baudrate, jint bits,
		jint stopbits, jint parity, jboolean rts, jboolean cts, jint timeout) {
	info("[DSU] initilize \n");
	Java_com_ibm_oti_connection_comm_Connection_configureImpl(jenv, jobj,handle, baudrate, bits, stopbits, parity, rts, cts, timeout);
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

	usleep(200*1000);
	setrts((HANDLE)handle,0);
	info("[DSU] write %d bytes\n",len);
	result = Java_com_ibm_oti_connection_comm_Connection_writeImpl(jenv, jobj,
			handle, buffer, off, len);
	usleep(( 200 + len * 10 ) *1000);
	setrts((HANDLE)handle,1);
	return result;
}

JNIEXPORT jint JNICALL Java_org_avm_device_connection_rs485_Connection_availableImpl(
		JNIEnv *jenv, jobject jobj, jint handle) {
	info("[DSU] call availableImpl()\n");
	return Java_com_ibm_oti_connection_comm_Connection_availableImpl(jenv, jobj, handle);
}

