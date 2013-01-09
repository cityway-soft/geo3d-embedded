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

int _led = -1;
int _watchdog = -1;
int _handle = -1;

int _started = 1;
int _value = -1;
int _timeout = 600;

void flash() {
	ioctl(_led, LPC32X0_GPIO_WRITE_HIGH_LEVEL, 0);
	usleep(100000);
	ioctl(_led, LPC32X0_GPIO_WRITE_LOW_LEVEL, 0);

}

void keep_alive() {
	int dummy;
	debug("[DSU] call keep_alive \n");
	ioctl(_watchdog, WDIOC_KEEPALIVE, &dummy);
	flash();
}

void shutdown() {
	info("[DSU] call shutdown \n");
	_started = 1;
}

void timeout(int signum) {
	shutdown();
}

void power_up() {
	info("[DSU] call power_up \n");
	alarm(0);
}

void power_down() {
	info("[DSU] call power_down \n");
	alarm(_timeout);
}

void dispose() {

	signal(SIGALRM, SIG_DFL);

	if (_led != -1) {
		close(_led);
	}

	if (_handle != -1) {
		close(_handle);
	}

	if (_watchdog != -1) {
		close(_watchdog);
	}

}

int initialize() {

	if ((_led = open("/dev/p1_20", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((_watchdog = open("/dev/watchdog", O_WRONLY)) < 0) {
		goto error;
	}

	if ((_handle = open("/dev/klk-pic", O_RDONLY | O_NONBLOCK)) < 0) {
		goto error;
	}

	signal(SIGALRM, timeout);

	return 0;

	error: dispose();

	return -1;
}

int read_apc() {
	int cr;
	int value = _value;
	klk_pic_state_t state;
	int current;
	int count = 3;

	while (count > 0) {
		if ((cr = ioctl(_handle, KLK_PIC_GET_STATE, &state)) < 0) {
			current = 0;
		} else {
			current = ((state & KLK_PIC_STATE_NAPC_PIN)
					== KLK_PIC_STATE_NAPC_PIN) ? 0 : 1;
		}

		if (current == value) {
			count--;
		} else {
			value = current;
			count = 2;
		}
		usleep(10000);
	}
	return value;
}

int main(int argc, char *argv[]) {
	int cr;
	int value = 0;
	long count = 0;
	int opt;

	while ((opt = getopt(argc, argv, "t: h")) != -1) {
		switch (opt) {
		case 'h':
			fprintf(stderr, "Usage: %s [-t nsecs] [-h] \n", argv[0]);
			return EXIT_SUCCESS;
			break;
		case 't':
			_timeout = atoi(optarg);
			break;
		default:
			_timeout = 600;
			break;
		}
	}

	if ((cr = initialize()) < 0) {
		return EXIT_FAILURE;
	}

	_value = read_apc();
	if (_value == 0) {
		shutdown();
	}

	while (_started) {

		value = read_apc();
		if (_value != value) {
			_value = value;
			if (_value == 0) {
				power_down();
			} else {
				power_up();
			}
		}

		if (count++ % 10 == 0) {
			keep_alive();
		}

		usleep(100000);
	}

	dispose();

	return EXIT_SUCCESS;
}
