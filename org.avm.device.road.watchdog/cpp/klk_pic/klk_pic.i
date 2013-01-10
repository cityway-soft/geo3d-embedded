%module KLK_PIC
%{
#include "klk_pic.h"
%}

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("klk_pic");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}




#include  klk_pic.h


