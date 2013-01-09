MODULE = wirma2_io
CROSSTOOLS =  /opt/toolchains/armeabi-gcc-4.3.2-glibc-2.8
OBJS       = $(MODULE).o $(MODULE)_wrap.o
NATIVE_LIB = libwirma2_io.so
GCC_PREFIX = arm-none-linux-gnueabi
CC = $(CROSSTOOLS)/bin/arm-none-linux-gnueabi-gcc
IVE_HOME =  /opt/ive/runtimes/zaurus/arm
JAVA_HOME =  /opt/ive
DIST_DIR = ../../lib

CPPFLAGS  += -c -g -O2 -Wall \
			-I$(CROSSTOOLS)/arm-none-linux-gnueabi/libc/usr/include \
			-I$(JAVA_HOME)/bin/include 

all: $(NATIVE_LIB)

$(NATIVE_LIB): $(OBJS)
	$(CC) -shared -o $(NATIVE_LIB) $^ -L$(JAVA_HOME)/bin -L$(IVE_HOME)/ppro10/bin
	cp $(NATIVE_LIB) $(DIST_DIR)

$(MODULE).o: $(MODULE).c $(MODULE).h 
	$(CC) $(CPPFLAGS) $<
	
$(MODULE)_wrap.o: $(MODULE)_wrap.c  $(MODULE).h 
	$(CC) $(CPPFLAGS) $<
	

clean: 
	rm -f *.o 
	rm -f *.so 

