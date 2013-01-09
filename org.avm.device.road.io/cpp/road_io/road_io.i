%module ROAD_IO
%{
#include "road_io.h"
%}

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("road_io");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}


#include  "road_io.h"


