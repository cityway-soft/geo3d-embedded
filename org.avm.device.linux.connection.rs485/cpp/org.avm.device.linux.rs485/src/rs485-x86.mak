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
CC = g++

#include ../Makefile

all: librs485.so

librs485.so: rs485.o
	$(CC) -shared -o librs485.so $^ -L /opt/j9/bin/ -ljclfoun10_22

rs485.o: rs485.cpp rs485.h
	$(CC) -c -g -I/opt/ive/runtimes/linux/x86/foundation10/bin/include rs485.cpp 

clean: 
	rm -f *.o 
	rm -f *.so 

