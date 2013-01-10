package org.avm.elementary.command;

public interface CommandChainInjector {

	public void setCommandChain(CommandChain command);

	public void unsetCommandChain(CommandChain command);

}
