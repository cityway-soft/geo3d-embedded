#include <stdio.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>

#include <linux/klk-pic.h>
#include <linux/watchdog.h>

#include "klk_pic.h"

#define info(...)	fprintf(stderr, __VA_ARGS__)
#ifdef _DEBUG
#define debug(...)	fprintf(stderr, __VA_ARGS__)
#else
#define debug(...)
#endif

int _watchdog = -1;

int klkpic_open(const char* name) {
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

void klkpic_close(int handle) {
	if (handle != -1) {
		close(handle);
	}
}

int klkpic_read_apc(int handle) {
	int cr;
	klk_pic_state_t state;
	cr = ioctl(handle, KLK_PIC_GET_STATE, &state);
	return (state & 0xffff);
}

int klkpic_read_wakeup(int handle) {
	int cr;
	klk_pic_state_t state;
	cr = ioctl(handle, KLK_PIC_GET_WAKE_STATE, &state);
	return state;
}

void klkpic_keep_alive() {
	int dummy;
	if (_watchdog == -1) {
		if ((_watchdog = open("/dev/watchdog", O_WRONLY)) < 0) {
			goto error;
		}
	}
	ioctl(_watchdog, WDIOC_KEEPALIVE, &dummy);

	error: if (_watchdog != -1) {
		close(_watchdog);
		_watchdog = -1;
	}
}
