/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.34
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.avm.device.fm6000.screen.jni;

class COMVS_NIGHTMODEJNI {

	static {
		try {
			System.loadLibrary("comvs_screen");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load. \n" + e);
		}
	}

	public final static native int COMVS_TRUE_get();

	public final static native int COMVS_FALSE_get();

	public final static native short Comvs_SetScreenMode(short jarg1, int jarg2);
}
