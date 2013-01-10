#include <linux/module.h>
#include <linux/init.h>
#include "vsi.h"
// lecture counter : dd if=/dev/vsi3 bs=2 count=10 | od  - -x -w2
// reset echo -n -e "\0000\0001" > dev/vsi2
# define DELAY 10

// Controle (y compris l'acces au convertisseur CS5509)
//#define	SCLK		0x01			// clock du convertisseur
//#define	nCS			0x02			// CS du convertisseur
#define	CONV		0x04			// CONV du convertisseur
#define	CAL			0x08			// CAL du convertisseur
#define	RES1		0x10			// non utilise
#define	RES2		0x20			// non utilise
#define	RSTPPS		0x40			// reset du PPS (front montant)
#define	RSTODO		0x80			// reset du convertisseur (front descendant)



int major= VSI_MAJOR;
int base= VSI_BASE;

module_param(base, int, 1);
module_param(major, int, 0);

MODULE_AUTHOR("DSU");
MODULE_DESCRIPTION("Vehicule Specific Interface");
MODULE_SUPPORTED_DEVICE("PCcar V3");

struct vsi_dev vsi_dev;

struct file_operations vsi_din_fops = {.owner = THIS_MODULE,
	.read = vsi_din_read, .write = vsi_din_write, .open = vsi_din_open,
	.release = vsi_din_release };

struct file_operations vsi_dout_fops = {.owner = THIS_MODULE,
.read = vsi_dout_read, .write = vsi_dout_write, .open = vsi_dout_open,
.release = vsi_dout_release };

struct file_operations vsi_reset_fops = {.owner = THIS_MODULE,
.read = vsi_reset_read, .write = vsi_reset_write,
.open = vsi_reset_open, .release = vsi_reset_release };

struct file_operations vsi_counter_fops = {.owner = THIS_MODULE,
.read = vsi_counter_read, .write = vsi_counter_write,
.open = vsi_counter_open, .release = vsi_counter_release };

struct file_operations *vsi_fops_array[] = { &vsi_din_fops, &vsi_dout_fops,
		&vsi_reset_fops, &vsi_counter_fops };

void vsi_timer_fn(unsigned long ptr) {
	struct vsi_dev *dev = (struct vsi_dev*) ptr;
	struct vsi_device *device;
	int odo_reg;
	int previous, value;
	int previous_odo_value, odo_value, counter_value;
	int delta;


	if (atomic_read(&dev->started)) {

		/* din */
		device = &dev->devices[0];
		previous = atomic_read(&device->value);
		//pr_info("vsi: timer input previous = 0x%x \n", previous);

		value = 0;
		value |= (inb(VSI_IN_REG) & 0xff); /* bits 0-7 digital input */
		value |= ((inb(VSI_STATUS_REG) & 0x24) << 8); /* bits 10.13 brake ignition */
		value |= ((inb(VSI_WU_REG) & 0x40) << 8); /* bits 14 ext wakeup*/
		odo_reg = inb(VSI_ODO_REG) ;
		// pr_info("vsi: timer odometer = 0x%x \n", odo_reg);
		value |= ((odo_reg & 0x80) << 8); /* bits 15 reverse */
		//pr_info("vsi: timer input value = 0x%x \n", value);

		if (previous != value) {
			atomic_set(&device->value, value);
			pr_info("vsi: timer input notify process \n");
			wake_up_interruptible(&device->queue);
		}

		/* counter */
		device = &dev->devices[3];
		previous = atomic_read(&device->value);
		//pr_info("vsi: timer counter previous = 0x%x \n", previous);

		counter_value = previous & 0xffff;
		previous_odo_value = (previous & 0x00ff0000) >> 16;
		odo_value = (odo_reg & 0x7f);
		delta = odo_value - previous_odo_value;
		if (delta < 0) {
			counter_value += (128 - previous_odo_value + odo_value);
		} else {
			counter_value += delta;
		}
		value = (odo_value << 16) + (counter_value & 0xffff );
		//pr_info("vsi: timer counter value = 0x%x \n", value);

		if (previous != value) {
			atomic_set(&device->value, value);
			pr_info("vsi: timer counter notify process \n");
			wake_up_interruptible(&device->queue);
		}

		/* timer */
		dev->timer.expires = jiffies + DELAY;
		add_timer(&dev->timer);

	} else {
		pr_info("vsi: timer stopped\n");
	}
}

static void __exit vsi_cleanup_module(void)
{
	int i;
	dev_t devno = MKDEV(major, 0);

	atomic_set(&vsi_dev.started,0);
	set_current_state(TASK_INTERRUPTIBLE);
	schedule_timeout(DELAY *3);

	for (i = 0; i < VSI_NR_DEVS; i++) {
		pr_info("vsi: unregister device vsi%d \n", i);
		cdev_del(&vsi_dev.devices[i].cdev);
	}
	unregister_chrdev_region(devno, VSI_NR_DEVS);

	pr_info("vsi: free the ISA region base address 0x%x \n", base);
	release_region(base, 8);

}

static void vsi_setup_cdev(struct vsi_device *device, int index) {
	int err, devno = MKDEV(major, index);

	pr_info("vsi: register device vsi%d \n", index);

	cdev_init(&device->cdev, vsi_fops_array[index]);
	device->cdev.owner = THIS_MODULE;
	device->cdev.ops = vsi_fops_array[index];

	init_waitqueue_head(&device->queue);
	atomic_set(&device->value, 0);

	err = cdev_add(&device->cdev, devno, 1);
	if (err) {
		pr_info("vsi: error %d register vsi%d \n", err, index);
	}

	pr_info("vsi: device vsi%d registered = %d \n", index,
			MINOR(device->cdev.dev));
}

static void vsi_init_hardware(void) {
	int wu_reg = inb(VSI_WU_REG);
	SET(wu_reg, 2);
	outb(wu_reg, VSI_WU_REG);

	outb(0x80, VSI_CTRL_REG);
	outb(0x00, VSI_CTRL_REG);
}

static int __init vsi_init_module(void)
{
	int result;
	int i;
	dev_t dev = MKDEV(major, 0);

	/* Register your major, and accept a dynamic number. */
	if (major) {
		result = register_chrdev_region(dev, VSI_NR_DEVS, "vsi");
	} else {
		result = alloc_chrdev_region(&dev, 0, VSI_NR_DEVS, "vsi");
		major = MAJOR(dev);
	}

	pr_info("vsi: major number %d \n", major);
	if (result < 0)
	goto exit;

	/* Initialize each device. */
	for (i = 0; i < VSI_NR_DEVS; i++) {
		vsi_setup_cdev(&vsi_dev.devices[i], i);
	}

	/* Reserve the ISA region */
	pr_info("vsi: reserve the ISA region base address 0x%x \n", base);
	if (!request_region(base, 8, "vsi")) {
		result = -EBUSY;
		pr_info("vsi: echec reserve the ISA region base address 0x%x \n", base);
		goto exit;
	}
	pr_info("vsi: ISA region base address 0x%x reserved \n", base);

	vsi_init_hardware();

	pr_info("vsi: initialize timer \n");
	atomic_set(&vsi_dev.started,1);
	init_timer(&vsi_dev.timer);
	vsi_dev.timer.function = vsi_timer_fn;
	vsi_dev.timer.expires = jiffies + DELAY;
	vsi_dev.timer.data = (unsigned long) &vsi_dev;
	add_timer(&vsi_dev.timer);

	return 0; /* succeed */

	exit:
	return result;
}

module_init(vsi_init_module);
module_exit(vsi_cleanup_module);
