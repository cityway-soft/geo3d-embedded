###########################################
# Makefile for HID API
###########################################



CC       ?= gcc
CFLAGS   ?= -Wall -g

CXX      ?= g++
CXXFLAGS ?= -Wall -g

COBJS     = z8.o
OBJS      = $(COBJS)
LIBS      = 
INCLUDES ?= -I/opt/java/include -I/opt/java/include/linux


all: z8.so

z8.so: $(OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) -shared -o z8.so $^ $(LIBS) -L /home/flabourot/dev/jvm/j9/bin/ -ljclfoun10_22

$(COBJS): %.o: %.c
	$(CC) $(CFLAGS) -c $(INCLUDES) $< -o $@


clean:
	rm -f $(OBJS) pdbmanager *~

.PHONY: clean
