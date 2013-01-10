#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <linux/watchdog.h>
#include <linux/klk-pic.h>
#include <linux/lpc32x0_gpio.h>
#include <termios.h>

typedef unsigned char         u8;
typedef unsigned short int	  u16;
typedef unsigned int	        u32;
typedef signed char			      s8;
typedef /*signed*/ short int	s16;
typedef /*signed*/ int		    s32;

#define _DEBUG

#define info(...)	fprintf(stderr, __VA_ARGS__)
#ifdef _DEBUG
#define debug(...)	fprintf(stderr, __VA_ARGS__)
#else
#define debug(...)
#endif


void toggle_on_off_gps() {
	int power = -1;
	int gps = -1;
	int rtc = -1;

	// open
	if ((gps = open("/dev/gpo_14", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((rtc = open("/dev/p1_15", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((power = open("/dev/p1_17", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	// initialize
	if (ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(power, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// toggle on/off gps
	if (ioctl(power, LPC32X0_GPIO_WRITE_HIGH_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 120ms
	usleep(120 * 1000);

	if (ioctl(power, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	debug("[DSU] toggle on/off gps\n");

	//close
	error: if (gps != -1) {
		close(gps);
		gps = -1;
	}
	if (rtc != -1) {
		close(rtc);
		rtc = -1;
	}
	if (power != -1) {
		close(power);
		power = -1;
	}
	return;
}

void initialize_gps() {
	int power = -1;
	int gps = -1;
	int rtc = -1;

	// open
	if ((gps = open("/dev/gpo_14", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((rtc = open("/dev/p1_15", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((power = open("/dev/p1_17", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	// initialize
	if (ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(power, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// initialize gps

	if (ioctl(gps, LPC32X0_GPIO_WRITE_HIGH_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_HIGH_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 10s
	usleep(10* 1000000 );

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 120ms
	usleep(120 * 1000 );

	if (ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 200ms
	usleep(200 * 1000 );

	debug("[DSU] initialize_gps\n");

	//close
	error:
	if (gps != -1) {
		close(gps);
		gps = -1;
	}
	if (rtc != -1) {
		close(rtc);
		rtc = -1;
	}
	if (power != -1) {
		close(power);
		power = -1;
	}
	return;
}

void reset_gps() {
	int power = -1;
	int gps = -1;
	int rtc = -1;

	// open
	if ((gps = open("/dev/gpo_14", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((rtc = open("/dev/p1_15", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if ((power = open("/dev/p1_17", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	// initialize
	if (ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(power, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	if (ioctl(rtc, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) < 0) {
		goto error;
	}

	// reset gps
	if (ioctl(gps, LPC32X0_GPIO_WRITE_HIGH_LEVEL, NULL) < 0) {
		goto error;
	}

	// wait 3s
	usleep(3* 1000000 );

	if(ioctl(gps, LPC32X0_GPIO_WRITE_LOW_LEVEL, NULL) <0) {
		goto error;
	}

	// wait 200ms
	usleep(200*1000);

	debug("[DSU] reset_gps \n");

	//close
	error:
	if (gps != -1) {
		close(gps);
		gps = -1;
	}
	if (rtc != -1) {
		close(rtc);
		rtc = -1;
	}
	if (power != -1) {
		close(power);
		power = -1;
	}
	return;
}

void wakeup_gps() {
	klk_pic_state_t state;
	int handle = -1;

	if ((handle = open("/dev/klk-pic", O_RDWR | O_NONBLOCK)) < 0) {
		goto error;
	}

	if (ioctl(handle, KLK_PIC_GET_WAKE_STATE, &state) < 0) {
		goto error;
	}
	if ((state & KLK_PIC_STATE_FIRST_WAKEUP) == KLK_PIC_STATE_FIRST_WAKEUP) {
		initialize_gps();
	} else {
		toggle_on_off_gps();
	}

	debug("[DSU] wakeup_gps ok\n");

	error: if (handle != -1) {
		close(handle);
		handle = -1;
	}
	return;
}

static int
kbs_gps_read( int in_pv_hd_gps_in, u8* in_pu8_src, u32* in_pu32_length)
{
  int t_res = 0;
  s32 s32_nb_read = 0;

  // VOS_TRACE_DEBUG("Reading %d bytes", *in_pu32_length);

  if( -1 != (s32)in_pv_hd_gps_in)
  {
    s32_nb_read = read( in_pv_hd_gps_in, in_pu8_src, *in_pu32_length);

   }

  // VOS_TRACE_DEBUG("%d bytes read", s32_nb_read);
  //printf("%x\n", * in_pu8_src);

  if((u32)s32_nb_read != *in_pu32_length)
  {
    t_res = 1;
  }
  else
  {
    printf("%c", * in_pu8_src);

    }


ENDP:

  return(t_res);
}





static void
_kbs_gps_init_at_power_on_sirf(void)
{
  struct termios  termios_p;
  int _pv_hd_gps_in   = -1;
  u32 u32_nb = 1;
  u8 u8_buf=0;

  if ( (_pv_hd_gps_in = open("/dev/ttyTX0",O_RDWR|O_NOCTTY/*|O_NDELAY*/)) == -1)
  {
      info("Can't open GPS serial port\n");
      goto ENDP;
  }
   info("open ok\n");

  // Read current port configuration
  if(tcgetattr(_pv_hd_gps_in,&termios_p))
  {
    info("Can't get termios attributes\n");
    goto ENDP;
  }

  // always work at 4800b
  switch(cfgetospeed(&termios_p))
  {

    case B4800:
      // knetd was stopped while in operational state and ARM was NOT rebooted
      //VOS_TRACE_INFO("GPS serial link already configured at 4800 baud : performing configuration step at that speed");
    break;

    default:
//      VOS_TRACE_WARNING("set GPS speed to 4800");
      cfsetispeed(&termios_p, B4800);
      cfsetospeed(&termios_p, B4800);
    break;
  }


  termios_p.c_cflag |= (CLOCAL | CREAD);
	termios_p.c_cflag &= ~PARENB;
	termios_p.c_cflag &= ~CSTOPB;
	termios_p.c_cflag &= ~CSIZE;
	termios_p.c_cflag |= CS8;

  // Configure serial port to speak with uBlox
  termios_p.c_iflag &= ~(IXON | IXOFF |IXANY);
  termios_p.c_iflag &= ~(ICRNL |INLCR | IGNCR);
  termios_p.c_iflag |= (IGNBRK | IGNPAR);
  // No Output processing
  termios_p.c_oflag = 0;
  // Not used
  termios_p.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);

  // We assume that we always get some characters to read
  // if no character available for 10 seconds it means
  // that communication with GPS is KO
  termios_p.c_cc[VMIN] = 0;   //
//  termios_p.c_cc[VTIME]= 100;  // 10s
  termios_p.c_cc[VTIME]= 10;  // 1s
  if(tcsetattr(_pv_hd_gps_in,TCSANOW,&termios_p))
  {
    info("Can't set Serial link\n");
    goto ENDP;
  }

  // try to know if GPS is already started or not
  if(1 == kbs_gps_read(_pv_hd_gps_in, &u8_buf, &u32_nb))
  {
    // SIRF is OFF !!!!
    info("[SIRF] Off state detected\n");

    // start start it
    toggle_on_off_gps();
     u32_nb = 1;
    if(1 == kbs_gps_read(_pv_hd_gps_in, &u8_buf, &u32_nb))
    {
      info("[SIRF] cannot recover gps, reset\n");
      reset_gps();
    u32_nb = 1;
      if(1 == kbs_gps_read(_pv_hd_gps_in, &u8_buf, &u32_nb))
      {
        info("[SIRF] cannot recover gps, reset first init!\n");
        initialize_gps();
      }
    }
    else
    {
      info("GPS ok\n");

    }
  }
  else
  {
    info("GPS ok\n");

  }

  while (1)
  {
    u32_nb = 1;
    kbs_gps_read(_pv_hd_gps_in, &u8_buf, &u32_nb);
  }


ENDP:
  return;
}

int main(int argc, char *argv[]) {
	int opt;

	while ((opt = getopt(argc, argv, "nriht")) != -1) {
		switch (opt) {
		case 'h':
			fprintf(stderr, "Usage: %s [-n] [-r] [-i] [-h] [-t] \n", argv[0]);
			break;
		case 'r':
			reset_gps();
			break;
		case 'i':
			initialize_gps();
			break;
		case 'n':
			wakeup_gps();
			break;
	  case 't':
	    wakeup_gps();
      _kbs_gps_init_at_power_on_sirf();
			break;
		}
	}

	return EXIT_SUCCESS;
}
