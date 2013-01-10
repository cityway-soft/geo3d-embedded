/**
 * 
 */
package org.avm.hmi.mmi.avm;

import org.avm.hmi.mmi.application.actions.ProcessRunnable;

/**
 * @author lbr
 * 
 */
public class OnValidationProgressBar implements ProcessRunnable {

	private AvmControler _controler;

	public OnValidationProgressBar(AvmControler controler) {
		_controler = controler;
	}

	public void init(String[] args) {
	}

	public void run() {
		_controler.synchronize();
	}

}
