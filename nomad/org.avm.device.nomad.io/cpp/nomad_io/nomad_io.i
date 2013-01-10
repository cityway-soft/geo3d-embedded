%module NOMAD_IO
%{
#include "nomad_io.h"
%}

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("nomad_io");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}


#include  "nomad_io.h"


