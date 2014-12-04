
#include <jni.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include "i2c-dev.h"
#include "org_avm_device_ihmi_z8_Z8Access.h"


#define DEVICE "/dev/i2c-0"
#define CHIP 0x6E

#define CMD_STATE 0x01
#define CMD_TEMP 0x03
#define CMD_VIN 0x04
#define CMD_BACKLIGHT 0x13
#define CMD_LIGHT 0x14
#define CMD_WATCHDOG 0x18


int readFromAddress(char address){
	int file;
	int ret;

	file = open(DEVICE, O_RDWR);
	ioctl(file,I2C_SLAVE, CHIP);
	ret = i2c_smbus_read_byte_data(file, address);
	close (file);
	return ret;
}

void setToAddress (char address, char val){
	int file;
	
	file = open(DEVICE, O_RDWR);
	ioctl(file,I2C_SLAVE, CHIP);
	i2c_smbus_write_byte_data(file, address, val);
	close (file);
}


JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getSystemCurrentState
  (JNIEnv *jniEnv, jclass jClass){
	return readFromAddress(CMD_STATE);
}

JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getBoardTemperature
  (JNIEnv *jniEnv, jclass jClass){
	return readFromAddress(CMD_TEMP);
}


JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getVIN
  (JNIEnv *jniEnv, jclass jClass){
	return readFromAddress(CMD_VIN);
}


JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getBacklightLevel
  (JNIEnv *jniEnv, jclass jClass){
	return readFromAddress(CMD_BACKLIGHT);
}


JNIEXPORT void JNICALL Java_org_avm_device_ihmi_z8_Z8Access_setBacklightLevel
  (JNIEnv *jniEnv, jclass jClass, jint level){
	setToAddress(CMD_BACKLIGHT, (char)level);
}


JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getLightLevel
  (JNIEnv *jniEnv, jclass jClass){
	return readFromAddress(CMD_LIGHT);
}


JNIEXPORT void JNICALL Java_org_avm_device_ihmi_z8_Z8Access_setWatchdog
  (JNIEnv *jniEnv, jclass jClass, jint time){
	setToAddress(CMD_WATCHDOG, (char)time);
}

JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_getWatchdog
  (JNIEnv *jniEnv, jclass jClass){
	return readFromAddress(CMD_WATCHDOG);
}

JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_opendevice
  (JNIEnv *jniEnv, jclass jClass){
	int file;
	file = open(DEVICE, O_RDWR);
	ioctl(file,I2C_SLAVE, CHIP);
	return file;
}

JNIEXPORT void JNICALL Java_org_avm_device_ihmi_z8_Z8Access_closedevice
  (JNIEnv *jniEnv, jclass jClass, jint id){
	if (id != 0){
		close(id);
	}
}

JNIEXPORT jint JNICALL Java_org_avm_device_ihmi_z8_Z8Access_readSystemCurrentState
  (JNIEnv *jniEnv, jclass jClass, jint id){
	if (id != 0){
		return i2c_smbus_read_byte_data(id, CMD_STATE);
	}
	return -1;
}


void JNI_OnUnload(JavaVM *vm, void *reserved){
	
}

