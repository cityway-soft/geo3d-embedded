#include "vsi.h"

# define VSI_CPU_OFF 0x01
# define VSI_GPS_OFF 0x02
# define VSI_GSM_OFF 0x10

extern struct vsi_dev vsi_dev;

int vsi_reset_open(struct inode *inode, struct file *filp) {
	struct vsi_device *device;

	device = container_of(inode->i_cdev, struct vsi_device, cdev);
	filp->private_data = device;
	return 0;
}

int vsi_reset_release(struct inode *inode, struct file *filp) {
	return 0;
}

ssize_t vsi_reset_read(struct file *filp, char __user *buf, size_t count, loff_t *f_pos)
{
	return 0;
}

ssize_t vsi_reset_write(struct file *filp, const char __user *buf, size_t count, loff_t *f_pos)
{
	char buffer[2];
	struct vsi_device *device;
	int value, out_reg;
	device = filp->private_data;

	if(count < 2 ) {
		return 0;
	}

	if(copy_from_user(buffer, buf, 2))
	return -EFAULT;

	value = buffer[0];
	pr_info("vsi: reset 0x%x \n", value);
	/* reset cpu */
	if(value & VSI_CPU_OFF) {
		outb(VSI_CPU_OFF, VSI_OFF_REG);
	}
	
	/* reset gps */
	if(value & VSI_GPS_OFF) {
		outb(VSI_GPS_OFF, VSI_GPS_REG);
	}
	
	/* reset gsm */
	if(value & VSI_GSM_OFF) {
			out_reg = atomic_read(&vsi_dev.devices[1].value) & 0xff;
			pr_info("vsi: reset out_reg 0x%x \n", out_reg);
			SET(out_reg, 4);
			CLEAR(out_reg, 5);
			pr_info("vsi: reset out_reg 0x%x \n", out_reg);
			outb(out_reg, VSI_OUT_REG);
			mdelay(100);
			CLEAR(out_reg, 4);
			pr_info("vsi: reset out_reg 0x%x \n", out_reg);
			outb(out_reg, VSI_OUT_REG);
	}

	return 2;
}
