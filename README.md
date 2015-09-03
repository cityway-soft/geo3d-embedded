# Geo3D Embedded

## Description
	
Geo3D is an AVMS (Automatic Vehicle Monitoring System) developped by Cityway. Geo3d system has 3 parts:

* Geo3D embedded which runs inside embedded system installed in buses or coaches (and whose code is proposed in this github repository)
* Geo3d server
* Geo3d backoffice 

Geo3d backoffice has also 3...dimensions (3D) :
* maintenance : to manage Geo3d embedded application and system (upgrades, status,...)
* realtime : to get realtime information about vehicles (localisation, duty, journey, driver,...)
* differed time : to analyse the history and what happened in the past

Currently only Geo3D Embedded is proposed for opensource.

Geo3D embedded is an [OSGi](http://www.osgi.org/Technology/HowOSGi) application based on Eclipse Equinox. 
In summary, an OSGi application is made of "Bundles". A Bundle is a jar file (with specific MANIFEST entries) that can either provide services or contain data. 

## Quick start

In order to run Geo3D Embedded, you need 
* to install Java (1.8 or higher) 
* to have an Eclipse environment (Luna or higher) with Plugins Development Environment (PDE)
* ...to clone  this repo.

Then, you have to
* import Geo3d project into an Eclipse workspace
* configure Java (Window->Preferences->Java->Installed JREs->your java installation) and "Add External JARs" with \<your.local.gitrepo.dir\>/fr.cityway.avm.embedded.target/swt-lib/linux/x86_64/swt.jar (you have to select swt.jar that match your platform)
* edit from Eclipse  "\<your.local.gitrepo.dir\>/fr.cityway.avm.embedded.target/fr.cityway.avm.embedded.luna.target  and apply "Set as Target Platform"
* create a new launcher : "Run->Run configuration...", then double-clic on "OSGi Framework", and........Run

When application is launched, you are able to log with admin permissions (user id=0000, password=0000) or as a "simple" driver (user id=0001, password=0001) .
A database for test is provided ; duty 1, 2,...,9 can be used. 

## External librairies used

* JIBX : http://jibx.sourceforge.net/
* FLAVOR : http://flavor.sourceforge.net/
* SMC : http://smc.sourceforge.net/

## Licence

AGPL


