//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// The Original Code is State Machine Compiler(SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 - 2003 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s):
//
// statemap.java --
//
// This package defines the FSMContext class which must be inherited by
// any Java class wanting to use an smc-generated state machine.
//
// RCS ID
// $Id: StateUndefinedException.java,v 1.2 2010/08/23 10:50:13 geolia Exp $
//
// Change Log
// $Log: StateUndefinedException.java,v $
// Revision 1.2  2010/08/23 10:50:13  geolia
// *** empty log message ***
//
// Revision 1.1  2010/08/18 12:35:57  geolia
// *** empty log message ***
//
// Revision 1.1  2010/08/18 10:54:29  geolia
// *** empty log message ***
//
// Revision 1.1  2010/08/13 12:10:57  geolia
// *** empty log message ***
//
// Revision 1.2  2008/06/18 13:04:19  cvs
// *** empty log message ***
//
// Revision 1.1  2008/03/21 11:29:49  cvs
// *** empty log message ***
//
// Revision 1.2 2008/02/22 12:59:27 cvs
// *** empty log message ***
//
// Revision 1.1 2008/02/21 14:28:20 cvs
// *** empty log message ***
//
// Revision 1.4 2005/05/28 18:44:13 cwrapp
// Updated C++, Java and Tcl libraries, added CSharp, Python and VB.
//
// Revision 1.0 2003/12/14 20:39:59 charlesr
// Initial revision
//

package statemap;

/**
 * A <code>StateUndefinedException</code> is thrown by an SMC-generated state
 * machine whenever a transition is taken and there is no state currently set.
 * This occurs when a transition is issued from with a transition action.
 */
public final class StateUndefinedException extends RuntimeException {
	/**
	 * Default constructor.
	 */
	public StateUndefinedException() {
		super();
	}

	/**
	 * Constructs a <code>StateUndefinedException</code> with a detail
	 * message.
	 * 
	 * @param reason
	 *            the detail message.
	 */
	public StateUndefinedException(String reason) {
		super(reason);
	}
}
