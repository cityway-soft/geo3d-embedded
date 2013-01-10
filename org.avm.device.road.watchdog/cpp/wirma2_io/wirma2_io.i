%module WIRMA2_IO
%{
#include "wirma2_io.h"
%}

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("wirma2_io");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}




#include  wirma2_io.h


