#include <stdio.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>

#include <linux/lpc32x0_gpio.h>

#include "wirma2_io.h"

#define info(...)	fprintf(stderr, __VA_ARGS__)
#ifdef _DEBUG
#define debug(...)	fprintf(stderr, __VA_ARGS__)
#else
#define debug(...)
#endif

int dio_open(const char* name) {
	int handle = -1;

	if ((handle = open(name, O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	return handle;

	error: if (handle != -1) {
		close(handle);
	}

	return -1;
}

void dio_close(int handle) {
	if (handle != -1) {
		close(handle);
	}
}

int dio_read_input(int handle) {
	int cr = 0;
	unsigned int value = 0;

	if ((cr = ioctl(handle, LPC32X0_GPIO_WAIT, &value)) < 0) {
		return -1;
	}
	if ((cr = ioctl(handle, LPC32X0_GPIO_SET_INPUT, NULL)) < 0) {
		return -1;
	}
	if ((cr = ioctl(handle, LPC32X0_GPIO_READ, &value)) < 0) {
			return -1;
		}

	return value;
}

int dio_write_ouput(int handle, int value) {
	int cr = 0;
	int state = (value == 0) ? LPC32X0_GPIO_WRITE_LOW_LEVEL : LPC32X0_GPIO_WRITE_HIGH_LEVEL;
	if ((cr = ioctl(handle, state, NULL)) < 0) {
		return -1;
	}
	return 0;
}

