<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: comvs_io - Win32 (WCE ARMV4I) Release--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP9241.tmp" with contents
[
/nologo /W3 /Oxt /I "C:\Program Files\Java\jdk1.6.0_04\include\win32" /I "C:\Program Files\Java\jdk1.6.0_04\include" /I "C:\opt\comvs\include" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "NDEBUG" /D "comvs_io_EXPORTS" /FR"ARMV4IRel/" /Fo"ARMV4IRel/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.io\cpp\comvs_io\comvs_io_wrap.c"
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.io\cpp\comvs_io\comvs_io.c"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSP9241.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP9252.tmp" with contents
[
commctrl.lib coredll.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:no /pdb:"ARMV4IRel/comvs_io.pdb" /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /out:"ARMV4IRel/comvs_io.dll" /implib:"ARMV4IRel/comvs_io.lib" /libpath:"C:\opt\comvs\lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IRel\comvs_io_wrap.obj
.\ARMV4IRel\comvs_io.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSP9252.tmp"
<h3>Output Window</h3>
Compiling...
comvs_io_wrap.c
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.io\cpp\comvs_io\comvs_io_wrap.c(1686) : warning C4013: 'open' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.io\cpp\comvs_io\comvs_io_wrap.c(1699) : warning C4013: 'close' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.io\cpp\comvs_io\comvs_io_wrap.c(1740) : warning C4013: 'read' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.io\cpp\comvs_io\comvs_io_wrap.c(1791) : warning C4013: 'write' undefined; assuming extern returning int
comvs_io.c
Generating Code...
Linking...
   Creating library ARMV4IRel/comvs_io.lib and object ARMV4IRel/comvs_io.exp
Creating command line "bscmake.exe /nologo /o"ARMV4IRel/comvs_io.bsc"  .\ARMV4IRel\comvs_io_wrap.sbr .\ARMV4IRel\comvs_io.sbr"
Creating browse info file...
<h3>Output Window</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP988A.bat" with contents
[
@echo off
xcopy ARMV4IRel\comvs_io.dll ..\..\lib /Y
]
Creating command line "C:\Users\dsu\AppData\Local\Temp\RSP988A.bat"

ARMV4IRel\comvs_io.dll
1 fichier(s) copi‚(s)




<h3>Results</h3>
comvs_io.dll - 0 error(s), 4 warning(s)
</pre>
</body>
</html>
