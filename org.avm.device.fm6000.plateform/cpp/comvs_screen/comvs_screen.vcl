<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: comvs_screen - Win32 (WCE ARMV4I) Release--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP5400.tmp" with contents
[
/nologo /W3 /Oxt /I "C:\Program Files\Java\jdk1.6.0_04\include\win32" /I "C:\Program Files\Java\jdk1.6.0_04\include" /I "C:\opt\comvs\include" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "NDEBUG" /D "comvs_screen_EXPORTS" /Fo"ARMV4IRel/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.plateform\cpp\comvs_screen\comvs_screen_wrap.c"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSP5400.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP5401.tmp" with contents
[
commctrl.lib coredll.lib LIB_NightMode.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:no /pdb:"ARMV4IRel/comvs_screen.pdb" /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /out:"ARMV4IRel/comvs_screen.dll" /implib:"ARMV4IRel/comvs_screen.lib" /libpath:"C:\opt\comvs\lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IRel\comvs_screen_wrap.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSP5401.tmp"
<h3>Output Window</h3>
Compiling...
comvs_screen_wrap.c
Linking...
   Creating library ARMV4IRel/comvs_screen.lib and object ARMV4IRel/comvs_screen.exp
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP56B0.bat" with contents
[
@echo off
xcopy ARMV4IRel\comvs_screen.dll ..\..\lib /Y
]
Creating command line "C:\Users\dsu\AppData\Local\Temp\RSP56B0.bat"

ARMV4IRel\comvs_screen.dll
1 fichier(s) copi‚(s)




<h3>Results</h3>
comvs_screen.dll - 0 error(s), 0 warning(s)
</pre>
</body>
</html>
