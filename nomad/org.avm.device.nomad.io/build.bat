@echo on
set IVEHOME=C:\opt\ive-2.2\runtimes\win32\x86\foundation10
set JAVA_HOME=%IVEHOME%
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASS=org.avm.device.fm6000.io.COMVS_IO
set OUTPUT_FILE=cpp\comvs_io\comvs_io.h 

javah.exe -verbose -force -classpath bin -o %OUTPUT_FILE% %CLASS%