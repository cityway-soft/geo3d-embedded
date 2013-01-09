package org.avm.device.player;

public interface Player {

	public void open(String name);

	public void play();

	public void pause();

	public void resume();

	public void stop();

	public void close();

}
