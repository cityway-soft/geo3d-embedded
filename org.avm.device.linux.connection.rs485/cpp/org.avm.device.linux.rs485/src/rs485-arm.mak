#
# %W% %E%
#
# Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
#
# See also the LICENSE file in this distribution.
#
# Makefile for the example demonstrating shared dispatchers with JNI.
#

CLASSES    = org.avm.device.connection.rs485.Connection.class
OBJS       = rs485.o
MAIN_CLASS = org.avm.device.connection.rs485.Connection
NATIVE_LIB = librs485.so
#CROSSTOOLS = /opt/toolchains/gcc-4.3.2-glibc-2.7
CROSSTOOLS = /opt/arm-linux-cross-compil-3.4.4/arm
#CC = $(CROSSTOOLS)/bin/arm-vfp-linux-gnu-gcc
CC = $(CROSSTOOLS)/bin/arm-linux-gcc
IVE_HOME = /opt/ive/runtimes/zaurus/arm
JAVA_HOME = /opt/ive
CPPFLAGS  += -g  

all: librs485.so

librs485.so: rs485.o
	$(CC) $(CPPFLAGS) -shared -o librs485.so $^ -L$(IVE_HOME)/ppro10/bin -ljclppro10_22

rs485.o: rs485.cpp rs485.h
	$(CC) $(CPPFLAGS) -c  -I$(JAVA_HOME)/bin/include  rs485.c 

clean: 
	rm -f *.o 
	rm -f *.so 

