#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <termios.h>
#include "nexcom_driver_access.h"

#define BASEADDR1 0x0ED2         // GPO Base address
#define BASEADDR2 0x0ED1         // GPI Base address
#define BASEADDR3 0x0ED6         // GPIO Type Base address
#define BASEADDR4 0x0EE0	// Ign state

#define DEVICE "/dev/NEXCOM_IO"

#define OPEN 1
#define CLOSE 0

typedef struct {
	int fileHandle;
	char opened;
	int read_count;
	int last;
} NexcomIo;


JNIEXPORT jint JNICALL Java_org_avm_device_vtc1010_common_NexcomDriverAccess_open
  (JNIEnv *jniEnv, jclass jClass){
	NexcomIo *handle = (NexcomIo *) malloc (sizeof(NexcomIo));
	handle->fileHandle = open (DEVICE, O_RDWR);
	handle->opened =  OPEN;
	handle->read_count = 0;
	handle->last = 0;
	return (int) handle;
}


JNIEXPORT void JNICALL Java_org_avm_device_vtc1010_common_NexcomDriverAccess_close
  (JNIEnv *jniEnv, jclass jClass, jint ptr){
	NexcomIo *handle = (NexcomIo *) ptr;

	handle->opened = CLOSE;
	while(handle->read_count > 0){
		usleep(50000);
	}
	close (handle->fileHandle);
	free(handle);
}

JNIEXPORT jint JNICALL Java_org_avm_device_vtc1010_common_NexcomDriverAccess_readInput
  (JNIEnv *jniEnv, jclass jClass, jint ptr, jint block){
	int bfr = 0;
	NexcomIo *handle = (NexcomIo *) ptr;

	
	handle->read_count++;
	if (handle->fileHandle != 0){
		do{
			read(handle->fileHandle,&bfr,BASEADDR2);
			usleep(100000);
		} while (bfr == handle->last && handle->opened == OPEN && block==1);
		handle->last = bfr;
	}
	handle->read_count--;
	return bfr;
}


JNIEXPORT void JNICALL Java_org_avm_device_vtc1010_common_NexcomDriverAccess_setOutput
  (JNIEnv *jniEnv, jclass jClass, jint ptr, jint value){
	NexcomIo *handle = (NexcomIo *) ptr;

	if (handle->fileHandle != 0){
		write(handle->fileHandle,&value,BASEADDR1);
	}
}

JNIEXPORT jint JNICALL Java_org_avm_device_vtc1010_common_NexcomDriverAccess_readConfig
  (JNIEnv *jniEnv, jclass jClass, jint handle){

	return 0;
}

JNIEXPORT void JNICALL Java_org_avm_device_vtc1010_common_NexcomDriverAccess_writeConfig
  (JNIEnv *jniEnv, jclass jClass, jint handle, jint value){
}

JNIEXPORT jint JNICALL Java_org_avm_device_vtc1010_common_NexcomDriverAccess_readIgn
  (JNIEnv *jniEnv, jclass jClass, jint ptr){
	int bfr = 0;
	NexcomIo *handle = (NexcomIo *) ptr;

	handle->read_count++;
	if (handle->fileHandle != 0){
		do{
			read(handle->fileHandle,&bfr,BASEADDR4);
			usleep(100000);
		} while (bfr == handle->last && handle->opened == OPEN);
		handle->last = bfr;
	}
	handle->read_count--;
	return bfr;
}
