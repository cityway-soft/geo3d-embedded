MODULE = vcan
CLASSPATH = ../../bin/
CLASS    = org/avm/device/protocol/vcan/Connection.class
CLASSNAME = org.avm.device.protocol.vcan.Connection
OBJS       = $(MODULE).o
NATIVE_LIB = libvcan.so
CC = gcc
JDK = /opt/java
JAVAH = $(JDK)/bin/javah
DIST_DIR = ../../dist
CPPFLAGS  += -c -g -O2 -Wall -Wno-parentheses \
			-I$(JDK)/include -I$(JDK)/include/linux -I/usr/src/linux/include \
			-fno-strict-aliasing -DPF_CAN=29 -DAF_CAN=PF_CAN

all: $(NATIVE_LIB)

$(NATIVE_LIB): $(OBJS)
	$(CC) -shared -o $(NATIVE_LIB) $^ -L $(JDK)/bin
	cp $(NATIVE_LIB) $(DIST_DIR)

$(MODULE).o: $(MODULE).c $(MODULE).h
	$(CC) $(CPPFLAGS) $<
	
$(MODULE).h: $(CLASSPATH)/$(CLASS)
	$(JAVAH)  -classpath $(CLASSPATH) -o $(MODULE).h -jni $(CLASSNAME) 

clean: 
	rm -f *.o 
	rm -f *.so 

