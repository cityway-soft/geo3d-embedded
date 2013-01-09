//  [DSU] parametre du module sja1000-io base=0x140,0x340 irq=5,5 speed=250,250
//
//	description:    LLCF/socketcan 'sja1000' network device driver
//	license:        Dual BSD/GPL
//	author:         Oliver Hartkopp <oliver.hartkopp@volkswagen.de>
//	depends:
//	vermagic:       2.6.25 mod_unload PENTIUM4
//	parm:           base:CAN controller base address (array of int)
//	parm:           irq:CAN controller interrupt (array of int)
//	parm:           speed:CAN bus bitrate (array of int)
//	parm:           btr:Bit Timing Register value 0x<btr0><btr1>, e.g. 0x4014 (array of int)
//	parm:           rx_probe:switch to trx mode after correct msg receiption. (default off) (array of int)
//	parm:           clk:CAN controller chip clock (default: 16MHz) (int)
//	parm:           debug:set debug mask (default: 0) (int)
//	parm:           restart_ms:restart chip on heavy bus errors / bus off after x ms (default 100ms) (int)
//	parm:           echo:Echo sent frames. default: 1 (On) (int)

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <net/if.h>

#include <linux/can.h>
#include <linux/can/raw.h>

#include "vcan.h"

#define info(...)	fprintf(stderr, __VA_ARGS__)
#ifdef _DEBUG
#define debug(...)	fprintf(stderr, __VA_ARGS__)
#else
#define debug(...)
#endif

#define FRAME_LEN 14
typedef unsigned char byte;

void throwIOException(JNIEnv* env, char* msg) {
	jclass e;
	e = (*env)->FindClass(env, "java/io/IOException");
	(*env)->ThrowNew(env, e, msg);
}

void toByteArray(struct can_frame *frame, jbyte b[], int off) {
	unsigned int id;
	byte flag;
	int i = 0;

	if ((frame->can_id & CAN_EFF_FLAG) == CAN_EFF_FLAG) {
		id = frame->can_id & CAN_EFF_MASK;
	} else {
		id = frame->can_id & CAN_SFF_MASK;
	}

	// little endian to big endian
	b[off++] = (byte) (id & 0xff);
	b[off++] = (byte) ((id >> 8) & 0xff);
	b[off++] = (byte) ((id >> 16) & 0xff);
	b[off++] = (byte) ((id >> 24) & 0xff);

	flag = ((frame->can_id & CAN_EFF_FLAG) == CAN_EFF_FLAG) ? 2 : 0;
	flag = ((frame->can_id & CAN_RTR_FLAG) == CAN_RTR_FLAG) ? 1 : flag;

	b[off++] = flag;
	b[off++] = (byte) frame->can_dlc;

	for (i = 0; i < frame->can_dlc; i++) {
		b[off++] = frame->data[i];
	}

}

void fromByteArray(jbyte b[], int off, struct can_frame *frame) {
	unsigned int id;
	int i = 0;

	// big endian to little endian
	id = (b[0] & 0x000000ff) | ((b[1] << 8) & 0x0000ff00) | ((b[2] << 16)
			& 0x00ff0000) | (b[3] << 24);

	off += 4;
	byte flag = b[off++];
	switch (flag) {
	case 0:
		id &= CAN_SFF_MASK;
		id &= ~CAN_EFF_FLAG;
	case 1:
		id &= CAN_EFF_MASK;
		id |= CAN_RTR_FLAG;
	case 2:
		id &= CAN_EFF_MASK;
		id |= CAN_EFF_FLAG;
	}
	frame->can_id = id;
	frame->can_dlc = b[off++];

	for (i = 0; i < frame->can_dlc; i++) {
		frame->data[i] = b[off++];
	}
}

/*
 * Class:     org_avm_device_protocol_vcan_Connection
 * Method:    openImpl
 * Signature: (III[I[I)I
 * @param port num of the can device
 * @param baudrate 	the baudrate at which to set the can device
 * @param timeout  	timeout value in milliseconds for read methods, if 0 methods will not block
 * @param filter 	the filter array of the can device
 * @param mask		the mask array to the filters of the can device
 * @return handle of the can device
 * @throws IOException
 */
