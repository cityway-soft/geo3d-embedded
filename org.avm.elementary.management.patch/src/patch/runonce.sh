#!/bin/sh
WORK_DIR=`dirname $(readlink -f $0)`


cat /root/avm/bin/avm.properties | grep org.avm.region
if [ "$?" != "0" ];then
	echo org.avm.region=CEN >> /root/avm/bin/avm.properties
	echo org.avm.country=FR >> /root/avm/bin/avm.properties
	echo org.avm.branch=VT >> /root/avm/bin/avm.properties
fi


#wget ftp://avm:avm++@ftpserver.avm.org/download/sendxmpp-0.0.8-2.fc10.noarch.rpm -O /root/sendxmpp-0.0.8-2.fc10.noarch.rpm

mkdir /root/testtar
cd /root/testtar
tar xzvf $WORK_DIR/test.tar.gz
cp /root/testtar/nice2.png /root/avm/
