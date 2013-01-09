#include <stdio.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <linux/i2c-dev.h>

#include "nomad_io.h"

#define info(...)	fprintf(stderr, __VA_ARGS__)
#ifdef _DEBUG
#define debug(...) fprintf(stderr, __VA_ARGS__)
#else
#define debug(...)
#endif

#define TEST_BIT(x, n)   (((x) & (1<<(n)))?1:0)
#define SET_BIT(x, n)      ((x) |= (1<<(n)))
#define CLEAR_BIT(x, n)    ((x) &= ~(1<<(n)))
#define TOGGLE_BIT(x, n)   ((x) ^= (1<<(n)))

#define I2C_SLAVE	0x0703

#define EXT_IO_INPUT_REGISTER   0x00
#define EXT_IO_OUTPUT_REGISTER  0x02
#define EXT_IO_CONFIG_REGISTER  0x06
#define EXT_IO_IRQ_REGISTER     0x08
#define EXT_IO_COUNTER_REGISTER 0x0A

#define TIMEOUT 	1000000
#define PERIOD 		10000

// digital input bit 0-5
// counter bit 6-7
// digital ouput bit 8-13
// adc input bit 14-15

u16 _dio_input_register;
u16 _dio_output_register;
u16 _dio_counter_register;

u16 _dio_config_register = 0xC0FF;
u16 _dio_irq_register = 0xFF3F;

u16 read_register(int handle, u8 reg, u16* value) {
	char buffer[2];

	buffer[0] = reg;
	if (write(handle, buffer, 1) != 1) {
		return -1;
	}

	if (read(handle, buffer, 2) != 2) {
		return -1;
	}
	u16 cr = buffer[0] | (buffer[1] << 8);
	(*value) = cr;

	return 0;
}

u16 write_register(int handle, u8 reg, u16 value) {
	char buffer[3];
	buffer[0] = reg;
	buffer[1] = (u8) (value) & 0xFF;
	buffer[2] = (u8) (value >> 8) & 0xFF;
	if (write(handle, buffer, 3) != 3) {
		return -1;
	}
	return 0;
}

int dio_open(const char* name) {
	int handle = -1;
	int cr = 0;
	char* message = NULL;

	info("[DSU] open nomad io \n");

	if ((handle = open(name, O_RDWR)) < 0) {
		goto error;
	}

	if (ioctl(handle, I2C_SLAVE, 0x41) < 0) {
		goto error;
	}

	// write config register
	if ((cr = write_register(handle, EXT_IO_CONFIG_REGISTER,
			_dio_config_register)) < 0) {
		return -1;
	}

	// write irq register
	if ((cr = write_register(handle, EXT_IO_IRQ_REGISTER, _dio_irq_register))
			< 0) {
		goto error;
	}

	// read output resgister
	if ((cr = read_register(handle, EXT_IO_OUTPUT_REGISTER,
			&_dio_output_register)) < 0) {
		goto error;
	}

	// read input resgister
	if ((cr
			= read_register(handle, EXT_IO_INPUT_REGISTER, &_dio_input_register))
			< 0) {
		goto error;
	}

	info("[DSU] nomad io  %s opened handle = 0x%0x \n", name, handle);

	return handle;

	error: message = strerror(errno);
	info("[DSU] echec open nomad io : %s \n", message);
	if (handle != -1) {
		close(handle);
	}

	return (u16) -1;
}

void dio_close(int handle) {
	info("[DSU] close nomad io handle = %0x%0x \n", handle);
	if (handle != -1) {
		close(handle);
	}
}

u16 dio_read_input(int handle) {
	int cr = 0;
	int count = TIMEOUT / PERIOD;
	u16 value;
	u16 mask = 0x003F;
	char* message = NULL;

	debug("[DSU] call read input handle = 0x%0x \n", handle);
	while (count-- > 0) {

		// read input resgister
		if ((cr = read_register(handle, EXT_IO_INPUT_REGISTER, &value)) < 0) {
			message = strerror(errno);
			info("[DSU] echec read input handle = 0x%0x : %s \n", handle, message);
			return _dio_input_register;
		}

		if ((_dio_input_register & mask) != (value & mask)) {
			_dio_input_register = value;
			break;
		}

		usleep(PERIOD);
	}

	debug("[DSU] read input handle = 0x%0x value = 0x%0x \n", handle, _dio_input_register);
	return (value & mask);
}

