#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <linux/can4linux.h>

#define info(...)	fprintf(stderr, __VA_ARGS__)
#ifdef _DEBUG
#define debug(...)	fprintf(stderr, __VA_ARGS__)
#else
#define debug(...)
#endif

#define CAN_SFF_MASK 0x000007FFU   /* standard frame format (SFF) */
#define CAN_EFF_MASK 0x1FFFFFFFU   /* extended frame format (EFF) */
#define CAN_EFF_FLAG 0x80000000U /* EFF/SFF is set in the MSB */

#define FRAME_LEN 14
#define BUFFER_SIZE 80
typedef unsigned char byte;

int _timeout = 0;

unsigned int _counter = 0;
unsigned int _frame = 0;

void toByteArray(canmsg_t *frame, byte b[], int off) {
	unsigned int id;
	byte flag;
	int i = 0;

	if ((frame->flags & MSG_EXT) == MSG_EXT) {
		id = frame->id & CAN_EFF_MASK;
	} else {
		id = frame->id & CAN_SFF_MASK;
	}

	// little endian to big endian
	b[off++] = (byte) (id & 0xff);
	b[off++] = (byte) ((id >> 8) & 0xff);
	b[off++] = (byte) ((id >> 16) & 0xff);
	b[off++] = (byte) ((id >> 24) & 0xff);

	flag = ((frame->flags & MSG_EXT) == MSG_EXT) ? 2 : 0;
	flag = ((frame->flags & MSG_RTR) == MSG_RTR) ? 1 : flag;

	b[off++] = flag;
	b[off++] = (byte) frame->length;

	for (i = 0; i < frame->length; i++) {
		b[off++] = frame->data[i];
	}

}

void fromByteArray(byte b[], int off, canmsg_t *frame) {
	unsigned int id;
	int i = 0;

	// big endian to little endian
	id = (b[0] & 0x000000ff) | ((b[1] << 8) & 0x0000ff00) | ((b[2] << 16)
			& 0x00ff0000) | (b[3] << 24);

	off += 4;
	byte flag = b[off++];
	switch (flag) {
	case 0:
		frame->id &= CAN_SFF_MASK;
		frame->flags &= ~MSG_EXT;
	case 1:
		frame->id &= CAN_EFF_MASK;
		frame->flags |= MSG_RTR;
	case 2:
		frame->id &= CAN_EFF_MASK;
		frame->flags |= MSG_EXT;
	}
	frame->id = id;
	frame->length = b[off++];

	for (i = 0; i < frame->length; i++) {
		frame->data[i] = b[off++];
	}
}

void clear_filter(int handle) {
	Config_par_t cfg;

	cfg.target = CONF_ACC;
	cfg.val1 = 0;
	cfg.val2 = 0;
	info("[DSU] clear filter handle = 0x%x \n", handle);
	ioctl(handle, CONFIG, &cfg);
}

void set_mask(int handle, unsigned long mask) {
	Config_par_t cfg;

	if ((mask & CAN_EFF_FLAG) == CAN_EFF_FLAG) {

		// extended identifier mask
		cfg.target = CONF_ACCM;
		cfg.val1 = ((mask & CAN_EFF_MASK) & 0x3FFFF) | // extended identifier part
				(((mask & CAN_EFF_MASK) & 0xFFFC0000) << 3); // standard identifier part
		info("[DSU] set extended mask 0x%x  handle = 0x%x \n", (unsigned int) cfg.val1, handle);
		ioctl(handle, CONFIG, &cfg);

	} else {

		// standard identifier mask
		cfg.target = CONF_ACC;
		cfg.val1 = ((mask & CAN_SFF_MASK) << 21);
		info("[DSU] set standard mask 0x%x handle = 0x%x \n",(unsigned int) cfg.val1, handle);
		ioctl(handle, CONFIG, &cfg);
	}
}

void set_filter(int handle, unsigned long filter) {
	Config_par_t cfg;

	if ((filter & CAN_EFF_FLAG) == CAN_EFF_FLAG) {

		// extended identifier filter
		cfg.target = CONF_ACCC;
		cfg.val1 = ((filter & CAN_EFF_MASK) & 0x3FFFF) | // extended identifier part
				(((filter & CAN_EFF_MASK) & 0xFFFC0000) << 3) | // standard identifier part
				0x80000; // extended identifier enable bit
		info("[DSU] set extended filter 0x%x handle = 0x%x \n",(unsigned int) cfg.val1, handle);
		ioctl(handle, CONFIG, &cfg);

	} else {

		// standard identifier filter
		cfg.target = CONF_ACCC;
		cfg.val1 = ((filter & CAN_SFF_MASK) << 21);
		info("[DSU] set standard filter 0x%x handle = 0x%x \n",(unsigned int) cfg.val1, handle);
		ioctl(handle, CONFIG, &cfg);
	}
}

