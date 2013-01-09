%module COMVS_NIGHTMODE

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("comvs_screen");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}

%{
#include <windows.h>
#include <COMVS_NightMode.h>
%}

#include <COMVS_NightMode.h>


