#!/bin/bash
SETCOLOR_SUCCESS="echo -en \\033[1;32m"
SETCOLOR_FAILURE="echo -en \\033[1;31m"
SETCOLOR_WARNING="echo -en \\033[1;33m"
SETCOLOR_NORMAL="echo -en \\033[0;39m"
groupid="fr.cityway.avm.embedded"
repo="http://nexus.paris.cityway.fr:8081/nexus/service/local/repositories/releases/content/"
p2repo="http://dev.paris.cityway.fr/repo/fr.cityway.avm.embedded.target/tab"
outputdir=""
project=""
filename=""
version="LATEST"
bundlelist=""

function usage(){
	#mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:get -DartifactId=PCTC -DgroupId=fr.cityway.avm.simulator -Dversion=LATEST -DremoteRepositories=http://nexus.paris.cityway.fr:8081/nexus/service/local/repositories/releases/content/ -Ddest=./toto

	echo "Usage : `basename $0` -a <artifactid> -p <project> [-g <groupid>] [-v <version>] [-o <outputdir>]  [-r <repo>] [-f <bundle-list>]"
}

function getInfo(){
	jarfile=$1
	info=$2
	curentdir=`pwd`
	workdir=`mktemp -d "/tmp/modulesXXXX"` 
	cd $workdir
	jar xvf $jarfile META-INF/MANIFEST.MF > /dev/null 
	echo `cat META-INF/MANIFEST.MF|grep ^$info|sed "s/$info: //g"| tr -d '\r'`
	rm -rf $workdir
}

function download(){
	bundlename=$1
	outdir=$2
	if [ -z "$outdir" ];then
		outdir=$outputdir
	fi
	

	filename="${bundlename}.jar"

	echo -n "downloading $bundlename : "
	CMD="wget ${p2repo}/${filename} -O ${outdir}/${filename}"
	#echo $CMD
	$CMD > /dev/null 2>&1
	result=$?
	if [ "$result" != "0" ];then
		CMD="mvn -U org.apache.maven.plugins:maven-dependency-plugin:2.4:get -DartifactId=$bundlename -DgroupId=${groupid}.${project} -Dversion=$version -DremoteRepositories=$repo -Ddest=${outdir}/${filename}"
		#echo $CMD
		$CMD > /dev/null
	fi
	result=$?

	filesize=`stat -c %s ${outdir}/${filename}`
	if [ "$filesize" == "0" ];then
		rm ${outdir}/${filename}
		$SETCOLOR_FAILURE
		echo  "*failed : file empty*"
		$SETCOLOR_NORMAL
	elif [ "$result" == "0" ];then
		newfilename="${bundlename}_`getInfo ${outdir}/${filename} Bundle-Version`.jar"
		rm ${bundlename}_*.jar > /dev/null 2>&1
		mv ${outdir}/${filename} ${outdir}/${newfilename}
		jar tvf ${outdir}/${newfilename}|grep -v META|grep -v config>/dev/null
		if [ "$?" == "0" ];then
			$SETCOLOR_SUCCESS
			echo "OK"
			$SETCOLOR_NORMAL
		else
			$SETCOLOR_WARNING
			echo "*warning : jar not complete"
			$SETCOLOR_NORMAL
		fi
	else	
		$SETCOLOR_FAILURE
		echo "*failed : bundle not found*"
		$SETCOLOR_NORMAL
	fi
}

$SETCOLOR_NORMAL
while getopts "o:a:g:v:r:f:p:" optname
  do
    case "$optname" in
      "o")
        outputdir=$OPTARG
        ;;
      "a")
        artifactid=$OPTARG
        ;;
      "g")
        groupid=$OPTARG
        ;;
      "v")
        version=$OPTARG
        ;;
      "r")
        repo=$OPTARG
        ;;
      "f")
        bundlelist=$OPTARG
        ;;
      "p")
        project=$OPTARG
        ;;
       "?")
       	usage
	exit 0
        ;;
      ":")
        echo "No argument value for option $OPTARG"
        ;;
      *)
      # Should not occur
        echo "Unknown error while processing options"
        ;;
    esac
  done


if [ "$project" == "" ];then
	usage
	echo "Project must be specified (tab, cg38, ...)"
	exit 1
fi



if [ "$outputdir" == "" ];then
	outputdir="`pwd`/"
fi


#echo "artifactid=$artifactid"
#echo "groupid=$groupid"
#echo "version=$version"
#echo "repo=$repo"
#outputdirecho "outputdir=$outputdir"
#echo "filename=$filename"



if [ ! -z "$bundlelist" ]; then
	if [ -e "$bundlelist" ];then
		#outdir="repo-`date +%Y%m%d-%H%M%S`"
		#mkdir $outdir
		outdir=$ouputdir
		for line in $(cat $bundlelist); do
			bundlestartlevel=`echo $line |cut -d";" -f 1` 
			if [ $bundlestartlevel -gt 0 ];then
				bundleversion=`echo $line |cut -d";" -f 3` 
				bundlename=`echo $line |cut -d";" -f 2` 
				download $bundlename $outdir
			else
				$SETCOLOR_WARNING
				echo "$line ignore"
				$SETCOLOR_NORMAL
			fi
		done	
		zip -jr $outdir/BUNDLES.zip $outdir/*.jar
	fi

	
elif [ ! -z "$artifactid" ]; then
	download $artifactid $outputdir
else
	usage
	exit 1
fi



