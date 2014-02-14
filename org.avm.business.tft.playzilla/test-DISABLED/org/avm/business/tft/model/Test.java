package org.avm.business.tft.model;

import java.util.Date;

import junit.framework.TestCase;

import org.avm.business.core.AvmModel;
import org.avm.business.core.AvmModelManager;
import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;
import org.stringtree.json.JSONWriter;

public class Test extends TestCase {

	private void process(AvmModel model) {

		SERVICE sed = new SERVICE(new Date(),model.getState().getValue());
		Course course = model.getCourse();
		COURSE crs = new COURSE(course.getId(), course.getIdu(), course
				.getNom(), course.getDepart(), course.getCodeLigne(), course.getLigneNom(), course
				.getItineraireIdu());
		Point[] points = course.getPoints();
		POINT[] pnts = new POINT[points.length];
		for (int i = 0; i < points.length; i++) {
			Point point = points[i];
			pnts[i] = new POINT(point.getIdu(), point.getNom(),
					point.getNomReduitGroupePoint(), point.getLongitude(),
					point.getLatitude(), (int) point.getDistance(), point
							.getCodeGirouette(), point.getArriveeTempsReel(), 0, point
							.getArriveeTheorique(),point.getAttente(),point.getRang(), point.isDesservi()?1:0);
		}
		crs.setPOINTS(pnts);
		sed.setCOURSE(crs);

		JSONWriter writer = new JSONWriter();
		String text = writer.write(sed);
		System.out.println(text);
	}

	public void testModel() {

		AvmModelManager model = new AvmModelManager();
		
		Course course = new Course(123, 0, 456,  "crsblip",0, "dest", 45, "9|15", "111", "9", 222, 333);
		Point pts[] = new Point[3];
		pts[0] = new Point(10, "HOTEL", 1, 2, 3, 4, 5, 6, 7, 8 ,0 );pts[0].setNomReduitGroupePoint("bilout");
		pts[1] = new Point(20, "CHATELET", 11, 12, 13, 14, 15, 16, 17, 18 ,0 );
		pts[2] = new Point(30, "PORT ROYAL", 21, 22, 23, 24, 25, 26, 27, 28, 0);
		course.setPoints(pts);
		
		model.setCourse(course);
		model.setState(5);
		
		process(model);
	}

}
