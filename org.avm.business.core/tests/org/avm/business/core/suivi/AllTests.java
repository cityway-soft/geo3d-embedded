package org.avm.business.core.suivi;


import org.avm.business.core.suivi.SuiviCourseTest1;
import org.avm.business.core.suivi.SuiviCourseTest2;
import org.avm.business.core.suivi.SuiviCourseTest3;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.avm.business.core.suivi");
		//$JUnit-BEGIN$
		suite.addTestSuite(SuiviCourseTest1.class);
		suite.addTestSuite(SuiviCourseTest2.class);
		suite.addTestSuite(SuiviCourseTest3.class);
		//$JUnit-END$
		return suite;
	}

}