int can_open(int port, int baudrate, int timeout, unsigned int filter[],
		unsigned int mask[]) {
	char device[50];
	int handle;
	Config_par_t cfg;
	Command_par_t cmd;
	int i = 0;
	errno = 0;

	unsigned long mask1 = 0xFFFFFFFF;
	unsigned long mask2 = 0xFFFFFFFF;

	// open device
	sprintf(device, "/dev/can%d", port);
	info("[DSU] open device %s\n", device);

	if (timeout == 0) {
		// device non bloquant
		if ((handle = open(device, O_RDWR | O_NONBLOCK)) < 0) {
			char* message = strerror(errno);
			info("[DSU] error: %s\n", message);
			return -1;
		}
	} else {
		// devicet bloquant & timeout
		if ((handle = open(device, O_RDWR)) < 0) {
			char* message = strerror(errno);
			info("[DSU] error: %s\n", message);
			return -1;
		}

		// timeout
		//TODO memo timeout <- handle
		_timeout = timeout;
	}

	// stop
	cmd.cmd = CMD_STOP;
	if (ioctl(handle, COMMAND, &cmd) < 0) {
		char* message = strerror(errno);
		close(handle);
		info("[DSU] error: %s\n", message);
		return -1;
	}

	// baudrate
	cfg.target = CONF_TIMING;
	cfg.val1 = baudrate;
	if (ioctl(handle, CONFIG, &cfg) < 0) {
		char* message = strerror(errno);
		close(handle);
		info("[DSU] error: %s\n", message);
		return -1;
	}

	// filter
	int length = sizeof(filter)/sizeof(unsigned int);
	length = (length > 6) ? 6 : length;

	clear_filter(handle);

	_frame = 0;
	_counter = 0;

	if (length > 0) {
		for (i = 0; i < length; i++) {
			if (i < 2) {
				mask1 &= mask[i];
			} else {
				mask2 &= mask[i];
			}
			set_filter(handle, filter[i]);
		}

		set_mask(handle, mask1);
		if (length > 2) {
			set_mask(handle, mask2);
		}
	} else {
		debug("[DSU] set no filter\n");
	}

	// start
	cmd.cmd = CMD_START;
	if (ioctl(handle, COMMAND, &cmd) < 0) {
		char* message = strerror(errno);
		close(handle);
		info("[DSU] error: %s\n", message);
		return -1;
	}

	info("[DSU] device %s opened handle = %d \n", device, handle);

	return handle;
}

void can_close(int handle) {
	info("[DSU] close device handle = %d \n", handle);
	info("[DSU] %d frame read \n", _frame);
	if (handle > 0) {
		close(handle);
	}
}

int can_read(int handle, byte buffer[], int offset, int length) {
	canmsg_t frame[BUFFER_SIZE];
	errno = 0;
	int n, i, cr;
	byte b[FRAME_LEN];
	int count;
	int off = offset;
	n = 0;
	count = (length / FRAME_LEN);
	count = (count > BUFFER_SIZE) ? BUFFER_SIZE : count;
	_counter++;

	fd_set rfds;
	struct timeval tv;
	//debug("[DSU] call read handle = %d length %d bytes offset = %d \n", handle, length, offset);

	FD_ZERO(&rfds);
	FD_SET(handle,&rfds);
	tv.tv_sec = 0;
	tv.tv_usec = _timeout * 1000;
	if (select(FD_SETSIZE, &rfds, NULL, NULL, &tv) > 0
			&& FD_ISSET(handle,&rfds)) {
		if ((cr = read(handle, frame, count)) > 0) {
			_frame += cr;

			for (i = 0; i < cr; i++) {
				toByteArray(&frame[i], b, 0);
				off += FRAME_LEN;
				n += FRAME_LEN;
			}
		}
		if (n > 0)
			debug("[DSU] read handle = %d length %d bytes \n", handle, n);
	} else {
		debug("[DSU] aucune donnée disponible, ou timeout expiré \n");
	}

	if (_counter % 5000 == 0) {
		info("[DSU] %d frame read / %d read call \n", _frame, _counter);
	}

	return (n == 0) ? -1 : n;
}

int main(int argc, char **argv) {
	unsigned int filter[] = { 0x80f00400, 0x80f00300, 0x80fef100, 0x80fef200,
			0x80f00500 };
	unsigned int mask[] = { 0x80ffff00, 0x80ffff00, 0x80ffff00, 0x80ffff00,
			0x80ffff00 };
	byte buffer[80];

	int handle = can_open(0, 250, 100, filter, mask);
	while (1) {
		can_read(handle, buffer, 0, sizeof(buffer));
	}
	can_close(handle);
	return 0;
}

