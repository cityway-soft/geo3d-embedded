#!/bin/bash
filename=$1
   SETCOLOR_SUCCESS="echo -en \\033[1;32m"
    SETCOLOR_FAILURE="echo -en \\033[1;31m"
    SETCOLOR_WARNING="echo -en \\033[1;33m"
    SETCOLOR_NORMAL="echo -en \\033[0;39m"

if [ ! -z $filename ];then
	for i in `find . -name "*$filename" -print`; 
	do 
		$SETCOLOR_NORMAL
		echo -n "$i :"
		jar tvf $i|grep class > /dev/null && ($SETCOLOR_SUCCESS; echo "OK") || ($SETCOLOR_FAILURE; echo "failed"); 
	done
	$SETCOLOR_NORMAL
fi


