<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: comvs_wifi - Win32 (WCE ARMV4I) Release--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\root\AppData\Local\Temp\RSP23BA.tmp" with contents
[
/nologo /W3 /Oxt /I "C:\Program Files\Java\jdk1.6.0_12\include" /I "C:\Program Files\Java\jdk1.6.0_12\include\win32" /I "C:\opt\comvs\include" /D _WIN32_WCE=400 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=400 /D "UNICODE" /D "_UNICODE" /D "NDEBUG" /D "comvs_wifi_EXPORTS" /Fo"ARMV4IRel/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\root\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.wifi\cpp\comvs_wifi\comvs_wifi_wrap.c"
]
Creating command line "clarm.exe @C:\Users\root\AppData\Local\Temp\RSP23BA.tmp" 
Creating temporary file "C:\Users\root\AppData\Local\Temp\RSP23CA.tmp" with contents
[
commctrl.lib coredll.lib LIB_WifiDevice.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:no /pdb:"ARMV4IRel/comvs_wifi.pdb" /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /out:"ARMV4IRel/comvs_wifi.dll" /implib:"ARMV4IRel/comvs_wifi.lib" /libpath:"C:\opt\comvs\lib" /subsystem:windowsce,4.00 /MACHINE:THUMB 
.\ARMV4IRel\comvs_wifi_wrap.obj
]
Creating command line "link.exe @C:\Users\root\AppData\Local\Temp\RSP23CA.tmp"
<h3>Output Window</h3>
Compiling...
comvs_wifi_wrap.c
Linking...
   Creating library ARMV4IRel/comvs_wifi.lib and object ARMV4IRel/comvs_wifi.exp
Creating temporary file "C:\Users\root\AppData\Local\Temp\RSP2783.bat" with contents
[
@echo off
xcopy ARMV4IRel\comvs_wifi.dll ..\..\lib /Y
]
Creating command line "C:\Users\root\AppData\Local\Temp\RSP2783.bat"

ARMV4IRel\comvs_wifi.dll
1 fichier(s) copi‚(s)




<h3>Results</h3>
comvs_wifi.dll - 0 error(s), 0 warning(s)
</pre>
</body>
</html>
