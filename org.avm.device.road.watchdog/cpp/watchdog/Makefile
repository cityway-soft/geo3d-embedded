MODULE = watchdog
CROSSTOOLS = /opt/toolchains/armeabi-gcc-4.3.2-glibc-2.8
OBJS = $(MODULE).o 
GCC_PREFIX = arm-none-linux-gnueabi
CC = $(CROSSTOOLS)/bin/arm-none-linux-gnueabi-gcc
DIST_DIR = ../../lib

CPPFLAGS  += -g -O2 -Wall \
			-I$(CROSSTOOLS)/arm-none-linux-gnueabi/libc/usr/include 

$(MODULE):	$(OBJS)
	$(CC) $(CPPFLAGS) -o $(MODULE) $(OBJS) $(LIBS)

all:	$(MODULE)

clean:
	rm -f $(OBJS) $(MODULE)

