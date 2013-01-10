#!/bin/bash


#export ECLIPSE_HOME=<Eclipse Installation>
# SOURCE must contains /plugin or /features
#export SOURCE=<Bundles directory>
#export DESTINATION=<P2 export directory>


# --- edit here! ----#
export ECLIPSE_HOME=/home/akunin/Development/Eclipse
export SOURCE=/home/akunin/Downloads/source
export DESTINATION=/home/akunin/Downloads/
# --- edit here! ----#


# --- start ----#
launcher=`find $ECLIPSE_HOME/plugins/ |grep org.eclipse.equinox.launcher_|head -n 1`

proc="java -jar $launcher\
	-application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher\
	-metadataRepository file:$DESTINATION/fr.cityway.avm.embedded.target\
	-artifactRepository file:$DESTINATION/fr.cityway.avm.embedded.target\
	-source $SOURCE\
	-configs gtk.linux.x86 gtk.linux.x86_64\
	-compress\
	-publishArtifacts"

$proc
# --- end ----#