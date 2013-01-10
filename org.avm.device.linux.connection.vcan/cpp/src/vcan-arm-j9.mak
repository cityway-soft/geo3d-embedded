MODULE = vcan
CROSSTOOLS =  /opt/toolchains/armeabi-gcc-4.3.2-glibc-2.8
CLASSPATH = ../../bin/
CLASS    = org/avm/device/protocol/vcan/Connection.class
CLASSNAME = org.avm.device.protocol.vcan.Connection
OBJS       = $(MODULE).o
NATIVE_LIB = libvcan.so
GCC_PREFIX = arm-none-linux-gnueabi
CC = $(CROSSTOOLS)/bin/arm-none-linux-gnueabi-gcc
JAVA_HOME =  /root/affaires/road/j9
JAVAH = /opt/ive/bin/javah
DIST_DIR = ../../dist
CPPFLAGS  += -c -g -O2 -Wall -Wno-parentheses \
			-I$(JAVA_HOME)/bin/include -I$(CROSSTOOLS)/arm-none-linux-gnueabi/libc/usr/include \
			-fno-strict-aliasing -DPF_CAN=29 -DAF_CAN=PF_CAN

all: $(NATIVE_LIB)

$(NATIVE_LIB): $(OBJS)
	$(CC) -shared -o $(NATIVE_LIB) $^ -L $(JAVA_HOME)/bin -ljclfoun11_23
	cp $(NATIVE_LIB) $(DIST_DIR)

$(MODULE).o: $(MODULE).c $(MODULE).h
	$(CC) $(CPPFLAGS) $<
	
$(MODULE).h: $(CLASSPATH)/$(CLASS)
	$(JAVAH)  -classpath $(CLASSPATH) -o $(MODULE).h -jni $(CLASSNAME) 

clean: 
	rm -f *.o 
	rm -f *.so 

