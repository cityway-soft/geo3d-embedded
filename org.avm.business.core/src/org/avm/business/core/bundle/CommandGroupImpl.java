package org.avm.business.core.bundle;

import java.io.PrintWriter;
import java.io.Reader;
import java.util.Dictionary;
import java.util.StringTokenizer;

import org.avm.business.core.Avm;
import org.avm.business.core.AvmConfig;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.elementary.common.AbstractCommandGroup;
import org.knopflerfish.service.console.Session;
import org.osgi.service.component.ComponentContext;

public class CommandGroupImpl extends AbstractCommandGroup {
	public static final String COMMAND_GROUP = "avmcore";

	private Avm _peer;

	public CommandGroupImpl(ComponentContext context, Avm peer,
			ConfigImpl config) {
		super(context, config, COMMAND_GROUP,
				"Configuration commands for avmcore.");
		_peer = peer;
	}

	// Transitions
	public final static String FLAG_PARAM = "-p";

	public final static String USAGE_TRANSITION = "[-p #SA#] <transition>";

	public final static String[] HELP_TRANSITION = new String[] {
			"Activation des transitions",
			"\tListe des transitions:\n" + "\t\t- c[ourse] -p <idu course>\n"
					+ "\t\t- dep[art]\n" + "\t\t- dev[iation]\n"
					+ "\t\t- e[ntree] -p <id arret>\n" + "\t\t- finc[ourse]\n"
					+ "\t\t- fins[ervice]\n"
					+ "\t\t- p[oste] [-p <conducteur/vehicule>]\n"
					+ "\t\t- se[rvice] -p <idu service>\n"
					+ "\t\t- so[rtie] -p <id arret>\n" + "\t\t- su[ivant]\n" };

	public int cmdTransition(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String transition = ((String) opts.get("transition"));

		transition = transition.toLowerCase();
		int length = transition.length();
		try {

			if ("poste".startsWith(transition) && length >= 3) {// -- poste
				int c = 0, v = 0;
				String params = (String) opts.get(FLAG_PARAM);
				if (params != null) {
					StringTokenizer t = new StringTokenizer(params, "/");
					try {
						c = Integer.parseInt((String) t.nextToken());
						v = Integer.parseInt((String) t.nextToken());
					} catch (Throwable e) {
						out.println("Impossible de traiter le parametre : "
								+ params
								+ " - verifier le format <conducteur>/<vehicule>");
					}
				}
				_peer.prisePoste(v, c);
			} else if ("suivant".startsWith(transition) && length >= 1) {// --
																			// suivant
				if (_peer.getModel().isInsidePoint()) {
					Point dernier = _peer.getModel().getDernierPoint();
					if (dernier != null) {
						int id = dernier.getId();
						_peer.sortie(id);
					}
				} else {
					Point prochain = _peer.getModel().getProchainPoint();
					if (prochain != null) {
						int id = prochain.getId();
						_peer.entree(id);
					}
				}
				infoArret(out);
			} else if ("deviation".startsWith(transition) && length >= 3) { // --
																			// deviation
				_peer.sortieItineraire();
			} else if ("depart".startsWith(transition) && length >= 3) { // --
																			// depart
				_peer.depart();
			} else if ("fincourse".startsWith(transition) && length >= 4) {// --
																			// fincourse
				_peer.finCourse();
			} else if ("finservice".startsWith(transition) && length >= 4) {// --
																			// finservice
				_peer.finService();
			} else if ("entree".startsWith(transition) && length >= 1) {// --
																		// entree
				int id = Integer.valueOf((String) opts.get(FLAG_PARAM))
						.intValue();
				_peer.entree(id);
				infoArret(out);
			} else if ("sortie".startsWith(transition) && length >= 3) {// --
																		// sortie
				int id = Integer.valueOf((String) opts.get(FLAG_PARAM))
						.intValue();
				_peer.sortie(id);
				infoArret(out);
			} else if ("course".startsWith(transition) && length >= 1) {// -
																		// course
				String course = ((String) opts.get(FLAG_PARAM)).trim();
				_peer.priseCourse(Integer.parseInt(course));
				showCourse(out);
			} else if ("service".startsWith(transition) && length >= 2) { // --
																			// se
				String service = ((String) opts.get(FLAG_PARAM)).trim();
				_peer.priseService(Integer.parseInt(service));
				serviceAgent(out);
			} else {
				out.println("Transition inconnue : " + transition);
			}

		} catch (Throwable t) {
			out.println("Echec lors de la transition: " + t.getMessage());
		}

		return 0;
	}

