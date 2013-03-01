package org.avm.business.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.core.event.ServiceAgent;
import org.avm.elementary.database.Database;

public class AvmDatabaseDatasource implements AvmDatasource {

	private static final SimpleDateFormat DF = new SimpleDateFormat("ddMMyyyy");

	private String _currentDate;

	private static AvmDatabaseDatasource _instance;

	private Logger _log;

	private Database _database;

	private int _version = 0;

	private final List listeJours = Arrays.asList(new Object[] {
			new Integer(Calendar.MONDAY), new Integer(Calendar.TUESDAY),
			new Integer(Calendar.WEDNESDAY), new Integer(Calendar.THURSDAY),
			new Integer(Calendar.FRIDAY), new Integer(Calendar.SATURDAY),
			new Integer(Calendar.SUNDAY) });

	private String _listePeriodes;

	private String _listeProprietes;

	private boolean _checkValidite;

	private static final String REQ_PERIODES_ET_PROPRIETES_JOUR = "select PDJ.PDJ_ID "
			+ "from Proprietes_de_jour PDJ "
			+ "join Jour_Proprietes JOP on PDJ.PDJ_ID = JOP.PDJ_ID "
			+ "where PDJ_TYPE=? and JEX_DATE=?";

	private static final String REQ_COURSES_SANS_VALIDITE = "select crs_depart, crs.crs_id, crs_idu, crs_nom, pnt_nom, lgn.lgn_idu, "
			+ "        lgn.lgn_nom, lgn.lgn_amplitude, lgn.lgn_chevauchement, pcr.pcr_nom, pcr.pcr_idu "
			+ " from course crs "
			// OLD +
			// " join horaire hor on (crs.crs_id=hor.crs_id and hor.hor_attente = -1 and hor.hor_rang <> 1)"
			+ " join (select crs_id,max(hor_rang) as last from horaire group by crs_id) temp on temp.crs_id=crs.crs_id"
			+ " join horaire hor on (crs.crs_id=hor.crs_id and hor.hor_rang=temp.last)"
			+ " join point_sur_parcours psp on psp.psp_id=hor.psp_id"
			+ " join point_sur_itineraire psi on psi.psi_id=psp.psi_id"
			+ " join point pnt on psi.pnt_id=pnt.pnt_id"
			+ " join itineraire iti on iti.iti_id=psi.iti_id"
			+ " join parcours pcr on pcr.iti_id=iti.iti_id"
			+ " join ligne lgn on iti.lgn_id=lgn.lgn_id"
			+ " where crs.sag_id=? order by crs_depart";

	private static final String REQ_SERVICE_AGENT = "select sag_id from service_agent where sag_idu=?";

	private static final String REQ_POINTS_COURSE = "SELECT  PNT_ID,PNT_IDU,PNT_NOM,PNT_X,PNT_Y,GRP_IDU,PSI_DISTANCE,HOR_ARRIVEE,HOR_ATTENTE,PSP_GIR "
			+ " FROM HORAIRE HOR  "
			+ " join point_sur_parcours psp on psp.psp_id=hor.psp_id  "
			+ " join point_sur_itineraire psi on psi.psi_id=psp.psi_id "
			+ " join point pnt on psi.pnt_id=pnt.pnt_id "
			+ " JOIN POINT_GROUPE_POINT PGP ON PGP.PNT_ID=PNT.PNT_ID "
			+ " JOIN GROUPE_POINT GRP ON GRP.GRP_ID=PGP.GRP_ID "
			+ " WHERE HOR.CRS_ID = ? " + " ORDER BY HOR_RANG  ";

	private static final String REQ_ATTRIBUTS_POINTS = "SELECT ADP_ID,ATT_ID,PNT_ID,ATT_NOM,ADP_VAL from attribut, attribut_point where attribut.att_id = attribut_point.att_id";

	private static final String REQ_SERVICES_AGENT = "SELECT SAG_IDU FROM SERVICE_AGENT";

	private static final String getCoursesIdValides(int sag_id,
			String listePeriodes, String listeProprietes) {
		// les courses valides doivent :
		return "select crs_id from course crs "
				+ "  where crs.sag_id="
				+ sag_id
				// - avoir au moins une période en commun avec le jour courant

				+ "    and (  (exists (select 1 from validite_course vcr"
				+ "         where vcr.crs_id=crs.crs_id and pdj_id in ("
				+ listePeriodes
				+ ")))"
				// ou pas de période du tout (elle est valable tout le temps !)
				+ "            or(not exists (select 1 from validite_course vcr"
				+ "                join proprietes_de_jour pdj"
				+ "                     on pdj.pdj_id=vcr.pdj_id"
				+ "               where vcr.crs_id=crs.crs_id and pdj_type=0))"
				+ "    )"
				// ET - avoir une propriété de jour en commun
				+ "    and exists (select 1 from validite_course vcr"
				+ "             where vcr.crs_id=crs.crs_id and pdj_id in ("
				+ listeProprietes + "))";
	}

