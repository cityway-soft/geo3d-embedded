#!/bin/sh
#set -x

MODULE="vsi"
DEVICE="vsi"
MODE="664"
RETVAL=0

#
# See how we were called.
#

start() {
	VERSION=`uname -r`
	insmod /lib/modules/$VERSION/kernel/drivers/$MODULE.ko 
	major=`awk  "\\$2==\"$MODULE\" {print \\$1}" /proc/devices`
	mknod /dev/${DEVICE}0 c $major 0
	mknod /dev/${DEVICE}1 c $major 1
	mknod /dev/${DEVICE}2 c $major 2
	mknod /dev/${DEVICE}3 c $major 3
	return $RETVAL
}

stop() {
	rm -f /dev/${DEVICE}[0-3]
	rmmod $MODULE
        return $RETVAL
}


restart() {
	stop
	start
}	


case "$1" in
start)
	start
	;;
stop)
	stop
	;;
restart)
	restart
	;;
*)
	echo $"Usage: $0 {start|stop|restart}"
	exit 1
esac

exit $RETVAL