	private void infoArret(PrintWriter out) {
		Point dernier = _peer.getModel().getDernierPoint();
		if (dernier != null) {
			out.println((_peer.getModel().isInsidePoint() ? "* A l'arret *  : "
					: "Dernier arret  : ") + dernier);
		} else {
			out.println("Aucun arret desservi.");
		}
		Point prochain = _peer.getModel().getProchainPoint();
		if (prochain != null) {
			out.println("Prochain arret : " + prochain);
		} else {
			out.println("Pas de prochain arret");
		}
	}

	// Service
	public final static String USAGE_SERVICE = "";

	public final static String[] HELP_SERVICE = new String[] { "Show current 'service agent'." };

	public int cmdService(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		serviceAgent(out);
		return 0;
	}

	private void serviceAgent(PrintWriter out) {
		ServiceAgent sa = _peer.getModel().getServiceAgent();

		String result = (sa == null) ? "aucun" : sa.toString();
		out.println("ServiceAgent : " + result);
		if (sa != null) {
			if (sa.getCourses() != null) {
				Course[] courses = sa.getCourses();
				for (int i = 0; i < courses.length; i++) {
					out.println("   (" + courses[i].getIdu() + ")  => "
							+ courses[i]);
				}
			}
		}

	}

	// Course
	public final static String USAGE_PLANIFICATION = "";

	public final static String[] HELP_PLANIFICATION = new String[] { "Show current 'course'." };

	public int cmdPlanification(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Planification:");
		out.println(_peer.getModel().getPlanification());
		return 0;
	}

	// Course
	public final static String USAGE_COURSE = "";

	public final static String[] HELP_COURSE = new String[] { "Show current 'course'." };

	public int cmdCourse(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		showCourse(out);
		return 0;
	}

	private void showCourse(PrintWriter out) {
		Course course = _peer.getModel().getCourse();
		String result = (course == null) ? "aucune" : course.toString();
		out.println("Course : " + result);
		if (course != null) {
			Point[] points = course.getPoints();
			if (points == null)
				return;
			for (int i = 0; i < points.length; i++) {
				out.println("   =>> " + points[i]);
			}
		}
	}

	// Show Max tranche
	public final static String USAGE_SHOWMAXTRANCHE = "";

	public final static String[] HELP_SHOWMAXTRANCHE = new String[] { "Show current 'max tranche'." };

	public int cmdShowmaxtranche(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Max tranche = " + ((AvmConfig) _config).getMaxTranche());
		return 0;
	}

	// Set Max tranche
	public final static String USAGE_SETMAXTRANCHE = "<int_value>";

	public final static String[] HELP_SETMAXTRANCHE = new String[] { "Set current 'max tranche'." };

	public int cmdSetmaxtranche(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String maxT = ((String) opts.get("int_value")).trim();
		((AvmConfig) _config).setMaxTranche(new Integer(maxT));
		_config.updateConfig();
		out.println("Nouvelle max tranche = "
				+ ((AvmConfig) _config).getMaxTranche());
		return 0;
	}

	// Show Tolerance Deviation
	public final static String USAGE_SHOWTOLERANCEDEVIATION = "";

	public final static String[] HELP_SHOWTOLERANCEDEVIATION = new String[] { "Show current 'tolerance deviation'." };

	public int cmdShowtolerancedeviation(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		out.println("Tolerance dev = "
				+ ((AvmConfig) _config).getToleranceDev());
		return 0;
	}

