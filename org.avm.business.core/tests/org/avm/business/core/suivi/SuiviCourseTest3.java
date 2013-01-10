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
 * Meme chose que test2 mais avec d'autres valeurs de Chevauchement et Amplitude
 * 
 * 
 * Cas 1 : Le véhicule est à l'heure. L'écart calculé est plus petit que
 * l'amplitude et est dans la tranche T0.
 * 
 * Le véhicule progresse et prend du retard au delà  de l'amplitude ; il passe
 * ainsi dans la tranche suivante T1 : cas 2. Un message d'avance-retard est
 * envoyé.
 * 
 * Il continue à prendre du retard toute en restant dans l'amplitude : cas 3. On
 * n'envoie pas de message d'avance-retard.
 * 
 * Il continue à prendre du retard et on passe dans la tranche T2 : cas 4. Un
 * message d'avance-retard est envoyé.
 * 
 * Il rattrape son retard et repasse dans la tranche T1 mais dans la zone de
 * chevauchement, en fait il n'est pas sorti de la tranche 2 : cas 5. On
 * n'envoie pas de message d'avance-retard.
 * 
 * Il continue à rattraper son retard, reste dans la tranche T1 mais a dépassé
 * le chevauchement (est sorti de la tranche T2) : cas 6. Un message
 * d'avance-retard est envoyé.
 * 
 * Il rattrape tellement son retard qu'il est en avance : cas 7. Un message
 * d'avance-retard est envoyé car on est sorti de la tranche T1.
 * 
 * Il continue à prendre de l'avance et passe dans la tranche suivante : cas 8.
 * Un message d'avance-retard est envoyé (on est sorti de T0).
 * 
 * Il perd son avance : cas 9. On n'envoie pas de message d'avance-retard.
 * 
 * Il continue à perdre son avance et sort de T-1 : cas 10. Un message
 * d'avance-retard est envoyé.
 */
public class SuiviCourseTest3 extends TestCase {
	private SuiviCourse _sc;

	private Course _course;

	private Point[] _points = null;

	private int _oldAR;

	/**
	 * @param arg0
	 */
	public SuiviCourseTest3(String arg0) {
		super(arg0);
//		_oldAR = 0;
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
		_sc.setChevauchement(new Integer(15));
		_sc.setMaxTranche(new Integer(256));
		_sc.setPeriodiciteTraitement(new Integer(1000));
		_sc.start();
		System.out.println("test scenario");
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
		System.out.println("Fin test scenario");
	}

	public void testIsNecessarySendAR(){
		isNecessarySendARCas1();
		isNecessarySendARCas2();
		isNecessarySendARCas3();
		isNecessarySendARCas4();
		isNecessarySendARCas5();
		isNecessarySendARCas6();
		isNecessarySendARCas7();
		isNecessarySendARCas8();
		isNecessarySendARCas9();
		isNecessarySendARCas10();
	}
	/**
	 * Cas 1 : Le véhicule est à l'heure. L'écart calculé est plus petit que
	 * l'amplitude et est dans la tranche T0.
	 */
	private void isNecessarySendARCas1() {
		_oldAR = 0;
		System.out.println("cas 1. ar = O ; oldAR = "+_oldAR);
		assertFalse(_sc.isNecessarySendAR(0, 0));

	}

	/**
	 * Le véhicule progresse et prend du retard au delà  de l'amplitude ; il
	 * passe ainsi dans la tranche suivante T1 : cas 2. Un message
	 * d'avance-retard est envoyé.
	 */
	private void isNecessarySendARCas2() {
		int ar = _sc.getAmplitude() + 2;
		System.out.println("cas 2. ar = "+ar+" ; oldAR = "+_oldAR);
		assertTrue(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}

	/**
	 * Il continue à prendre du retard toute en restant dans l'amplitude : cas
	 * 3. On n'envoie pas de message d'avance-retard.
	 */
	private void isNecessarySendARCas3() {
		int ar = _oldAR + 1;
		System.out.println("cas 3. ar = "+ar+" ; oldAR = "+_oldAR);
		assertFalse(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}

	/**
	 * Il continue à prendre du retard et on passe dans la tranche T2 : cas 4.
	 * Un message d'avance-retard est envoyé.
	 */
	private void isNecessarySendARCas4() {
		int ar = _sc.getAmplitude()*2;
		System.out.println("cas 4. ar = "+ar+" ; oldAR = "+_oldAR);
		assertTrue(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}

	/**
	 * Il rattrape son retard et repasse dans la tranche T1 mais dans la zone de
	 * chevauchement, en fait il n'est pas sorti de la tranche 2 : cas 5. On
	 * n'envoie pas de message d'avance-retard.
	 */
	private void isNecessarySendARCas5() {
		int ar = 2*_sc.getAmplitude() - _sc.getChevauchement() + 2;
		System.out.println("cas 5. ar = "+ar+" ; oldAR = "+_oldAR);
		assertFalse(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}

	/**
	 * Il continue à rattraper son retard, reste dans la tranche T1 mais a
	 * dépassé le chevauchement (est sorti de la tranche T2) : cas 6. Un message
	 * d'avance-retard est envoyé.
	 */
	private void isNecessarySendARCas6() {
		int ar = 2*_sc.getAmplitude() - 2*_sc.getChevauchement();
		System.out.println("cas 6. ar = "+ar+" ; oldAR = "+_oldAR);
		assertTrue(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}

	/**
	 * Il rattrape tellement son retard qu'il est en avance : cas 7. Un message
	 * d'avance-retard est envoyé car on est sorti de la tranche T1.
	 */
	private void isNecessarySendARCas7() {
		int ar = -(_sc.getAmplitude() - (_sc.getChevauchement() * 2) + 1);
		System.out.println("cas 7. ar = "+ar+" ; oldAR = "+_oldAR);
		assertTrue(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}

	/**
	 * Il continue à prendre de l'avance et passe dans la tranche suivante : cas
	 * 8. Un message d'avance-retard est envoyé (on est sorti de T0).
	 */
	private void isNecessarySendARCas8() {
		int ar = -_sc.getAmplitude();
		System.out.println("cas 8. ar = "+ar+" ; oldAR = "+_oldAR);
		assertTrue(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}

	/**
	 * Il perd son avance : cas 9. On n'envoie pas de message d'avance-retard.
	 */
	private void isNecessarySendARCas9() {
		int ar = -(_sc.getAmplitude() - _sc.getChevauchement() - 1);
		System.out.println("cas 9. ar = "+ar+" ; oldAR = "+_oldAR);
		assertFalse(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}

	/**
	 * Il continue à perdre son avance et sort de T-1 : cas 10. Un message
	 * d'avance-retard est envoyé.
	 */

	private void isNecessarySendARCas10() {
		int ar = -1;
		System.out.println("cas 10. ar = "+ar+" ; oldAR = "+_oldAR);
		assertTrue(_sc.isNecessarySendAR(ar, _oldAR));
		_oldAR = ar;
	}
}
