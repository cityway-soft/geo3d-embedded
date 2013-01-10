<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: comvs_system - Win32 (WCE ARMV4I) Release--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP68F2.tmp" with contents
[
/nologo /W3 /Oxt /I "C:\Program Files\Java\jdk1.6.0_04\include\win32" /I "C:\Program Files\Java\jdk1.6.0_04\include" /I "C:\opt\comvs\include" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "NDEBUG" /D "comvs_system_EXPORTS" /FR"ARMV4IRel/" /Fo"ARMV4IRel/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_system\comvs_system.c"
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_system\comvs_system_wrap.c"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSP68F2.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP6903.tmp" with contents
[
commctrl.lib coredll.lib corelibc.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:no /pdb:"ARMV4IRel/comvs_system.pdb" /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /out:"ARMV4IRel/comvs_system.dll" /implib:"ARMV4IRel/comvs_system.lib" /libpath:"C:\opt\comvs\lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IRel\comvs_system.obj
.\ARMV4IRel\comvs_system_wrap.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSP6903.tmp"
<h3>Output Window</h3>
Compiling...
comvs_system.c
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_system\comvs_system.c(55) : warning C4244: '=' : conversion from '__int64 ' to 'unsigned long ', possible loss of data
comvs_system_wrap.c
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_system\comvs_system_wrap.c(206) : warning C4013: 'exec' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_system\comvs_system_wrap.c(222) : warning C4013: 'kill' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_system\comvs_system_wrap.c(236) : warning C4013: 'settime' undefined; assuming extern returning int
Generating Code...
Linking...
   Creating library ARMV4IRel/comvs_system.lib and object ARMV4IRel/comvs_system.exp
Creating command line "bscmake.exe /nologo /o"ARMV4IRel/comvs_system.bsc"  .\ARMV4IRel\comvs_system.sbr .\ARMV4IRel\comvs_system_wrap.sbr"
Creating browse info file...
<h3>Output Window</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP6E80.bat" with contents
[
@echo off
xcopy ARMV4IRel\comvs_system.dll ..\..\lib /Y
]
Creating command line "C:\Users\dsu\AppData\Local\Temp\RSP6E80.bat"

ARMV4IRel\comvs_system.dll
1 fichier(s) copi‚(s)




<h3>Results</h3>
comvs_system.dll - 0 error(s), 4 warning(s)
</pre>
</body>
</html>