	// Set Tolerance Deviation
	public final static String USAGE_SETTOLERANCEDEVIATION = "<tolerance>";

	public final static String[] HELP_SETTOLERANCEDEVIATION = new String[] { "Set current 'tolerance deviation'." };

	public int cmdSettolerancedeviation(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String maxT = ((String) opts.get("tolerance")).trim();
		((AvmConfig) _config).setToleranceDev(new Integer(maxT));
		_config.updateConfig();
		out.println("Nouvelle Tolerance dev = "
				+ ((AvmConfig) _config).getToleranceDev());
		return 0;
	}

	// Show periode
	public final static String USAGE_SHOWPERIODE = "";

	public final static String[] HELP_SHOWPERIODE = new String[] { "Frequence de calcul de deviation" };

	public int cmdShowperiode(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println("Periode = " + ((AvmConfig) _config).getSuiviCrsPeriod()
				+ " secondes");
		return 0;
	}

	// Set periode
	public final static String USAGE_SETPERIODE = "<periode>";

	public final static String[] HELP_SETPERIODE = new String[] { "Positionne la frequence de calcul de deviation" };

	public int cmdSetperiode(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String periode = ((String) opts.get("periode")).trim();
		((AvmConfig) _config).setSuiviCrsPeriod(new Integer(periode));
		_config.updateConfig();
		out.println("Periode = " + ((AvmConfig) _config).getSuiviCrsPeriod()
				+ " secondes");
		return 0;
	}

	// Set check validite courses
	public final static String USAGE_CHECKVALIDITE = "[<check>]";

	public final static String[] HELP_CHECKVALIDITE = new String[] { "Verifie la validite des courses ('true' par default)" };

	public int cmdCheckvalidite(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String check = ((String) opts.get("check"));
		boolean current = ((AvmConfig) _config).isCheckValidite();
		if (check != null) {
			boolean newVal = check.toLowerCase().equals("true");
			if (current != newVal) {
				((AvmConfig) _config).setCheckValidite(newVal);
				_config.updateConfig(true);
			}
		}

		out.println("Controle de la validite des courses = "
				+ ((AvmConfig) _config).isCheckValidite());
		return 0;
	}

	// modele
	public final static String USAGE_MODEL = "";

	public final static String[] HELP_MODEL = new String[] { "Affiche les donnees du modele" };

	public int cmdModel(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		out.println(_peer.getModel());
		return 0;
	}

	// Set sa auto
	public final static String USAGE_SETAUTOMATICSA = "[<automatic>]";

	public final static String[] HELP_SETAUTOMATICSA = new String[] { "Active le mode service agent automatique" };

	public int cmdSetautomaticsa(Dictionary opts, Reader in, PrintWriter out,
			Session session) {
		String auto = ((String) opts.get("automatic"));
		boolean current = ((AvmConfig) _config).isAutomaticCourseMode();
		if (auto != null) {
			boolean newVal = auto.trim().toLowerCase().equals("true");
			if (current != newVal) {
				((AvmConfig) _config).setAutomaticCourseMode(newVal);
				_config.updateConfig(true);
			}
		}
		out.println("Mode SA auto = "
				+ ((AvmConfig) _config).isAutomaticCourseMode());
		return 0;
	}

	// Set automatic sa label
	public final static String USAGE_SETAUTOMATICSALABEL = "[<label>]";

	public final static String[] HELP_SETAUTOMATICSALABEL = new String[] { "Definit le libellé pour SA automatic" };

	public int cmdSetautomaticsalabel(Dictionary opts, Reader in,
			PrintWriter out, Session session) {
		String label = ((String) opts.get("label"));
		if (label != null) {
			((AvmConfig) _config).setAutomaticSALabel(label.trim());
			_config.updateConfig();
		}
		out.println("Label SA automatic = "
				+ ((AvmConfig) _config).getAutomaticSALabel());
		return 0;
	}
}