u16 dio_write_ouput(int handle, u16 value, u16 mask) {
	char* message = NULL;
	int cr = 0;
	int i = 0;

	debug("[DSU] call write output handle = 0x%0x value = 0x%0x \n", handle, (value & mask));

	for (i = 0; i < 6; i++) {
		if (TEST_BIT(mask, i)) {
			if (TEST_BIT(value, i)) {
				SET_BIT(_dio_output_register, i + 8);
			} else {
				CLEAR_BIT(_dio_output_register, i + 8);
			}
		}
	}

	// write output resgister
	if ((cr = write_register(handle, EXT_IO_OUTPUT_REGISTER,
			_dio_output_register)) < 0) {
		message = strerror(errno);
		info("[DSU] echec write output handle = 0x%0x : %s \n", handle, message);
		return -1;
	}

	debug("[DSU] write output handle = 0x%0x value = 0x%0x \n", handle, _dio_output_register);

	return 0;
}

u16 dio_read_counter(int handle, u16 id) {
	int cr = 0;
	int count = TIMEOUT / PERIOD;
	u16 value = 0;
	u16 address = EXT_IO_COUNTER_REGISTER + ((id + 6) * 2);

	debug("[DSU] call read counter handle = 0x%0x : id = %d \n", handle , id);
	while (count-- > 0) {

		// read counter resgister
		if ((cr = read_register(handle, address, &value)) < 0) {
			char* message = strerror(errno);
			info("[DSU] echec read counter handle = 0x%0x : %s \n", handle, message);
			return _dio_counter_register;
		}

		value  = (((value & 0xff) << 8) + ((value >> 8) & 0xff));

		if (_dio_counter_register != value) {
			_dio_counter_register = value;
			break;
		}

		usleep(PERIOD);
	}

	debug("[DSU] read counter handle = 0x%0x value = 0x%0x \n", handle, _dio_counter_register);
	return _dio_counter_register;
}

int adc_open(const char* name) {
	int handle = -1;

	info("[DSU] open nomad adc \n");

	if ((handle = open(name, O_RDWR)) < 0) {
		goto error;
	}

	if (ioctl(handle, I2C_SLAVE, 0x21) < 0) {
		goto error;
	}

	info("[DSU] nomad adc  %s opened handle = 0x%0x \n", name, handle);
	return handle;

	error: if (handle != -1) {
		close(handle);
	}

	return (u16) -1;
}

void adc_close(int handle) {
	info("[DSU] close nomad adc handle = %0x%0x \n", handle);
	if (handle > 0) {
		close(handle);
	}
}

u16 adc_read_input(int handle, u16 id) {
	char buffer[3];
	u16 config = 0;
	u16 value = 0;
	char * message;

	info("[DSU] call read adc handle = 0x%0x \n", handle);

	// select correct channel in configuration register
	config = (((1 << (id + 14)) << 4) | 0x08);
	buffer[0] = 2;
	buffer[1] = (u8) (config >> 8);
	buffer[2] = (u8) (config) & 0xFF;
	if (write(handle, buffer, 3) != 3) {
		message = strerror(errno);
		info("[DSU] echec select correct channel in configuration register handle = 0x%0x : %s \n", handle, message);
		goto error;
	}

	// set register on converter register & active read of corresponding channel (Mode 2)
	buffer[0] = 0x80 | ((id + 14) & 0x7) << 4;
	if (write(handle, buffer, 1) != 1) {
		message = strerror(errno);
		info("[DSU] echec set register on converter register & active read of corresponding channel (Mode 2) handle = 0x%0x : %s \n", handle, message);
		goto error;
	}

	// wait a while for convertion time : 10 ms
	usleep(10000);

	// read converted value
	if (read(handle, buffer, 2) != 2) {
		message = strerror(errno);
		info("[DSU] echec read converted value handle = 0x%0x : %s \n", handle, message);
		goto error;
	}

	// value is 12bits coded
	value = (buffer[1] | (buffer[0] << 8)) & 0x0FFF;

	info("[DSU] read adc handle = 0x%0x value = 0x%0x \n", handle, value);

	error: return value;
}

