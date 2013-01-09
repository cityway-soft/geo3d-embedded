/**
 * 
 */
package org.avm.hmi.mmi.application.actions;

import java.util.Hashtable;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

/**
 * @author lbr Dans un tableau a deux dimensions on stocke les actions
 *         correspondant a l'appuie sur une touche du clavier (colonne) dans un
 *         context donne (ligne).
 * 
 */
public class processFactory {
	private Hashtable _actions;
	private PooledExecutor _pool;
	// Singleton
	private static processFactory INSTANCE = null;

	private processFactory() {
		_actions = new Hashtable();
		_pool = new PooledExecutor(5);
	}

	public synchronized static processFactory createInstance() {
		if (INSTANCE == null)
			INSTANCE = new processFactory();
		return INSTANCE;
	}

	public void addProcess(String action, String state, ProcessRunnable process) {
		// System.out.println(">>>\taddProcess("+action+","+state+")");
		action = verifSize(action);
		state = verifSize(state);
		Hashtable etats = (Hashtable) _actions.get(action);
		if (etats == null)
			etats = new Hashtable();
		// Verif il ne devrait pas y avoir d'etat pour cette action
		if (etats.containsKey(state))
			System.out.println(">>>\tun process existe deja [" + action + ","
					+ state + "]");
		etats.put(state, process);
		_actions.put(action, etats);
	}

	private String verifSize(String arg) {
		if (arg.length() >= 32) {
			// System.out.println(">>>\t"+arg+" de Taille >= 32 !");
			arg = arg.substring(0, 32);
		}
		return arg;
	}

	public ProcessRunnable getProcess(String action, String state) {
		// System.out.println(">>>\tgetProcess("+action+","+state+")");
		Hashtable etats = (Hashtable) _actions.get(action);
		if (etats == null) {
			System.out.println(">>>\tPas d'etat pour [" + action + "]");
			return null;
		}
		ProcessRunnable proc = (ProcessRunnable) etats.get(state);
		if (proc == null) {
			System.out.println(">>>\tPas de process pour [" + state + "]");
			proc = (ProcessRunnable) etats.get("ALL_STATES");
			System.out.println(">>>\tUtilisation du process pour [ALL_STATES]");
		}
		return proc;
	}

	public void setArgs(String action, String state, String[] args) {
		action = verifSize(action);
		state = verifSize(state);

		System.out.println(">>>\tsetArgs(" + action + "," + state + ")");
		ProcessRunnable proc = getProcess(action, state);
		if (proc != null) {
			proc.init(args);
		}
	}

	public void launchProcess(String action, String state) {
		action = verifSize(action);
		state = verifSize(state);
		System.out.println(">>>\tlaunchProcess(" + action + "," + state + ")");
		ProcessRunnable proc = getProcess(action, state);
		if (proc != null) {
			try {
				_pool.execute(proc);
				// new Thread(proc).start();
				System.out.println(">>>\tOK");
			} catch (Throwable t) {
				System.out.println("!!!!\tNOK");
				t.printStackTrace(System.out);
				System.out.flush();
			}
		} else {
			try {
				_pool.execute(getProcess("null", "null"));
			} catch (InterruptedException e) {
			}
			// new Thread(getProcess("null", "null")).start();
			System.out.println("!!!!\tNOK");
		}
	}
}
