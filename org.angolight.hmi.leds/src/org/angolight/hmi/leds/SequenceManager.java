package org.angolight.hmi.leds;

public interface SequenceManager {

	public int initializeSequence();

	public byte getSequenceAddress(String name);

}
