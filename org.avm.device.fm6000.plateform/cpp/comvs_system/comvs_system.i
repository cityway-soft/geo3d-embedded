%module COMVS_SYSTEM

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("comvs_system");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}

%{
#include <windows.h>
%}

 long exec(const char* name, const char* args);
 int kill(long handle);
 int settime(unsigned long date);


