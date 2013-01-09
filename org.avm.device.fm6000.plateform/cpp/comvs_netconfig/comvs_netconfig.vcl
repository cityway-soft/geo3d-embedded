<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: comvs_netconfig - Win32 (WCE ARMV4I) Release--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP5897.tmp" with contents
[
/nologo /W3 /Oxt /I "C:\Program Files\Java\jdk1.6.0_04\include\win32" /I "C:\Program Files\Java\jdk1.6.0_04\include" /I "C:\opt\comvs\include" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "NDEBUG" /D "comvs_netconfig_EXPORTS" /Fo"ARMV4IRel/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_netconfig\comvs_netconfig.c"
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_netconfig\JNIHelper.c"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSP5897.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP58A8.tmp" with contents
[
commctrl.lib coredll.lib lib_netconfig.lib LIB_Log.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:no /pdb:"ARMV4IRel/comvs_netconfig.pdb" /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /def:".\jni.def" /out:"ARMV4IRel/comvs_netconfig.dll" /implib:"ARMV4IRel/comvs_netconfig.lib" /libpath:"C:\opt\comvs\lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IRel\comvs_netconfig.obj
.\ARMV4IRel\JNIHelper.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSP58A8.tmp"
<h3>Output Window</h3>
Compiling...
comvs_netconfig.c
JNIHelper.c
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_netconfig\jnihelper.c(137) : warning C4018: '<=' : signed/unsigned mismatch
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_netconfig\jnihelper.c(175) : warning C4018: '>' : signed/unsigned mismatch
Generating Code...
Linking...
   Creating library ARMV4IRel/comvs_netconfig.lib and object ARMV4IRel/comvs_netconfig.exp
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP5C41.bat" with contents
[
@echo off
xcopy ARMV4IRel\comvs_netconfig.dll ..\..\lib /Y
]
Creating command line "C:\Users\dsu\AppData\Local\Temp\RSP5C41.bat"

ARMV4IRel\comvs_netconfig.dll
1 fichier(s) copi‚(s)




<h3>Results</h3>
comvs_netconfig.dll - 0 error(s), 2 warning(s)
</pre>
</body>
</html>
