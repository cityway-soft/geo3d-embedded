#ifndef _NOMAD_IO_H
# define _NOMAD_IO_H

extern int dio_open(const char* name);
extern void dio_close(int handle);
extern int dio_read_input(int handle);
extern int dio_write_ouput(int handle, int value);


#endif
