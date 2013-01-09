#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <linux/watchdog.h>
#include <linux/at91_gpio.h>

#define info(...)	fprintf(stderr, __VA_ARGS__)
#ifdef _DEBUG
#define debug(...)	fprintf(stderr, __VA_ARGS__)
#else
#define debug(...)
#endif

int _led = -1;
int _watchdog = -1;
int _handle = -1;

int _value = -1;
int _timeout = 600;

void flash() {
	ioctl(_led, AT91_GPIO_OUTPUT_SET, NULL);
	usleep(100000);
	ioctl(_led, AT91_GPIO_OUTPUT_CLR, NULL);
}

void keep_alive() {
	int dummy;
	debug("[DSU] call keep_alive \n");
	ioctl(_watchdog, WDIOC_KEEPALIVE, &dummy);
	flash();
}

void shutdown() {
	int handle = 0;

	info("[DSU] call shutdown \n");

	// cpu
	handle = open("/dev/iod8", O_RDWR);
	if (handle > 0) {
		ioctl(handle, AT91_GPIO_OUTPUT_CLR, NULL);
		close(handle);
	}

	// gsm
	handle = open("/dev/iod24", O_RDWR);
	if (handle > 0) {
		ioctl(handle, AT91_GPIO_OUTPUT_SET, NULL);
		usleep(10000);
		ioctl(handle, AT91_GPIO_OUTPUT_CLR, NULL);
		close(handle);
	}
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

	if ((_led = open("/dev/iob2", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((_watchdog = open("/dev/watchdog", O_WRONLY)) < 0) {
		goto error;
	}

	if ((_handle = open("/dev/ioc5", O_RDONLY | O_NONBLOCK)) < 0) {
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
	int current;
	int count = 3;

	while (count > 0) {
		if ((cr = ioctl(_handle, AT91_GPIO_INPUT_READ, &current)) < 0) {
			current = 0;
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

int main( int argc, char *argv[]) {
	int cr;
	int value = 0;
	long count = 0;
	int opt;

	while ((opt = getopt(argc, argv, "t: h")) != -1) {
		switch (opt) {
		case 'h':
			fprintf(stderr, "Usage: %s [-t nsecs] [-h] \n",  argv[0]);
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

	while (1) {

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
