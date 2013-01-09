/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.40
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.avm.device.road.io.jni;

class ROAD_IOJNI {

  static {
    try {
        System.loadLibrary("road_io");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
    }
  }

  public final static native int dio_open(String jarg1);
  public final static native void dio_close(int jarg1);
  public final static native int dio_read_input(int jarg1);
  public final static native int dio_write_ouput(int jarg1, int jarg2, int jarg3);
  public final static native int dio_read_counter(int jarg1, int jarg2);
}
