#include "vsi.h"

int vsi_dout_open(struct inode *inode, struct file *filp) {
	struct vsi_device *device;

	device = container_of(inode->i_cdev, struct vsi_device, cdev);
	filp->private_data = device;
	return 0;
}

int vsi_dout_release(struct inode *inode, struct file *filp) {
	return 0;
}

ssize_t vsi_dout_read(struct file *filp, char __user *buf, size_t count, loff_t *f_pos)
{
	char buffer[2];
	struct vsi_device *device;
	int value;

	device = filp->private_data;

	if(count < 2 ) {
		return 0;
	}

	value = atomic_read( &device->value );
	buffer[0] = value & 0xff;
	buffer[1] = (value >> 8) & 0xff;

	if (copy_to_user(buf, buffer, 2))
	return -EFAULT;

	return 2;
}

ssize_t vsi_dout_write(struct file *filp, const char __user *buf, size_t count, loff_t *f_pos)
{
	char buffer[2];
	struct vsi_device *device;
	int value;
	device = filp->private_data;

	if(count < 2 ) {
		return 0;
	}

	if(copy_from_user(buffer, buf, 2))
	return -EFAULT;

	value = buffer[0];
	pr_info("vsi: write output 0x%x \n", value);
	outb(value, VSI_OUT_REG);
	atomic_set(&device->value, value);

	return 2;
}
