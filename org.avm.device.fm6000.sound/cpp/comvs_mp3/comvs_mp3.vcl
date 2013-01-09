<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: comvs_mp3 - Win32 (WCE ARMV4I) Release--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP2C64.tmp" with contents
[
/nologo /W3 /Oxt /I "C:\Program Files\Java\jdk1.6.0_04\include\win32" /I "C:\Program Files\Java\jdk1.6.0_04\include" /I "C:\opt\comvs\include" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "NDEBUG" /D "comvs_mp3_EXPORTS" /FR"ARMV4IRel/" /Fo"ARMV4IRel/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3.cpp"
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSP2C64.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP2C74.tmp" with contents
[
commctrl.lib coredll.lib ole32.lib strmiids.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:no /pdb:"ARMV4IRel/comvs_mp3.pdb" /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /out:"ARMV4IRel/comvs_mp3.dll" /implib:"ARMV4IRel/comvs_mp3.lib" /libpath:"C:\opt\comvs\lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IRel\comvs_mp3.obj
.\ARMV4IRel\comvs_mp3_wrap.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSP2C74.tmp"
<h3>Output Window</h3>
Compiling...
comvs_mp3.cpp
Generating Code...
Compiling...
comvs_mp3_wrap.c
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c(191) : warning C4013: 'initialize' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c(198) : warning C4013: 'dispose' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c(214) : warning C4013: 'open' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c(227) : warning C4013: 'play' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c(237) : warning C4013: 'pause' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c(247) : warning C4013: 'resume' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c(257) : warning C4013: 'stop' undefined; assuming extern returning int
c:\users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_mp3\comvs_mp3_wrap.c(267) : warning C4013: 'close' undefined; assuming extern returning int
Generating Code...
Linking...
   Creating library ARMV4IRel/comvs_mp3.lib and object ARMV4IRel/comvs_mp3.exp
Creating command line "bscmake.exe /nologo /o"ARMV4IRel/comvs_mp3.bsc"  .\ARMV4IRel\comvs_mp3.sbr .\ARMV4IRel\comvs_mp3_wrap.sbr"
Creating browse info file...
<h3>Output Window</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP34EE.bat" with contents
[
@echo off
xcopy ARMV4IRel\comvs_mp3.dll ..\..\lib /Y
]
Creating command line "C:\Users\dsu\AppData\Local\Temp\RSP34EE.bat"

ARMV4IRel\comvs_mp3.dll
1 fichier(s) copi‚(s)




<h3>Results</h3>
comvs_mp3.dll - 0 error(s), 8 warning(s)
</pre>
</body>
</html>
