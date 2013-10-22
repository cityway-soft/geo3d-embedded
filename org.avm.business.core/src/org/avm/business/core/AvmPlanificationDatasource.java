package org.avm.business.core;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.business.protocol.phoebus.Horodate;
import org.avm.business.protocol.phoebus.Planification;
import org.avm.elementary.database.Database;
import org.avm.elementary.database.DatabaseInjector;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

public class AvmPlanificationDatasource implements AvmDatasource,
		PreferencesServiceInjector, DatabaseInjector {

	private Logger _log;
	protected Preferences _rootNode;
	protected PreferencesService _preferencesService;
	private Database _database;

	private ServiceAgent _service;
	private org.avm.business.core.event.Planification _planification;
	private int _matricule;

	private static final String AGT_IDU = "agt_idu";

	private static final String PLA_DATE = "pla_date";
	private static final String PLA_ID = "pla_id";
	private static final String PLA_CHK = "pla_chk";
	private static final String PLA_VER = "pla_ver";
	private static final String SAG_NCO = "sag_nco";
	private static final String SAG_IDU = "sag_idu";

	private static final String CRS_IDU = "crs_idu";
	private static final String CRS_DEP = "crs_depart";
	private static final String CRS_NPT = "crs_npoints";
	private static final String CRS_LIG = "crs_lig";
	private static final String CRS_AMP = "crs_ampl";
	private static final String CRS_CHE = "crs_chev";

	private static final String PNT_ID = "pnt_id_";
	private static final String PNT_ARR = "pnt_arr_";
	private static final String PNT_ATT = "pnt_att_";
	private static final String PNT_DIST = "pnt_dist_";
	private static final String PNT_GIR = "pnt_gir_";

	private static final String PERSISTANCE_USER_NAME = "avm-planification";

	private static AvmPlanificationDatasource _instance;

	private AvmPlanificationDatasource() {
		_log = Logger.getInstance(this.getClass());
		// _log.setPriority(Priority.DEBUG);
		_planification = new org.avm.business.core.event.Planification(0, 0, 0,
				0, 0, null, 0);
	}

	public static AvmPlanificationDatasource getInstance() {
		if (_instance == null) {
			_instance = new AvmPlanificationDatasource();
		}
		return _instance;
	}

	protected void init() throws BackingStoreException {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws BackingStoreException {
					_rootNode = _preferencesService
							.getUserPreferences(PERSISTANCE_USER_NAME);
					load();
					return (null);
				}
			});
		} catch (PrivilegedActionException ex) {
			throw ((BackingStoreException) ex.getException());
		}
	}

	public void checkCurrentMatricule(int matricule) {
		_matricule = matricule;
		if (_planification != null
				&& _planification.getMatricule() != matricule) {
			// -- ne pas prendre en compte la planification sauvegardee si elle
			// ne concerne pas le meme conducteur.
			_log
					.info("Planification sauvegardee est ignoree (mat planif sauv= "
							+ _planification.getMatricule()
							+ " != mat. courrant=" + matricule + ").");
			_planification = new org.avm.business.core.event.Planification(0,
					0, 0, 0, 0, null, 0);
			;
		}
	}

	public void notify(Planification planification) {
		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Reception planification : " + planification);
			}
			int serviceIdu = planification.getService();
			Horodate hd = planification.getEntete().getDate();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, hd.getAnnee() + 1990);
			cal.set(Calendar.MONTH, hd.getMois() - 1);
			cal.set(Calendar.DAY_OF_MONTH, hd.getJour());
			Date date = cal.getTime();
			int nbCourses = planification.getCourses().length;

			org.avm.business.core.event.Planification newPlanification = new org.avm.business.core.event.Planification(
					_matricule, planification.getPlanification(), planification
							.getChecksum(), planification.getEntete()
							.getVersion(), serviceIdu, date, nbCourses);

			if (nbCourses > 0) { // -- nouvelle planification
				_planification = newPlanification;
				if (nbCourses != 0) {
					Course[] courses = new Course[nbCourses];
					for (int i = 0; i < courses.length; i++) {
						courses[i] = convert(planification.getCourses()[i]);
					}
					_service = new ServiceAgent(true, serviceIdu, courses);
				}
				_service.setPlanifie(true);
				if (_planification.isCorrect()) {
					save(planification);
				}
			} else if (newPlanification.getChecksum() == 0
					&& (newPlanification.getId() == 0)) {
				_planification = newPlanification;
				_log.info("Planification vide");
			} else {
				_log.info("Planification déja reçue (réutilisation) : "
						+ _planification);
			}
			_planification.confirm(true);
		} catch (Throwable t) {
			_log.error("Erreur on planification : ", t);
		}
	}

	public int getVersion() {
		return _planification.getVersion();
	}

	public int getChecksum() {
		return _planification.getChecksum();
	}

	public void load() {
		try {
			int agt_idu = _rootNode.getInt(AGT_IDU, 0);

			long t = _rootNode.getLong(PLA_DATE, 0);

			Date planificationDate = new Date(t);

			int checksum = _rootNode.getInt(PLA_CHK, 0);

			int planificationId = _rootNode.getInt(PLA_ID, 0);

			int planificationVersion = _rootNode.getInt(PLA_VER, 0);

			int sag_idu = _rootNode.getInt(SAG_IDU, 0);

			int ncourses = _rootNode.getInt(SAG_NCO, 0);

			_planification = new org.avm.business.core.event.Planification(
					agt_idu, planificationId, checksum, planificationVersion,
					sag_idu, planificationDate, ncourses);

			Course[] courses = new Course[ncourses];
			int i = 0;
			while (i < courses.length) {
				courses[i] = loadCourse(i);
				i++;
			}

			_service = new ServiceAgent(true, sag_idu, courses);
			_service.setPlanifie(true);
		} catch (Throwable t) {
			_log.error("Error:", t);
		}
	}

	public void save(Planification planification) {
		_log.debug("saving...");
		_rootNode.putInt(AGT_IDU, _planification.getMatricule());

		_rootNode.putLong(PLA_DATE, _planification.getDate().getTime());

		_rootNode.putInt(PLA_CHK, _planification.getChecksum());

		_rootNode.putInt(PLA_ID, _planification.getId());

		_rootNode.putInt(PLA_VER, _planification.getVersion());

		_rootNode.putInt(SAG_IDU, _planification.getServiceIdu());

		org.avm.business.protocol.phoebus.Course[] courses = planification
				.getCourses();

		_rootNode.putInt(SAG_NCO, courses.length);
		int i = 0;
		while (i < courses.length) {
			addCourse(_rootNode, i, courses[i]);
			i++;
		}

		try {
			_rootNode.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		_log.debug("saved.");

	}

	private Course convert(org.avm.business.protocol.phoebus.Course course) {

		int idu = course.getCourse();
		String nom = null;
		int depart = course.getDepart();

		int lgn_idu = course.getLigne();
		int amplitude = course.getAmplitude();
		int chevauchement = course.getChevauchement();

		String destination = null;
		String lgn_nom = null;
		int idx = course.getHoraires().length - 1;
		if (idx != -1) {
			Point terminus = getPointFromDatabase(course.getHoraires()[idx]
					.getPoint());
			destination = terminus.getNom();
			lgn_nom = getLigneNomFromDatabase(lgn_idu);
		}
		Course c = new Course(idu, idu, nom, depart, destination, lgn_nom,
				lgn_idu, "", 0, amplitude, chevauchement, 1);

		return c;
	}

	private String getLigneNomFromDatabase(int lgn_idu) {
		String result = "" + lgn_idu;
		if (_database != null) {

			StringBuffer buf = new StringBuffer();
			buf.append("SELECT  LGN_NOM FROM LIGNE ");
			buf.append(" WHERE LGN_IDU=");
			buf.append(lgn_idu);

			ResultSet rs = _database.sql(buf.toString());

			try {
				if (rs != null && rs.next()) {
					result = rs.getString("LGN_NOM");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private HashMap getPointsFromDatabase(int[] ids) {
		HashMap map = new HashMap();
		if (_database != null) {
			StringBuffer buf = new StringBuffer();

			buf
					.append("SELECT  PNT_ID,PNT_IDU,PNT_NOM,PNT_X,PNT_Y,GRP_IDU FROM POINT PNT  ");
			buf
					.append(" LEFT OUTER JOIN POINT_GROUPE_POINT PGP ON PGP.PNT_ID=PNT.PNT_ID ");
			buf
					.append(" LEFT OUTER JOIN GROUPE_POINT GRP ON GRP.GRP_ID=PGP.GRP_ID ");
			buf.append(" WHERE pnt.pnt_idu in ( ");
			for (int i = 0; i < ids.length; i++) {
				buf.append(ids[i]);
				if ((i + 1) < ids.length) {
					buf.append(", ");
				}
			}
			buf.append(" )");

			ResultSet rs = _database.sql(buf.toString());

			try {
				while (rs != null && rs.next()) {
					Point p = createPoint(rs);
					map.put(new Integer(p.getIdu()), p);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	private Point createPoint(ResultSet rs) throws SQLException {
		String pnt_nom = rs.getString("PNT_NOM"); // -- nom terminus
		int pnt_id = rs.getInt("PNT_ID");
		int pnt_idu = rs.getInt("PNT_IDU");
		float pnt_x = rs.getFloat("PNT_X");
		float pnt_y = rs.getFloat("PNT_Y");
		String reduit = rs.getString("GRP_IDU");
		if (reduit == null) {
			reduit = Integer.toString(pnt_idu);
		}

		Point p = new Point(pnt_id, pnt_idu, pnt_nom, 0, pnt_x, pnt_y);
		p.setNomReduitGroupePoint(reduit);
		return p;
	}

	private Point getPointFromDatabase(int idu) {
		Point result = null;
		if (_database != null) {
			StringBuffer buf = new StringBuffer();

			buf
					.append("SELECT  PNT_ID,PNT_IDU,PNT_NOM,PNT_X,PNT_Y,GRP_IDU FROM POINT PNT  ");
			buf
					.append(" LEFT OUTER JOIN POINT_GROUPE_POINT PGP ON PGP.PNT_ID=PNT.PNT_ID ");
			buf
					.append(" LEFT OUTER JOIN GROUPE_POINT GRP ON GRP.GRP_ID=PGP.GRP_ID ");
			buf.append(" WHERE pnt.pnt_idu=");
			buf.append(idu);

			ResultSet rs = _database.sql(buf.toString());

			try {
				if (rs != null && rs.next()) {
					result = createPoint(rs);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (result == null) {
			result = new Point(idu, idu, "Arret " + idu + " inconnu!", 0, 0, 0);
		}

		return result;
	}

	private Preferences addCourse(Preferences parent, int rang,
			org.avm.business.protocol.phoebus.Course course) {
		Preferences node = parent.node(Integer.toString(rang));
		node.putInt(CRS_IDU, course.getCourse());
		node.putInt(CRS_DEP, course.getDepart());
		node.putInt(CRS_NPT, course.getHoraires().length);
		node.putInt(CRS_LIG, course.getLigne());
		node.putInt(CRS_AMP, course.getAmplitude());
		node.putInt(CRS_CHE, course.getChevauchement());

		int i = 0;
		while (i < course.getHoraires().length) {

			org.avm.business.protocol.phoebus.Horaire p = course.getHoraires()[i];
			node.putInt(PNT_ID + (i + 1), p.getPoint());
			node.putInt(PNT_ARR + (i + 1), p.getArrivee());
			node.putInt(PNT_ATT + (i + 1), p.getAttente());
			node.putFloat(PNT_DIST + (i + 1), p.getDistance());
			node.putInt(PNT_GIR + (i + 1), p.getGirouette());
			i++;
		}
		return node;
	}

	private Course getCompleteCourse(int crs_idu) {
		int count = _service.getCourses().length;
		Course course = null;
		Preferences node = null;
		boolean found = false;
		int i = 0;
		while (i < count && found == false) {
			node = _rootNode.node(Integer.toString(i));
			int idu = node.getInt(CRS_IDU, 0);
			if (idu == crs_idu) {
				found = true;
				break;
			}
			i++;
		}

		if (found && node != null) {
			course = new Course(node.getInt(CRS_IDU, 0), node
					.getInt(CRS_IDU, 0), "Course " + i,
					node.getInt(CRS_DEP, 0), null,
					"" + node.getInt(CRS_LIG, 0), node.getInt(CRS_LIG, 0), "",
					0, node.getInt(CRS_AMP, 0), node.getInt(CRS_CHE, 0), 1);
			int npts = node.getInt(CRS_NPT, 0);

			Point points[] = null;
			if (npts > 0) {
				// aucun point pour cette course proposee dans la planification
				i = 0;
				int pntids[] = new int[npts];
				while (i < npts) {
					pntids[i] = node.getInt(PNT_ID + (i + 1), 0);
					i++;
				}

				HashMap map = getPointsFromDatabase(pntids);

				points = new Point[npts];
				i = 0;
				while (i < npts) {
					int idu = node.getInt(PNT_ID + (i + 1), 0);
					Point p = (Point) map.get(new Integer(idu));
					if (p == null) {
						points[i] = new Point(idu, idu, "Arret " + idu
								+ " inconnu!?", 0, 0, 0);
					} else {
						points[i] = (Point) p.clone();
						points[i].setRang(i + 1);
						points[i].setArriveeTheorique(node.getInt(PNT_ARR
								+ (i + 1), 0));
						points[i].setAttente(node.getInt(PNT_ATT + (i + 1), 0));
						points[i].setDistance(node.getFloat(PNT_DIST + (i + 1),
								0));
						points[i].setCodeGirouette(node.getInt(PNT_GIR
								+ (i + 1), 0));
						// _log.debug("point[" + i + "]=" + points[i]);
					}
					i++;
				}
			}

			course.setPoints(points);
			course.updatePoints();
		}

		return course;

	}

	private Course loadCourse(int rang) {
		String destination = null;
		Preferences node = _rootNode.node(Integer.toString(rang));
		int npts = node.getInt(CRS_NPT, 0);
		if (npts > 0) {
			int iduTerminus = node.getInt(PNT_ID + (npts), 0);
			Point terminus = getPointFromDatabase(iduTerminus);
			destination = terminus.getNom();
		}

		Course course = new Course(node.getInt(CRS_IDU, 0), node.getInt(
				CRS_IDU, 0), "Course " + rang, node.getInt(CRS_DEP, 0),
				destination, node.getInt(CRS_LIG, 0) + "", node.getInt(CRS_LIG,
						0), "", 0, node.getInt(CRS_AMP, 0), node.getInt(
						CRS_CHE, 0),1);
		return course;
	}

	public ServiceAgent getServiceAgent(int sag_idu) {
		_log.debug("getServiceAgent = " + _service);
		return _service;
	}

	public Course getCourse(ServiceAgent sa, int courseIDU) {
		Course result = null;
		if (_service != null) {
			try {
				result = getCompleteCourse(courseIDU);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			if (result == null) {
				throw new NoSuchElementException("course " + courseIDU);
			}
			return (Course) result.clone();
		}
		return result;
	}

	public void setDatabase(Database database) {
		_database = database;

	}

	public void unsetDatabase(Database database) {
		_database = null;
	}

	public void setPreferencesService(PreferencesService prefs) {
		_preferencesService = prefs;
		if (prefs != null) {
			try {
				init();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public void unsetPreferencesService(PreferencesService prefs) {
		_preferencesService = null;
	}

	public org.avm.business.core.event.Planification getPlanification() {
		return _planification;
	}

	public String getValiditePeriode() {
		return null;
	}

	public String getValiditePropriete() {
		return null;
	}

	public void setCheckValidite(boolean check) {
	}

}