JNIEXPORT jint JNICALL Java_org_avm_device_protocol_vcan_Connection_openImpl(
		JNIEnv *env, jobject obj, jint port, jint baudrate, jint timeout,
		jintArray filter, jintArray mask) {
	int handle;
	struct sockaddr_can addr;
	struct ifreq ifr;
	struct timeval timer;
	int i = 0;

	errno = 0;

	// open socket
	info("[DSU] open socket \n");
	if ((handle = socket(PF_CAN, SOCK_RAW, CAN_RAW)) < 0) {
		char* message = strerror(errno);
		throwIOException(env, message);
		return -1;
	}

	sprintf(ifr.ifr_name, "can%d", port);
	if (ioctl(handle, SIOCGIFINDEX, &ifr) < 0) {
		char* message = strerror(errno);
		close(handle);
		throwIOException(env, message);
		return -1;
	}

	jsize length = (*env)->GetArrayLength(env, filter);
	jint* can_id = (*env)->GetIntArrayElements(env, filter, JNI_FALSE);
	jint* can_mask = (*env)->GetIntArrayElements(env, mask, JNI_FALSE);

	struct can_filter *rfilter = (struct can_filter *) malloc(
			sizeof(struct can_filter) * length);

	for (i = 0; i < length; i++) {
		rfilter[i].can_id = can_id[i];
		rfilter[i].can_mask = can_mask[i];
		info("[DSU] set filter 0x%x / 0x%x \n", rfilter[i].can_id, rfilter[i].can_mask);
	}

	(*env)->ReleaseIntArrayElements(env, filter, can_id, JNI_ABORT);
	(*env)->ReleaseIntArrayElements(env, mask, can_mask, JNI_ABORT);

	if (length > 0) {

		if (setsockopt(handle, SOL_CAN_RAW, CAN_RAW_FILTER, NULL, 0) != 0) {
			char* message = strerror(errno);
			free(rfilter);
			close(handle);
			throwIOException(env, message);
			return -1;
		}

		if (setsockopt(handle, SOL_CAN_RAW, CAN_RAW_FILTER, rfilter,
				sizeof(struct can_filter) * length) != 0) {
			char* message = strerror(errno);
			free(rfilter);
			close(handle);
			throwIOException(env, message);
			return -1;
		}
	} else {
		debug("[DSU] set no filter\n");
	}
	free(rfilter);

	addr.can_family = AF_CAN;
	addr.can_ifindex = ifr.ifr_ifindex;

	if (timeout == 0) {
		debug("[DSU] set O_NONBLOCK \n");
		// socket non bloquante
		if (fcntl(handle, F_SETFL, O_NONBLOCK) < 0) {
			char* message = strerror(errno);
			close(handle);
			throwIOException(env, message);
			return -1;
		}
	} else {
		debug("[DSU] set SO_RCVTIMEO = %d \n", timeout);
		// socket bloquante & timeout
		timer.tv_sec = timeout / 1000;
		timer.tv_usec = timeout % 1000 * 1000;
		if (setsockopt(handle, SOL_SOCKET, SO_RCVTIMEO, &timer, sizeof(timer))
				< 0) {
			char* message = strerror(errno);
			close(handle);
			throwIOException(env, message);
			return -1;
		}
	}

	if (bind(handle, (struct sockaddr *) &addr, sizeof(addr)) < 0) {
		char* message = strerror(errno);
		close(handle);
		throwIOException(env, message);
		return -1;
	}

	info("[DSU] socket interface %s opened handle = %d \n", ifr.ifr_name, handle);

	return (jint) handle;
}

/*
 * Class:     org_avm_device_protocol_vcan_Connection
 * Method:    closeImpl
 * Signature: (I)V
 * @param handle 	handle of the can device
 * @throws IOException
 */
JNIEXPORT
void JNICALL Java_org_avm_device_protocol_vcan_Connection_closeImpl(
		JNIEnv *env, jobject obj, jint handle) {
	info("[DSU] close socket handle = %d \n", handle);
	if (handle > 0) {
		close(handle);
	}
}

/*
 * Class:     org_avm_device_protocol_vcan_Connection
 * Method:    readImpl
 * Signature: (I[BII)I
 * @param handle	handle of the can device
 * @param buffer 	byte array in which to store the read bytes
 * @param off 		offset at which to start storing the bytes in the byte array
 * @param len 		number of bytes to read
 * @return 			number of bytes read or -1 if EOF
 * @exception IOException
 */
JNIEXPORT
jint JNICALL Java_org_avm_device_protocol_vcan_Connection_readImpl(JNIEnv *env,
		jobject obj, jint handle, jbyteArray buffer, jint offset, jint length) {
	struct can_frame frame;
	errno = 0;
	int n, i, cr;
	jbyte b[FRAME_LEN];
	int count;
	int off = offset;
	n = 0;
	count = (length / FRAME_LEN);

	//debug("[DSU] call read handle = %d length %d bytes offset = %d \n", handle, length, offset);

	for (i = 0; i < count; i++) {
		if ((cr = read(handle, &frame, sizeof(struct can_frame))) < 0) {
			debug("[DSU] aucune donnée disponible, ou timeout expiré \n");
			break;
		}
		toByteArray(&frame, b, 0);

		(*env)->SetByteArrayRegion(env, buffer, off, FRAME_LEN, b);
		off += FRAME_LEN;
		n += FRAME_LEN;

	}
	if (n > 0)
		debug("[DSU] read handle = %d length %d bytes \n", handle, n);
	return (n == 0) ? -1 : n;
}

/*
 * Class:     org_avm_device_protocol_vcan_Connection
 * Method:    writeImpl
 * Signature: (I[BII)I
 * @param handle	handle of the can device
 * @param buffer	bytes to be written to the serial port
 * @param off 		offset at which to start reading the bytes from the byte array
 * @param len 		number of bytes to write
 * @return			number of bytes written
 * @throws IOException
 */
JNIEXPORT jint JNICALL Java_org_avm_device_protocol_vcan_Connection_writeImpl(
		JNIEnv *env, jobject obj, jint handle, jbyteArray buffer, jint offset,
		jint length) {
	struct can_frame frame;
	errno = 0;
	int off = 0;
	int n, i, cr;

	int count = (length / FRAME_LEN);
	n = 0;

	jbyte* b = (jbyte *) malloc(length);
	(*env)->GetByteArrayRegion(env, buffer, offset, length, b);

	for (i = 0; i < count; i++) {
		fromByteArray(b, off, &frame);
		if ((cr = write(handle, &frame, sizeof(struct can_frame))) < 0) {
			break;
		}
		off += FRAME_LEN;
		n += FRAME_LEN;
	}

	free(b);

	debug("[DSU] call write handle = %d length %d bytes \n", handle, n);
	return n;

}

/*
 * Class:     org_avm_device_protocol_vcan_Connection
 * Method:    availableImpl
 * Signature: (I)I
 * @param handle	handle of the can device
 * @return 			number of bytes available before blocking
 * @throws IOException
 */
JNIEXPORT jint JNICALL Java_org_avm_device_protocol_vcan_Connection_availableImpl(
		JNIEnv *env, jobject obj, jint handle) {
	return 0;
}

