%module COMVS_IO
%include "arrays_java.i";
%{
#include <windows.h>
#include <COMVS_IOSDriver.h>
%}

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("comvs_io");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}

JAVA_ARRAYSOFCLASSES(COMVS_IO_PIN)
typedef long HANDLE;
typedef unsigned long DWORD;
#include <COMVS_IOSDriver.h>

HANDLE open(const char* name);
void close(HANDLE handle);
DWORD read(HANDLE handle, COMVS_IO_PIN data[], DWORD size);
DWORD write(HANDLE handle, COMVS_IO_PIN data[], DWORD size);