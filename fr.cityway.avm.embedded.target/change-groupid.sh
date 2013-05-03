#!/bin/bash
GROUPID=$1
if [ -z $GROUPID ];then
	echo "Usage `basename $0` <groupid>"
	exit 1
fi

echo  GROUPID=$GROUPID
CURRENTDIR=`pwd`
for i in org*; 
do
	cd $CURRENTDIR
	cd $i	
	sed -ibak "s#<groupId>.*</groupId>#<groupId>$GROUPID</groupId>#g" pom.xml
	echo $i/pom.xml
done
