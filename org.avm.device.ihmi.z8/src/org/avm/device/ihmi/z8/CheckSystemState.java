package org.avm.device.ihmi.z8;

import java.util.ArrayList;
import java.util.List;

public class CheckSystemState extends Thread {

	private int delay;
	private boolean running = true;
	private int last = 0;
	private List listeners = new ArrayList();

	public CheckSystemState(int delay) {
		this.delay = delay;
		this.start();
	}

	public void stopIt() {
		running = false;
		this.notify();
	}

	public void run() {
		while (running) {
			int tmp = Z8Access.getSystemCurrentState();
			if (tmp != last) {
				notifyListeners(tmp);
			}
			last = tmp;
			try {
				synchronized (this) {
					this.wait(delay);
				}
			} catch (InterruptedException e) {

			}
		}
	}

	private void notifyListeners(int state) {
		for (int i=0; i < listeners.size(); ++i){
			SystemStateListener listener = (SystemStateListener)listeners.get(i);
			listener.onStateChange(state);
		}
	}
	
	public void addListener (SystemStateListener listener){
		listeners.add(listener);
	}
	
	public void removeListener (SystemStateListener listener){
		listeners.remove(listener);
	}
}
