/*
 * Copyright 2003-2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// WARNING This class MUST not have references to the Logger or
// WARNING RootLogger classes in its static initiliazation neither 
// WARNING directly nor indirectly.
// Contributors:
//                Luke Blanshard <luke@quiq.com>
//                Mario Schomburg - IBM Global Services/Germany
//                Anders Kristensen
//                Igor Poteryaev
package org.apache.log4j;

import java.util.Hashtable;

/**
 * This class is specialized in retrieving Loggers by name and also maintaining
 * the Logger hierarchy.
 * 
 * <p>
 * <em>The casual user should not have to deal with this class
 directly.</em>
 * In fact, up until version 0.9.0, this class had default package access.
 * 
 * <p>
 * The structure of the Logger hierarchy is maintained by the
 * {@link #getInstance} method. The hierarchy is such that children link to
 * their parent but parents do not have any pointers to their children.
 * Moreover, Loggers can be instantiated in any order, in particular descendant
 * before ancestor.
 * 
 * <p>
 * In case a descendant is created before a particular ancestor, then it creates
 * a provision node for the ancestor and adds itself to the provision node.
 * Other descendants of the same ancestor add themselves to the previously
 * created provision node.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author <a href="mailto:ralph.curtis@gabrielsoftware.com">Ralph Curtis</a>
 * 
 */
public class Hierarchy {

	Hashtable ht;
	Logger root;

	int enableInt;
	Priority enable;

	boolean emittedNoAppenderWarning = false;
	boolean emittedNoResourceBundleWarning = false;

	/**
	 * Create a new Logger hierarchy.
	 * 
	 * @param root
	 *            The root of the new hierarchy.
	 * 
	 */
	public Hierarchy(Logger root) {
		ht = new Hashtable();
		this.root = root;
		// Enable all priority levels by default.
		enable(Priority.ALL);
		this.root.setHierarchy(this);
	}

	/**
	 * Enable all logging priorities for this hierarchy. This is the default.
	 * 
	 */
	public void enableAll() {
		enable(Priority.ALL);
	}

	public void enable(Priority p) {
		if (p != null) {
			enableInt = p.level;
			enable = p;
		}
	}

	/**
	 * Returns the string representation of the internal <code>enable</code>
	 * state.
	 * 
	 * @since 1.2
	 */
	public Priority getEnable() {
		return enable;
	}

	/**
	 * Return a new Logger instance named as specified.
	 * 
	 * <p>
	 * If a logger of that name already exists, then it will be returned.
	 * Otherwise, a new Logger will be instantiated and linked with its existing
	 * ancestors as well as children.
	 * </p>
	 * 
	 * @param name
	 *            The name of the Logger to retrieve.
	 * @return a new or existing logger with the specified name.
	 */
	public Logger getInstance(String name) {
		// System.out.println("getInstance("+name+") called.");
		// Synchronize to prevent write conflicts. Read conflicts (in
		// getChainedPriority method) are possible only if variable
		// assignments are non-atomic.
		Logger logger;

		synchronized (ht) {
			Object o = ht.get(name);
			if (o == null) {
				logger = new Logger(name);
				logger.setHierarchy(this);
				ht.put(name, logger);
				updateParents(logger);
				return logger;
			} else if (o instanceof Logger) {
				return (Logger) o;
			} else if (o instanceof ProvisionNode) {
				logger = new Logger(name);
				logger.setHierarchy(this);
				ht.put(name, logger);
				updateChildren((ProvisionNode) o, logger);
				updateParents(logger);
				return logger;
			} else {
				// It should be impossible to arrive here
				return null; // but let's keep the compiler happy.
			}
		}
	}

	/**
	 * Get the root of this hierarchy.
	 * 
	 * @since 0.9.0
	 */
	public Logger getRoot() {
		return root;
	}

	/**
	 * This method loops through all the *potential* parents of 'cat'. There 3
	 * possible cases:
	 * 
	 * 1) No entry for the potential parent of 'cat' exists
	 * 
	 * We create a ProvisionNode for this potential parent and insert 'cat' in
	 * that provision node.
	 * 
	 * 2) There entry is of type Logger for the potential parent.
	 * 
	 * The entry is 'cat's nearest existing parent. We update cat's parent field
	 * with this entry. We also break from the loop because updating our
	 * parent's parent is our parent's responsibility.
	 * 
	 * 3) There entry is of type ProvisionNode for this potential parent.
	 * 
	 * We add 'cat' to the list of children for this potential parent.
	 */
	final private void updateParents(Logger cat) {
		String name = cat.name;
		int length = name.length();
		boolean parentFound = false;

		// System.out.println("UpdateParents called for " + name);

		// if name = "w.x.y.z", loop thourgh "w.x.y", "w.x" and "w", but not
		// "w.x.y.z"
		for (int i = name.lastIndexOf('.', length - 1); i >= 0; i = name
				.lastIndexOf('.', i - 1)) {
			String substr = name.substring(0, i);

			// System.out.println("Updating parent : " + substr);
			Object o = ht.get(substr);
			// Create a provision node for a future parent.
			if (o == null) {
				// System.out.println("No parent "+substr+" found. Creating
				// ProvisionNode.");
				ProvisionNode pn = new ProvisionNode(cat);
				ht.put(substr, pn);
			} else if (o instanceof Logger) {
				parentFound = true;
				cat.parent = (Logger) o;
				// System.out.println("Linking " + cat.name + " -> " + ((Logger)
				// o).name);
				break; // no need to update the ancestors of the closest
				// ancestor
			} else if (o instanceof ProvisionNode) {
				((ProvisionNode) o).addElement(cat);
			} else {
				Exception e = new IllegalStateException(
						"unexpected object type " + o.getClass() + " in ht.");
				e.printStackTrace();
			}
		}
		// If we could not find any existing parents, then link with root.
		if (!parentFound)
			cat.parent = root;
	}

	/**
	 * We update the links for all the children that placed themselves in the
	 * provision node 'pn'. The second argument 'cat' is a reference for the
	 * newly created Logger, parent of all the children in 'pn'
	 * 
	 * We loop on all the children 'c' in 'pn':
	 * 
	 * If the child 'c' has been already linked to a child of 'cat' then there
	 * is no need to update 'c'.
	 * 
	 * Otherwise, we set cat's parent field to c's parent and set c's parent
	 * field to cat.
	 * 
	 */
	final private void updateChildren(ProvisionNode pn, Logger cat) {
		// System.out.println("updateChildren called for " + cat.name);
		final int last = pn.size();

		for (int i = 0; i < last; i++) {
			Logger c = (Logger) pn.elementAt(i);
			// System.out.println("Updating child " +p.name);

			// Unless this child already points to a correct (lower) parent,
			// make cat.parent point to c.parent and c.parent to cat.
			if (!c.parent.name.startsWith(cat.name)) {
				cat.parent = c.parent;
				c.parent = cat;
			}
		}
	}

	/**
	 * Clear all entries from the internal hash table.
	 * 
	 */
	public void clear() {
		ht.clear();
	}

}