	private static final String getCoursesValides(String listeCourses) {
		return "select crs_depart, crs.crs_id, crs_idu, crs_nom, pnt_nom, pcr.pcr_idu,pcr.pcr_nom,lgn.lgn_idu, "
				+ "        lgn.lgn_nom, lgn.lgn_amplitude, lgn.lgn_chevauchement from course crs "
				+ "  join (select crs_id,max(hor_rang) as last from horaire group by crs_id) temp on temp.crs_id=crs.crs_id"
				+ "  join horaire hor on (crs.crs_id=hor.crs_id and hor.hor_rang=temp.last )"
				+ "  join point_sur_parcours psp on psp.psp_id=hor.psp_id"
				+ "  join parcours pcr on psp.pcr_id=pcr.pcr_id"
				+ "  join point_sur_itineraire psi on psi.psi_id=psp.psi_id"
				+ "  join point pnt on psi.pnt_id=pnt.pnt_id"
				+ "  join itineraire iti on iti.iti_id=psi.iti_id"
				+ "  join ligne lgn on iti.lgn_id=lgn.lgn_id"
				+ "  where crs_id in ("
				+ listeCourses
				+ ")"
				+ "  order by crs_depart";
	}

	private void checkCurrentDay() throws AvmDatabaseException {
		String date = DF.format(new Date());
		boolean update = _currentDate == null || (!_currentDate.equals(date));
		_log.info("Mise a jour des proprietes/periodes necessaire :" + update);
		if (update) {
			_currentDate = date;
			init();
		}
	}

