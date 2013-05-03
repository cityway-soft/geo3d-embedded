package org.avm.business.tft.model;

import java.util.Date;

import org.avm.business.core.AvmModel;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.stringtree.json.JSONWriter;

public class Util {
	public static String toJSONString(AvmModel model, String[] messages) {
		int state = AvmModel.STATE_INITIAL;
		if (model.getAuthentification() != null && model.getAuthentification().isPrisePoste()){
			state = model.getState().getValue();
		}
		SERVICE sed = new SERVICE(new Date(), state);

		Course course = model.getCourse();
		if (course != null) {
			COURSE crs = new COURSE(course.getId(), course.getIdu(), course.getNom(), course.getDepart(), course.getLigneIdu(), course.getLigneNom(), course
					.getAmplitude(), course.getChevauchement(), model.isHorsItineraire() ? 1 : 0);
			Point[] points = course.getPoints();
			POINT[] pnts = new POINT[points.length];
			for (int i = 0; i < points.length; i++) {
				Point point = points[i];
				pnts[i] = new POINT(point.getId(), point.getIdu(), point
						.getNom(), point.getNomReduitGroupePoint(), point
						.getLongitude(), point.getLatitude(), (int) point.getDistance(), point
						.getCodeGirouette(), point.getArriveeTempsReel(), 0,
						point.getArriveeTheorique(), point.getAttente(), point
								.getRang(), point.isDesservi() ? 1 : 0);
			}
			crs.setPOINTS(pnts);
			sed.setCOURSE(crs);
		}
		
		sed.setMESSAGES(messages);
		JSONWriter writer = new JSONWriter();
		String text = writer.write(sed);
		return text;
	}
}
