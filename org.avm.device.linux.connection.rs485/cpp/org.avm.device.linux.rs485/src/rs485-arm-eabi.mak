CROSSTOOLS =  /opt/toolchains/armeabi-gcc-4.3.2-glibc-2.8
CLASSES    = org.avm.device.connection.rs485.Connection.class
OBJS       = rs485.o
MAIN_CLASS = org.avm.device.connection.rs485.Connection
NATIVE_LIB = librs485.so
DIST_DIR = ../../../dist
GCC_PREFIX = arm-none-linux-gnueabi
CC = $(CROSSTOOLS)/bin/arm-none-linux-gnueabi-gcc
JAVA_HOME = /home/dsuru/affaires/plateform/automotive/j9
CPPFLAGS  += -g  

all: librs485.so

librs485.so: rs485.o
	$(CC) $(CPPFLAGS) -shared -o librs485.so $^ -L$(JAVA_HOME)/bin -ljclfoun11_23
	cp $(NATIVE_LIB) $(DIST_DIR)

rs485.o: rs485.cpp rs485.h
	$(CC) $(CPPFLAGS) -c  -I$(JAVA_HOME)/bin/include  rs485.c 

clean: 
	rm -f *.o 
	rm -f *.so 

