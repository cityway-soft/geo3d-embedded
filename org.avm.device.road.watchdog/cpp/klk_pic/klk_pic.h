#ifndef _KLK_PIC_H
# define _KLK_PIC_H

extern int klkpic_open(const char* name);
extern void klkpic_close(int handle);
extern int klkpic_read_apc(int handle);
extern int klkpic_read_wakeup(int handle);
extern void klkpic_keep_alive();

#endif
