/**
 * 
 */
package org.avm.business.core.suivi;

import junit.framework.TestCase;

import org.avm.business.core.event.Course;
import org.avm.business.core.event.Point;

/**
 * @author lbr
 * 
 */
public class SuiviCourseTest1 extends TestCase {
	static final int T0 = 255;
	
	private SuiviCourse _sc;

	private Course _course;

	private Point[] _points = null;

	/**
	 * @param arg0
	 */
	public SuiviCourseTest1(String arg0) {
		super(arg0);
		_sc = new SuiviCourse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		createTestCourse();
		_sc.setCourse(_course);
		_sc.setAmplitude(new Integer(25));
		_sc.setChevauchement(new Integer(10));
		_sc.setMaxTranche(new Integer(256));
		_sc.setPeriodiciteTraitement(new Integer(1000));
		_sc.start();
		System.out.println("----------------------------");
		System.out.println("Chevauchement = "+ _sc.getChevauchement()+" ; amplitude = "+_sc.getAmplitude());
	}

	private void createTestCourse() {
		String idu = "IDU_TEST";
		int id = 0;
		String nom = "Course Test";
		_points = null;
		int depart = 10 * (60 * 60) + 15 * (60);// à 10h15
		String destination = "Destination course";

		Course course = new Course(true, idu, id, nom, depart, destination);
		_course = new Course(true, course, _points);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		_sc.stop();
		System.out.println("----------------------------");
	}

	public void testGetIndexTrancheIncrIntInt1() {
		assertEquals(T0, _sc.getIndexIncr(0, 0));
	}

	public void testGetIndexTrancheIncrIntInt2() {
		// retard = chevauchement
		assertEquals(T0, _sc.getIndexIncr(0, _sc.getChevauchement()));
	}

	public void testGetIndexTrancheIncrIntInt3() {
		// retard = chevauchement+1
		assertEquals(T0+1, _sc.getIndexIncr(0, _sc.getChevauchement() + 1));
	}

	public void testGetIndexTrancheIncrIntInt4() {
		// Retard tres grand
		assertEquals(511, _sc.getIndexIncr(0, 10000));
	}

	public void testGetIndexTrancheIncrIntInt5() {
		// avance = amplitude -chevauchement +1
		int a = _sc.getAmplitude() -_sc.getChevauchement();
		assertEquals(T0-1, _sc.getIndexIncr(0, -a+1));
	}

	public void testGetIndexTrancheIncrIntInt6() {
		// avance = amplitude -chevauchement
		int a = _sc.getAmplitude() -_sc.getChevauchement();
		assertEquals(T0-1, _sc.getIndexIncr(0,-a));
	}

	public void testGetIndexTrancheIncrIntInt7() {
		// avance tres grande
		assertEquals(0, _sc.getIndexIncr(0, -10000));
	}

	public void testGetIndexTrancheIncrIntInt8() {
		assertEquals(T0, _sc.getIndexIncr(0, 0));
		assertEquals(T0, _sc.getIndexIncr(T0, _sc.getChevauchement()-1));
	}

	public void testGetIndexTrancheDecrIntInt1() {
		assertEquals(T0, _sc.getIndexDecr(511, 0));
	}

	public void testGetIndexTrancheDecrIntInt2() {
		// retard = chevauchement
		assertEquals(T0+1, _sc.getIndexDecr(511, _sc.getChevauchement()));
	}

	public void testGetIndexTrancheDecrIntInt3() {
		// retard = chevauchement+1
		assertEquals(T0+1, _sc.getIndexDecr(511,_sc.getChevauchement() + 1));
	}

	public void testGetIndexTrancheDecrIntInt4() {
		// Retard tres grand
		assertEquals(511, _sc.getIndexDecr(511, 10000));
	}

	public void testGetIndexTrancheDecrIntInt5() {
		// avance = amplitude -chevauchement +1
		int a = _sc.getAmplitude() -_sc.getChevauchement();
		assertEquals(T0, _sc.getIndexDecr(511, -a+1));
	}

	public void testGetIndexTrancheDecrIntInt6() {
		// avance = amplitude -chevauchement
		int a = _sc.getAmplitude() -_sc.getChevauchement();
		assertEquals(T0-1, _sc.getIndexDecr(511,-a));
	}

	public void testGetIndexTrancheDecrIntInt7() {
		// avance tres grande
		assertEquals(0, _sc.getIndexDecr(0, -10000));
	}
	public void testGetIndexTrancheDecrIntInt8() {
		assertEquals(T0, _sc.getIndexDecr(511, 0));
		assertEquals(T0, _sc.getIndexDecr(T0, _sc.getChevauchement()-1));
	}

}
