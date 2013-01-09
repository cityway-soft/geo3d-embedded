%module COMVS_MP3

%pragma(java) jniclasscode=%{
  static {
    try {
        System.loadLibrary("comvs_mp3");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }
%}

%{
#include <windows.h>
%}

typedef unsigned long HANDLE;
void initialize();
void dispose();
HANDLE open(const char *name);
void play(HANDLE handle);
void pause(HANDLE handle);
void resume(HANDLE handle);
void stop(HANDLE handle);
void close(HANDLE handle);




