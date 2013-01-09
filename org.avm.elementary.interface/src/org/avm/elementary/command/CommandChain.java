package org.avm.elementary.command;

public interface CommandChain {
	public boolean execute(CommandChainContext context) throws Exception;
}
