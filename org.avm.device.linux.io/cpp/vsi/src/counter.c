#include "vsi.h"

struct vsi_counter_data {
	int value;
	struct vsi_device *device;
};

int vsi_counter_open(struct inode *inode, struct file *filp) {
	struct vsi_device *device;
	struct vsi_counter_data *data;

	device = container_of(inode->i_cdev, struct vsi_device, cdev);
	data = kmalloc(sizeof(struct vsi_counter_data), GFP_KERNEL);
	data->value = 0;
	data->device = device;
	filp->private_data = data;

	return 0;
}

int vsi_counter_release(struct inode *inode, struct file *filp) {
	struct vsi_device *device;

	device = container_of(inode->i_cdev, struct vsi_device, cdev);
	kfree(filp->private_data);
	return 0;
}

ssize_t vsi_counter_read(struct file *filp, char __user *buf, size_t count, loff_t *f_pos) {
	char buffer[2];
	struct vsi_device *device;
	struct vsi_counter_data *data;

	data = filp->private_data;
	device = data->device;

	if(count < 2 ) {
		return 0;
	}

	pr_info("vsi: read device vsi%d previous = %d , value = %d \n", MINOR(device->cdev.dev), data->value, atomic_read(&device->value));
	wait_event_interruptible(device->queue, (atomic_read(&device->value) != data->value));
	data->value = atomic_read( &device->value );
	buffer[0] = data->value & 0xff;
	buffer[1] = (data->value >> 8) & 0xff;

	if (copy_to_user(buf, buffer, 2))
	return -EFAULT;

	return 2;
}

ssize_t vsi_counter_write(struct file *filp, const char __user *buf, size_t count, loff_t *f_pos)
{
	return 0;
}

