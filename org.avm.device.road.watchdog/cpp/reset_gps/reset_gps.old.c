#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <linux/watchdog.h>
#include <linux/klk-pic.h>
#include <linux/lpc32x0_gpio.h>

#define info(...)	fprintf(stderr, __VA_ARGS__)
#ifdef _DEBUG
#define debug(...)	fprintf(stderr, __VA_ARGS__)
#else
#define debug(...)
#endif

void toggle_on_off_gps() {
	int power = -1;
	int gps = -1;
	int rtc = -1;

	// open
	if ((gps = open("/dev/gpo_14", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((rtc = open("/dev/p1_15", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((power = open("/dev/p1_17", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	// initialize
	if (ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(power, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// toggle on/off gps
	if (ioctl(power, LPC32X0_GPIO_WRITE_HIGH_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 120ms
	usleep(120 * 1000);

	if (ioctl(power, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	debug("[DSU] toggle on/off gps\n");

	//close
	error: if (gps != -1) {
		close(gps);
		gps = -1;
	}
	if (rtc != -1) {
		close(rtc);
		rtc = -1;
	}
	if (power != -1) {
		close(power);
		power = -1;
	}
	return;
}

void initialize_gps() {
	int power = -1;
	int gps = -1;
	int rtc = -1;

	// open
	if ((gps = open("/dev/gpo_14", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((rtc = open("/dev/p1_15", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((power = open("/dev/p1_17", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	// initialize
	if (ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(power, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// initialize gps

	if (ioctl(gps, LPC32X0_GPIO_WRITE_HIGH_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_HIGH_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 10s
	usleep(10* 1000000 );

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 120ms
	usleep(120 * 1000 );

	if (ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 200ms
	usleep(200 * 1000 );

	debug("[DSU] initialize_gps\n");

	//close
	error:
	if (gps != -1) {
		close(gps);
		gps = -1;
	}
	if (rtc != -1) {
		close(rtc);
		rtc = -1;
	}
	if (power != -1) {
		close(power);
		power = -1;
	}
	return;
}

void reset_gps() {
	int power = -1;
	int gps = -1;
	int rtc = -1;

	// open
	if ((gps = open("/dev/gpo_14", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((rtc = open("/dev/p1_15", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((power = open("/dev/p1_17", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	// initialize
	if (ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(power, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// reset gps
	if (ioctl(gps, LPC32X0_GPIO_WRITE_HIGH_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 3s
	usleep(3* 1000000 );

	if(ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) <0) {
		goto error;
	}

	// wait 200ms
	usleep(200*1000);

	debug("[DSU] reset_gps \n");

	//close
	error:
	if (gps != -1) {
		close(gps);
		gps = -1;
	}
	if (rtc != -1) {
		close(rtc);
		rtc = -1;
	}
	if (power != -1) {
		close(power);
		power = -1;
	}
	return;
}

void wakeup_gps() {
	klk_pic_state_t state;
	int handle = -1;

	if ((handle = open("/dev/klk-pic", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if (ioctl(handle, KLK_PIC_GET_WAKE_STATE, &state) < 0) {
		goto error;
	}
	if ((state & KLK_PIC_STATE_FIRST_WAKEUP) == KLK_PIC_STATE_FIRST_WAKEUP) {
		initialize_gps();
	} else {
		toggle_on_off_gps();
	}

	debug("[DSU] wakeup_gps ok\n");

	error: if (handle != -1) {
		close(handle);
		handle = -1;
	}
	return;
}

int main(int argc, char *argv[]) {
	int opt;

	while ((opt = getopt(argc, argv, "nrih")) != -1) {
		switch (opt) {
		case 'h':
			fprintf(stderr, "Usage: %s [-n] [-r] [-i] [-h] \n", argv[0]);
			break;
		case 'r':
			reset_gps();
			break;
		case 'i':
			initialize_gps();
			break;
		case 'n':
			wakeup_gps();
			break;
		}
	}

	return EXIT_SUCCESS;
}

