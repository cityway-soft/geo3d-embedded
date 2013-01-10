MODULE = nomad_io
CROSSTOOLS = /opt/arm-linux-cross-compil-3.4.4/arm
OBJS       = $(MODULE).o $(MODULE)_wrap.o
NATIVE_LIB = libnomad_io.so
CC = $(CROSSTOOLS)/bin/arm-linux-gcc
IVE_HOME = /opt/ive/runtimes/zaurus/arm
JAVA_HOME = /opt/ive
DIST_DIR = ../../lib

CPPFLAGS  += -c -g -O2 -Wall  \
			-I/usr/include/include \
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

