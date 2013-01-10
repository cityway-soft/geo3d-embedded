/*
 * Created on 17 nov. 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.avm.elementary.media.jms;

/**
 * @author root
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface Mediator {

	public void openingCallback() throws Exception;

	public void closingCallback() throws Exception;;

	public void openedCallback() throws Exception;;

	public void closedCallback() throws Exception;;
}