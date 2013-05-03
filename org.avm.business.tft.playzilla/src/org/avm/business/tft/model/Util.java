package org.avm.business.tft.model;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.avm.business.messages.Messages;
import org.stringtree.json.JSONWriter;

public class Util {
	public static String toJSONString(AvmModel model, Collection messages) {
		int state = AvmModel.STATE_INITIAL;
		String text = null;
		try {

			if (model.getAuthentification() != null
					&& model.getAuthentification().isPrisePoste()) {
				state = model.getState().getValue();
			}
			SERVICE sed = new SERVICE(new Date(), state);

			Course course = model.getCourse();
			if (course != null) {
				COURSE crs = new COURSE(course.getId(), course.getIdu(),
						course.getNom(),
						course.getDepart(),
						course.getLigneIdu(), // course.getCodeLigne(),
						course.getLigneNom(), course.getCodeLigne(),
						course.getItineraireIdu());

				Point[] points = course.getPoints();
				if (points != null) {

					POINT[] pnts = new POINT[points.length];
					for (int i = 0; i < points.length; i++) {
						Point point = points[i];

						boolean nondesservi = point.getArriveeTheorique() == -1
								&& point.getAttente() == -1;
						int point_statut = nondesservi ? -1 : (point
								.isDesservi() ? 1 : 0);
						pnts[i] = new POINT(point.getIdu(), point.getNom(),
								point.getNomReduitGroupePoint(),
								point.getLongitude(), point.getLatitude(),
								(int) point.getDistance(),
								point.getCodeGirouette(),
								point.getArriveeTempsReel(),
								point.getAttente(),
								point.getArriveeTheorique(),
								point.getAttenteTheorique(), point.getRang(),
								point_statut);

					}
					crs.setPOINTS(pnts);
					sed.setCOURSE(crs);
				}

			}

			MESSAGE[] msg = new MESSAGE[messages.size()];
			Iterator iter = messages.iterator();
			int i = 0;
			while (iter.hasNext()) {
				Properties props = (Properties) iter.next();
				msg[i] = new MESSAGE();
				String message;
				try {
					byte[] m = new String(props.getProperty(Messages.MESSAGE)
							.getBytes(), "iso8859-1").getBytes("utf-8");
					message = new String(m);
					msg[i].setTexte(message);
					msg[i].setPriorite(Integer.parseInt(props
							.getProperty(Messages.PRIORITE)));
				} catch (UnsupportedEncodingException e) {
					// TODO Bloc catch généré automatiquement
					e.printStackTrace();
				}

				i++;
			}

			sed.setMESSAGES(msg);
			JSONWriter writer = new JSONWriter();
			text = writer.write(sed);

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return text;
	}
}
