#ifndef VSI_H_
#define VSI_H_

#include <linux/module.h>
#include <linux/moduleparam.h>
#include <linux/init.h>

#include <linux/time.h>
#include <linux/timer.h>
#include <linux/kernel.h>
#include <asm/io.h>
#include <asm/atomic.h>
#include <linux/ioport.h>
#include <linux/types.h>
#include <linux/cdev.h>
#include <linux/spinlock.h>
#include <linux/interrupt.h>
#include <linux/jiffies.h>
#include <linux/err.h>
#include <linux/fs.h> 
#include <linux/wait.h>
#include <asm/uaccess.h> 
#include <linux/delay.h>


#define SET(flag, bit) ((flag) |= (1 << (bit)))
#define CLEAR(flag, bit) ((flag) &= ~(1 << (bit)))  
#define GET(flag, bit) ((flag) & (1 << (bit))) 

#define VSI_MAJOR		0
#define VSI_NR_DEVS		4
#define VSI_BASE		0x300

#define VSI_CTRL_REG	VSI_BASE +0
#define VSI_STATUS_REG	VSI_BASE +0
#define VSI_WU_REG	    VSI_BASE +1
#define VSI_OUT_REG	    VSI_BASE +2
#define VSI_ODO_REG	    VSI_BASE +2
#define VSI_OFF_REG	    VSI_BASE +4
#define VSI_IN_REG	    VSI_BASE +4
#define VSI_GPS_REG	    VSI_BASE +5


struct vsi_device {
	atomic_t value;
	wait_queue_head_t queue;
	struct cdev cdev;
};

struct vsi_dev {
	atomic_t started;
	struct timer_list timer;
	struct vsi_device devices[VSI_NR_DEVS];
};

int vsi_din_open(struct inode *inode, struct file *filp);
int vsi_din_release(struct inode *inode, struct file *filp);
ssize_t vsi_din_read(struct file *filp, char __user *buf, size_t count, loff_t *f_pos);
ssize_t vsi_din_write(struct file *filp, const char __user *buf, size_t count, loff_t *f_pos);

int vsi_dout_open(struct inode *inode, struct file *filp);
int vsi_dout_release(struct inode *inode, struct file *filp);
ssize_t vsi_dout_read(struct file *filp, char __user *buf, size_t count, loff_t *f_pos);
ssize_t vsi_dout_write(struct file *filp, const char __user *buf, size_t count, loff_t *f_pos);

int vsi_reset_open(struct inode *inode, struct file *filp);
int vsi_reset_release(struct inode *inode, struct file *filp);
ssize_t vsi_reset_read(struct file *filp, char __user *buf, size_t count, loff_t *f_pos);
ssize_t vsi_reset_write(struct file *filp, const char __user *buf, size_t count, loff_t *f_pos);

int vsi_counter_open(struct inode *inode, struct file *filp);
int vsi_counter_release(struct inode *inode, struct file *filp);
ssize_t vsi_counter_read(struct file *filp, char __user *buf, size_t count, loff_t *f_pos);
ssize_t vsi_counter_write(struct file *filp, const char __user *buf, size_t count, loff_t *f_pos);

#endif /*VSI_H_*/
