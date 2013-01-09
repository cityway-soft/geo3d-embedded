<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: rs485 - Win32 (WCE ARMV4I) Debug--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP132E.tmp" with contents
[
/nologo /W3 /Zi /Od /I "C:\opt\comvs\include" /I "C:\opt\ive-2.2\runtimes\wm2003\arm\ppro10\bin\include" /D "DEBUG" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "rs485_EXPORTS" /FR"ARMV4IDbg/" /Fo"ARMV4IDbg/" /Fd"ARMV4IDbg/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.connection.rs485\cpp\rs485\rs485.cpp"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSP132E.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP133F.tmp" with contents
[
commctrl.lib coredll.lib jclppro10_.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:yes /pdb:"ARMV4IDbg/rs485.pdb" /debug /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /def:".\rs485.def" /out:"ARMV4IDbg/rs485.dll" /implib:"ARMV4IDbg/rs485.lib" /libpath:"C:\opt\ive-2.2\runtimes\wm2003\arm\ppro10\bin\lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IDbg\rs485.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSP133F.tmp"
<h3>Output Window</h3>
Compiling...
rs485.cpp
Linking...
   Creating library ARMV4IDbg/rs485.lib and object ARMV4IDbg/rs485.exp
Creating command line "bscmake.exe /nologo /o"ARMV4IDbg/rs485.bsc"  .\ARMV4IDbg\rs485.sbr"
Creating browse info file...
<h3>Output Window</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP1765.bat" with contents
[
@echo off
xcopy ARMV4IDbg\rs485.dll ..\..\lib /Y
]
Creating command line "C:\Users\dsu\AppData\Local\Temp\RSP1765.bat"

ARMV4IDbg\rs485.dll
1 fichier(s) copi‚(s)




<h3>Results</h3>
rs485.dll - 0 error(s), 0 warning(s)
</pre>
</body>
</html>
