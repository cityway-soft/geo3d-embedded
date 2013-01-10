MODULE = watchdog
CROSSTOOLS = /opt/arm-linux-cross-compil-3.4.4/arm
OBJS = $(MODULE).o 
CC = $(CROSSTOOLS)/bin/arm-linux-gcc
DIST_DIR = ../../lib

CPPFLAGS  += -g -O2 -Wall \
			-I/usr/include/include 

$(MODULE):	$(OBJS)
	$(CC) $(CPPFLAGS) -o $(MODULE) $(OBJS) $(LIBS)

all:	$(MODULE)

clean:
	rm -f $(OBJS) $(MODULE)

