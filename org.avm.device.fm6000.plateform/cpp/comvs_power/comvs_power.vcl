<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: comvs_power - Win32 (WCE ARMV4I) Release--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSPB553.tmp" with contents
[
/nologo /W3 /Oxt /I "C:\Program Files\Java\jdk1.6.0_04\include\win32" /I "C:\Program Files\Java\jdk1.6.0_04\include" /I "C:\opt\comvs\include" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "NDEBUG" /D "comvs_power_EXPORTS" /Fo"ARMV4IRel/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_power\comvs_power.c"
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_power\JNIHelper.c"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSPB553.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSPB573.tmp" with contents
[
commctrl.lib coredll.lib LIB_PowerManagement.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:no /pdb:"ARMV4IRel/comvs_power.pdb" /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /out:"ARMV4IRel/comvs_power.dll" /implib:"ARMV4IRel/comvs_power.lib" /libpath:"C:\opt\comvs\lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IRel\comvs_power.obj
.\ARMV4IRel\JNIHelper.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSPB573.tmp"
<h3>Output Window</h3>
Compiling...
comvs_power.c
JNIHelper.c
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_power\jnihelper.c(137) : warning C4018: '<=' : signed/unsigned mismatch
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_power\jnihelper.c(175) : warning C4018: '>' : signed/unsigned mismatch
Generating Code...
Linking...
   Creating library ARMV4IRel/comvs_power.lib and object ARMV4IRel/comvs_power.exp
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSPB92C.bat" with contents
[
@echo off
xcopy ARMV4IRel\comvs_power.dll ..\..\lib /Y
]
Creating command line "C:\Users\dsu\AppData\Local\Temp\RSPB92C.bat"

ARMV4IRel\comvs_power.dll
1 fichier(s) copi‚(s)




<h3>Results</h3>
comvs_power.dll - 0 error(s), 2 warning(s)
</pre>
</body>
</html>
