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

jint JNICALL Java_org_avm_device_protocol_comm_CommConnection_openImpl(
		JNIEnv *, jobject, jint, jint, jint, jint);

void JNICALL Java_org_avm_device_protocol_comm_CommConnection_closeImpl(
		JNIEnv *, jobject, jint);

jint JNICALL Java_org_avm_device_protocol_comm_CommConnection_readImpl(
		JNIEnv *, jobject, jint, jbyteArray, jint, jint);

void JNICALL Java_org_avm_device_protocol_comm_CommConnection_writeImpl(
		JNIEnv *, jobject, jint, jbyteArray, jint, jint);

jint JNICALL Java_org_avm_device_protocol_comm_CommConnection_availableImpl(
		JNIEnv *, jobject, jint);

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
# define OFF 0
# define ON 1

void setrts(HANDLE handle, int rts) {

	info("[DSU] setrts(%d)\n", rts);

	int status;

	if (ioctl(handle, TIOCMGET, &status) == -1) {
		perror("setRTS(): TIOCMGET");
	}

	if (rts == ON)
		status |= TIOCM_RTS;
	else
		status &= ~TIOCM_RTS;

	if (ioctl(handle, TIOCMSET, &status) == -1) {
		perror("setRTS(): TIOCMSET");
	}

}

JNIEXPORT jint JNICALL Java_org_avm_device_protocol_rs485_Rs485Connection_openImpl(
		JNIEnv *jenv, jobject jobj, jint port, jint baud, jint flags,
		jint timeout) {
	info("[DSU] call openImpl()\n");
	return Java_org_avm_device_protocol_comm_CommConnection_openImpl(jenv,
			jobj, port, baud, flags, timeout);
}

JNIEXPORT void JNICALL Java_org_avm_device_protocol_rs485_Rs485Connection_closeImpl(
		JNIEnv *jenv, jobject jobj, jint handle) {
	info("[DSU] call closeImpl()\n");
	Java_org_avm_device_protocol_comm_CommConnection_closeImpl(jenv, jobj,
			handle);
}

JNIEXPORT jint JNICALL Java_org_avm_device_protocol_rs485_Rs485Connection_readImpl(
		JNIEnv *jenv, jobject jobj, jint handle, jbyteArray buffer, jint off,
		jint len) {
	info("[DSU] call readImpl()\n");
	return Java_org_avm_device_protocol_comm_CommConnection_readImpl(jenv,
			jobj, handle, buffer, off, len);
}

JNIEXPORT void JNICALL Java_org_avm_device_protocol_rs485_Rs485Connection_writeImpl(
		JNIEnv *jenv, jobject jobj, jint handle, jbyteArray buffer, jint off,
		jint len) {

	usleep(200 * 1000);
	setrts((HANDLE) handle, ON);
	info("[DSU] write %d bytes\n", len);
	Java_org_avm_device_protocol_comm_CommConnection_writeImpl(jenv, jobj,
			handle, buffer, off, len);
	usleep((200 + len * 10) * 1000);
	setrts((HANDLE) handle, OFF);

}

JNIEXPORT jint JNICALL Java_org_avm_device_protocol_rs485_Rs485Connection_availableImpl(
		JNIEnv *jenv, jobject jobj, jint handle) {
	info("[DSU] call availableImpl()\n");
	return Java_org_avm_device_protocol_comm_CommConnection_availableImpl(jenv,
			jobj, handle);
}

