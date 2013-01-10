%module COMVS_AC97
%include "typemaps.i"

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("comvs_ac97");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}

%{
#include <windows.h>
#include <COMVS_AC97.h>
%}
%apply unsigned char *OUTPUT {unsigned char *};
#include <COMVS_AC97.h>

