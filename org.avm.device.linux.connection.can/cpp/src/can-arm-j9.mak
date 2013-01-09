MODULE = can
CROSSTOOLS = /opt/arm-linux-cross-compil-3.4.4/arm
#CAN4LINUX = /opt/can4linux/can4linux-3.5.4/
CLASSPATH = ../../bin/
CLASS    = org/avm/device/protocol/can/Connection.class
CLASSNAME = org.avm.device.protocol.can.Connection
OBJS       = $(MODULE).o
NATIVE_LIB = libcan.so
CC = $(CROSSTOOLS)/bin/arm-linux-gcc
IVE_HOME =  /opt/ive/runtimes/zaurus/arm
JAVA_HOME =  /opt/ive
JAVAH = /opt/ive/bin/javah
DIST_DIR = ../../dist

ifeq ($(DEBUG),yes)
	CPPFLAGS  = -c -g -O2 -Wall -D_DEBUG 
else
	CPPFLAGS  = -c -O2 -Wall 
endif

test: main.o
	$(CC) -o $@ $^ $(LDFLAGS) -L$(JAVA_HOME)/bin -L$(IVE_HOME)/ppro10/bin


main.o: main.c $(MODULE).h
	$(CC) -o $@ -c $< $(CPPFLAGS)
 
		 
all:  $(NATIVE_LIB)

$(NATIVE_LIB): $(OBJS)
	$(CC) -shared -o $(NATIVE_LIB) $^ -L$(JAVA_HOME)/bin -L$(IVE_HOME)/ppro10/bin
	cp $(NATIVE_LIB) $(DIST_DIR)

$(MODULE).o: $(MODULE).c $(MODULE).h
	$(CC) $(CPPFLAGS) $< -I$(CROSSTOOLS)/sysroot/usr/include -I$(JAVA_HOME)/bin/include
	
$(MODULE).h: $(CLASSPATH)/$(CLASS)
	$(JAVAH)  -classpath $(CLASSPATH) -o $(MODULE).h -jni $(CLASSNAME) 


clean: 
	rm -f *.o 
	rm -f *.so 
	rm -f test 

