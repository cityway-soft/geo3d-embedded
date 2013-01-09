<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: MuxConnector - Win32 (WCE ARMV4I) Debug--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSPA30B.tmp" with contents
[
/nologo /W3 /Zi /Od /I "src" /I "include" /I "C:\opt\ive-2.2\runtimes\wm2003\arm\ppro10\bin\include" /I "C:\opt\comvs\include" /D "_DEBUG" /D "DEBUG" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "MUXCONNECTOR_EXPORTS" /FR"ARMV4IDbg/" /Fp"ARMV4IDbg/MuxConnector.pch" /YX /Fo"ARMV4IDbg/" /Fd"ARMV4IDbg/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\org.avm.device.connection.mux\cpp\src\connection.c"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSPA30B.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSPA30C.tmp" with contents
[
commctrl.lib coredll.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:yes /pdb:"ARMV4IDbg/MuxConnector.pdb" /debug /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /out:"ARMV4IDbg/MuxConnector.dll" /implib:"ARMV4IDbg/MuxConnector.lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IDbg\connection.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSPA30C.tmp"
<h3>Output Window</h3>
Compiling...
connection.c
Linking...
   Creating library ARMV4IDbg/MuxConnector.lib and object ARMV4IDbg/MuxConnector.exp
Creating command line "bscmake.exe /nologo /o"ARMV4IDbg/MuxConnector.bsc"  .\ARMV4IDbg\connection.sbr"
Creating browse info file...
<h3>Output Window</h3>




<h3>Results</h3>
MuxConnector.dll - 0 error(s), 0 warning(s)
</pre>
</body>
</html>
