#ifndef _ROAD_IO_H
# define _ROAD_IO_H

typedef unsigned char	u8;
typedef unsigned short int	u16;

int dio_open(const char* name);
void dio_close(int handle);
u16 dio_read_input(int handle);
u16 dio_write_ouput(int handle, u16 value, u16 mask);
u16 dio_read_counter(int handle, u16 id);

#endif
