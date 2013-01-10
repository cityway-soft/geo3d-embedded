%module WIRMA_IO
%{
#include "wirma_io.h"
%}

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("wirma_io");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}




#include  wirma_io.h