	private void init() throws AvmDatabaseException {
		Calendar cal = Calendar.getInstance();

		int pdj_id_jour_courant = listeJours.indexOf(new Integer(cal
				.get(Calendar.DAY_OF_WEEK)));

		java.sql.Date dateCourante = new java.sql.Date(cal.getTime().getTime());

		_log.info("Date du jour:" + dateCourante);
		try {
			Connection connexion = getDatabase().getConnection();
			PreparedStatement requete;
			requete = connexion
					.prepareStatement(REQ_PERIODES_ET_PROPRIETES_JOUR);
			requete.setInt(1, 0); // 0 correspond au type PDJ_PERIODE
			requete.setDate(2, dateCourante);
			ResultSet rs = requete.executeQuery();
			_log.debug("requete 1:" + requete);
			_log.debug("datecourante:" + dateCourante);
			_listePeriodes = "";

			if (rs.next()) {

				StringBuffer strBuf = new StringBuffer();
				strBuf.append(rs.getString(1));
				while (rs.next()) {
					strBuf.append(",");
					strBuf.append(rs.getString(1));
				}
				_listePeriodes = strBuf.toString();
			}
			_log.debug("Periodes:" + _listePeriodes);
			rs.close();

			requete.setInt(1, 1); // 1 correspond au type PDJ_PROPRIETE
			requete.setDate(2, dateCourante);
			_log.debug("requete 2:" + requete);
			_listeProprietes = "";
			rs = requete.executeQuery();
			StringBuffer strBuf = new StringBuffer();
			if (rs.next()) {

				strBuf.append(rs.getString(1));
				while (rs.next()) {
					strBuf.append(",");
					strBuf.append(rs.getString(1));
				}
				// on ajoute la propriété du jour de la semaine
				strBuf.append(",");
				_listeProprietes = "";
			}
			strBuf.append(pdj_id_jour_courant);
			_listeProprietes = strBuf.toString();

			_log.info("Proprietes:" + _listeProprietes);
			rs.close();

			if (0 == _listePeriodes.length() || 0 == _listeProprietes.length()) {
				_log.warn("calendrier périmé ou jour (" + dateCourante
						+ ") non géré (periode=" + _listePeriodes
						+ ", proprietes=" + _listeProprietes);
				_listePeriodes = "-1";
				_listeProprietes = "-1";
				throw new AvmDatabaseException(
						AvmDatabaseException.ERR_BASE_PERIMEE);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private AvmDatabaseDatasource() {
		_log = Logger.getInstance(this.getClass());
	}

	public static AvmDatabaseDatasource getInstance() {
		if (_instance == null) {
			_instance = new AvmDatabaseDatasource();
		}
		return _instance;
	}

	public void setDatabase(Database database) throws AvmDatabaseException {
		_database = database;
		if (database != null) {
			try {
				_log.info("Database version:" + getDatabase().getVersion());
				_version = Integer.parseInt(getDatabase().getVersion());
				_currentDate = null;
			} catch (Throwable t) {
				_log.error("Error getVersion : parseInt ("
						+ getDatabase().getVersion() + ") ", t);
			}
			checkCurrentDay();
		}

	}

	public int getVersion() {
//		_version = 1;
//		System.out.println(" ****************************************************************************");
//		System.out.println(" **************** DATABASE EN VERSION "+ _version + " POUR DEBUG !!!!! ******************");
//		System.out.println(" **************** DATABASE EN VERSION "+ _version + " POUR DEBUG !!!!! ******************");
//		System.out.println(" **************** DATABASE EN VERSION "+ _version + " POUR DEBUG !!!!! ******************");
//		System.out.println(" **************** DATABASE EN VERSION "+ _version + " POUR DEBUG !!!!! ******************");
//		System.out.println(" ****************************************************************************");

		return _version;
	}

	public ServiceAgent getServiceAgent(int sag_idu)
			throws AvmDatabaseException {
		ServiceAgent sa = null;
		getDatabase(); // --check database service
		if (_checkValidite) {
			sa = getServiceAgentAvecValidite(sag_idu);
		} else {
			sa = getServiceAgentSansValidite(sag_idu);
		}
		return sa;
	}

	public ServiceAgent getServiceAgentAvecValidite(int sag_idu)
			throws AvmDatabaseException {
		Course[] courses = null;
		int sag_id = -1; // SAG_ID
		ServiceAgent result = null;
		long time = System.currentTimeMillis();
		String sag_lib = null;

		_log.info("Algorithme AVEC controle de validite des courses");
		checkCurrentDay();

		if (sag_idu != -1) {
			ResultSet rs = null;
			String request = null;

			// FLA ajout sag_lib, pour détection mode automatic
			request = "select sag_id, sag_lib from service_agent where sag_idu='"
					+ sag_idu + "'";

			rs = getDatabase().sql(request);
			if (rs == null) {
				throw new AvmDatabaseException(
						AvmDatabaseException.ERR_BASE_INTROUVABLE);
			}
			ResultSet rsCourses = null;
			try {
				if (rs != null && rs.next()) {
					sag_id = rs.getInt(1);
					sag_lib = rs.getString(2);
					
					request = getCoursesIdValides(sag_id, _listePeriodes,
							_listeProprietes);
					rsCourses = getDatabase().sql(request);

					StringBuffer listeCourses = new StringBuffer();
					if (rsCourses.next()) {
						listeCourses.append(rsCourses.getInt("CRS_ID"));
						while (rsCourses.next()) {
							listeCourses.append(",");
							listeCourses.append(rsCourses.getInt("CRS_ID"));
						}
						if (rsCourses != null) {
							rsCourses.close();
						}

					} else {
						if (rsCourses != null) {
							rsCourses.close();
						}
						throw new AvmDatabaseException(
								AvmDatabaseException.ERR_AUCUNE_COURSE_VALIDE);
					}

					_log.debug("liste courses id valides :" + listeCourses);
					request = getCoursesValides(listeCourses.toString());

					rsCourses = getDatabase().sql(request);

					ArrayList listCourses = null;
					while (rsCourses != null && rsCourses.next()) {
						if (listCourses == null) {
							listCourses = new ArrayList();
						}

						String destination = rsCourses.getString("PNT_NOM");
						int crs_idu = rsCourses.getInt("CRS_IDU");
						int crs_id = rsCourses.getInt("CRS_ID");
						int crs_depart = rsCourses.getInt("CRS_DEPART");
						String crs_nom = rsCourses.getString("CRS_NOM");
						String lgn_nom = rsCourses.getString("LGN_NOM");
						int lgn_idu = rsCourses.getInt("LGN_IDU");
						int amplitude = rsCourses.getInt("LGN_AMPLITUDE");
						int chevauchement = rsCourses
								.getInt("LGN_CHEVAUCHEMENT");
						String pcr_nom = rsCourses.getString("PCR_NOM");
						int pcr_idu = rsCourses.getInt("PCR_IDU");

						Course crs = new Course(crs_idu, crs_id, crs_nom,
								crs_depart, destination, lgn_nom, lgn_idu,
								pcr_nom, pcr_idu, amplitude, chevauchement);

						listCourses.add(crs);
					}
					Object[] list = null;
					if (listCourses != null) {
						list = listCourses.toArray();
						courses = new Course[list.length];
						System.arraycopy(list, 0, courses, 0, list.length);
					}
					result = new ServiceAgent(courses != null, sag_idu, courses);
				} else {
					_log.warn("rs.next()==false !!!");
					result = new ServiceAgent(false, sag_idu, null);
					result.setLibelle(sag_lib);
					throw new AvmDatabaseException(
							AvmDatabaseException.ERR_SERVICE_AGENT_INCONNU);
				}
			} catch (SQLException e) {
				_log.error("SQLException:", e);
			} finally {
				try {
					rs.close();
					if (rsCourses != null) {
						rsCourses.close();
					}
				} catch (SQLException e) {
					_log.error(e);
				}
			}
		}
		_log.info("Algorithme AVEC controle de validite des courses ; temps traitement :" + (System.currentTimeMillis()-time)  + " ms.");
		return result;
	}

	public ServiceAgent getServiceAgentSansValidite(int sag_idu)
			throws AvmDatabaseException {
		Course[] courses = null;
		int sag_id = -1; // SAG_ID
		ServiceAgent result = null;
		long time = System.currentTimeMillis();

		_log.info("Algorithme SANS controle de validite des courses");

		if (sag_idu != -1) {
			ResultSet rs = null;
			try {
				Connection connexion = getDatabase().getConnection();
				PreparedStatement requete;

				// recuperation du service agent
				requete = connexion.prepareStatement(REQ_SERVICE_AGENT);
				requete.setInt(1, sag_idu);
				rs = requete.executeQuery();

				if (rs == null || rs.next() == false) {
					result = new ServiceAgent(false, sag_idu, null);
					throw new AvmDatabaseException(
							AvmDatabaseException.ERR_SERVICE_AGENT_INCONNU);
				}
				sag_id = rs.getInt(1);
				rs.close();

				// recuperation des courses sans gestion validite
				requete = connexion.prepareStatement(REQ_COURSES_SANS_VALIDITE);
				requete.setInt(1, sag_id);
				rs = requete.executeQuery();

				ArrayList listCourses = null;
				Hashtable hashCourses = new Hashtable();
				while (rs != null && rs.next()) {
					if (listCourses == null) {
						listCourses = new ArrayList();
					}

					String destination = rs.getString("PNT_NOM");
					int crs_idu = rs.getInt("CRS_IDU");
					String key = Integer.toString(crs_idu);
					if (hashCourses.get(key) != null) {
						continue;
					}
					hashCourses.put(key, key);
					int crs_id = rs.getInt("CRS_ID");
					int crs_depart = rs.getInt("CRS_DEPART");
					String crs_nom = rs.getString("CRS_NOM");
					String lgn_nom = rs.getString("LGN_NOM");
					int lgn_idu = rs.getInt("LGN_IDU");
					int amplitude = rs.getInt("LGN_AMPLITUDE");
					int chevauchement = rs.getInt("LGN_CHEVAUCHEMENT");
					String pcr_nom = rs.getString("PCR_NOM");
					int pcr_idu = rs.getInt("PCR_IDU");

					Course crs = new Course(crs_idu, crs_id, crs_nom,
							crs_depart, destination, lgn_nom, lgn_idu, pcr_nom,
							pcr_idu, amplitude, chevauchement);

					listCourses.add(crs);
				}
				Object[] list = null;
				if (listCourses != null) {
					list = listCourses.toArray();
					courses = new Course[list.length];
					System.arraycopy(list, 0, courses, 0, list.length);
				}
				result = new ServiceAgent(courses != null, sag_idu, courses);

			} catch (SQLException e) {
				_log.error("SQLException:", e);
			} finally {
				try {
					rs.close();
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException e) {
					_log.error(e);
					// throw new AvmDatabaseException(
					// AvmDatabaseException.ERR_BASE);
				}
			}
		}

		_log.info("Algorithme SANS controle de validite des courses ; temps traitement :" + (System.currentTimeMillis()-time)  + " ms.");
		return result;
	}

	public Course getCourse(ServiceAgent sa, int courseIDU) {
		Course result = null;
		for (int i=0; i<sa.getNbCourse();i++){
			_log.info("course["+i+"]=" + sa.getCourses()[i]);
		}
		_log.info("Course " +courseIDU +" from sa " + sa + "...");

		Course course = sa.getCourseByIdu(courseIDU);
		_log.info("Course = " + course);
		if (course != null) {
			ResultSet rs = null;

			int crs_id = course.getId();

			_log.debug("recherche de l'identifiant du parcours");
			try {
				_log.debug("recherche de tous les points (horaire, distance, x,y,nom) de la course");

				// recuperation du service agent
				Connection connexion = null;
				try {
					connexion = getDatabase().getConnection();
				} catch (AvmDatabaseException e) {
					e.printStackTrace();
					return null;
				}
				PreparedStatement requete;
				requete = connexion.prepareStatement(REQ_POINTS_COURSE);
				requete.setInt(1, crs_id);
				rs = requete.executeQuery();

				_log.debug("Contruction du vecteur de points...");
				if (rs != null) {
					ArrayList listPoints = new ArrayList();
					int hor_rang = 0;
					while (rs.next()) {
						int idx = 1;
						int pnt_id = rs.getInt(idx++);
						int pnt_idu = rs.getInt(idx++);
						String pnt_nom = rs.getString(idx++);
						int pnt_x = rs.getInt(idx++);
						int pnt_y = rs.getInt(idx++);
						String reduit = rs.getString(idx++);
						int psp_distance = rs.getInt(idx++);
						int hor_arrivee = rs.getInt(idx++);
						int hor_attente = rs.getInt(idx++);
						hor_rang++;
						int codeGirouette = rs.getInt(idx++);

						Point point = new Point(pnt_id, pnt_idu, pnt_nom, 0,
								hor_arrivee, hor_attente, hor_rang,
								psp_distance, pnt_x, pnt_y, codeGirouette);
						point.setNomReduitGroupePoint(reduit);
						listPoints.add(point);
					}

					Object[] list = null;
					Point[] points = null;
					if (listPoints.size() > 0) {
						list = listPoints.toArray();
						points = new Point[list.length];
						System.arraycopy(list, 0, points, 0, list.length);
					}
					course.setPoints(points);
					getAttributPoints(course);
					course.updatePoints();
					result = course;
					_log.debug("Course = " + result);
				} else {
					_log.warn("rs.next()==false !!!");
				}
			} catch (SQLException e) {
				_log.error(e);
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException e) {
					_log.error(e);
				}
			}
		}
		return result;
	}

	private void getAttributPoints(Course course) {

		if (course != null) {
			ResultSet rs = null;

			try {
				_log.info("Attributs : recherche de tous les attributs de points de la course");

				Connection connexion = null;
				try {
					connexion = getDatabase().getConnection();
				} catch (AvmDatabaseException e) {
					e.printStackTrace();
					return;
				}
				PreparedStatement requete;
				requete = connexion.prepareStatement(REQ_ATTRIBUTS_POINTS);
				rs = requete.executeQuery();

				_log.debug("Attributs : Contruction du la liste de points...");

				if (rs != null) {
					long time = System.currentTimeMillis();
					while (rs.next()) {
						int idx = 1;
						int adp_id = rs.getInt(idx++);
						int att_id = rs.getInt(idx++);
						int pnt_id = rs.getInt(idx++);
						String att_nom = rs.getString(idx++);
						String att_val = rs.getString(idx++);
						Point[] points = course.getPointAvecId(pnt_id);
						if (points != null) {
							points[0].setAttribute(new Integer(att_id), att_val);
							System.out.println("point["+points[0].getNom()+"].setAttribut("+new Integer(att_id)+") => "+att_val);
						}
					}
					_log.info("Attributs : temps traitement  "
							+ (System.currentTimeMillis() - time) + " ms.");
				} else {
					_log.warn("rs.next()==false !!!");
				}
			} catch (SQLException e) {
				_log.error(e);
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException e) {
					_log.error(e);
				}
			}
		}

	}

	public String getValiditePeriode() {
		return _listePeriodes;
	}

	public String getValiditePropriete() {
		return _listeProprietes;
	}

	public void setCheckValidite(boolean check) {
		_checkValidite = check;
	}

	private Database getDatabase() throws AvmDatabaseException {
		if (_database == null) {
			throw new AvmDatabaseException(
					AvmDatabaseException.ERR_BASE_INTROUVABLE);
		}
		return _database;
	}

	public List getServicesAgent() {
		List result = new ArrayList();
		PreparedStatement requete;

		Connection connexion = null;
		ResultSet rs = null;
		try {
			connexion = getDatabase().getConnection();
			requete = connexion.prepareStatement(REQ_SERVICES_AGENT);

			rs = requete.executeQuery();

			_log.debug("Contruction du vecteur de points...");
			if (rs != null) {
				while (rs.next()) {
					String sag_idu = rs.getString(1);
					result.add(sag_idu);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (AvmDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				_log.error(e);
			}
		}

		return result;
	}

}
