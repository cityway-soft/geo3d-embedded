package org.avm.elementary.management.core;

import junit.framework.TestCase;

import org.avm.elementary.management.core.utils.Utils;

public class TestVersion extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	public void test1() {

		String version = "1.2.3.20141208";

		String generated = Utils.getVersion(version);

		String expected = "1.00200320141208";
		// double expected = 1.2320141208d;
		debug("version=" + version + "   => generated:" + generated
				+ ", expected:" + expected);
		assertEquals(expected, generated);
	}

	public void test2() {

		String v1 = "1.0.10.20141208";

		String v2 = "1.0.9.20141108";

		String g1 = Utils.getVersion(v1);
		String g2 = Utils.getVersion(v2);

		String e1 = "1.00001020141208";
		String e2 = "1.00000920141108";

		debug("v1=" + v1 + "   => g1:" + g1 + ", e1:" + e1);
		debug("v2=" + v2 + "   => g2:" + g2 + ", e2:" + e2);
		assertEquals(e1, g1);
		assertEquals(e2, g2);
		assertTrue(g1.compareTo(g2) == 1);
	}

	
	public void test2b() {

		String v1 = "1.0.10.20141208";

		String v2 = "1.0.10.20141208";

		String g1 = Utils.getVersion(v1);
		String g2 = Utils.getVersion(v2);

		String e1 = "1.00001020141208";
		String e2 = "1.00001020141208";

		debug("v1=" + v1 + "   => g1:" + g1 + ", e1:" + e1);
		debug("v2=" + v2 + "   => g2:" + g2 + ", e2:" + e2);
		assertEquals(e1, g1);
		assertEquals(e2, g2);
		assertTrue(g1.compareTo(g2) == 0);
	}
//	public void test3() {
//
//		String version = "1.2222.3.20141208";
//
//		String generated = Utils.getVersion(version);
//
//		String expected = "3.22200320141208";
//		// double expected = 1.2320141208d;
//		debug("version=" + version + "   => generated:" + generated
//				+ ", expected:" + expected);
//		assertEquals(expected, generated);
//	}
//
//	public void test4() {
//
//		String version = "1.0.2222.20141208";
//
//		String generated = Utils.getVersion(version);
//
//		String expected = "1.00222220141208";
//		// double expected = 1.2320141208d;
//		debug("version=" + version + "   => generated:" + generated
//				+ ", expected:" + expected);
//		assertEquals(expected, generated);
//	}

	public void test5() {

		String version = "1.0.0.201412111656";

		String generated = Utils.getVersion(version);

		String expected = "1.000000201412111656";
		// double expected = 1.2320141208d;
		debug("version=" + version + "   => generated:" + generated
				+ ", expected:" + expected);
		assertEquals(expected, generated);
	}

	public void test6() {

		String version = "1.0.0.20141211165688";

		String generated = Utils.getVersion(version);

		String expected = "1.00000020141211165688";
		// double expected = 1.2320141208d;
		debug("version=" + version + "   => generated:" + generated
				+ ", expected:" + expected);
		assertEquals(expected, generated);
	}

	public void test7() {

		String version1 = "1.0.0.20150107165743";

		String generated1 = Utils.getVersion(version1);

		String version2 = "1.0.0.20150107170229";

		String generated2 = Utils.getVersion(version2);

		// double expected = 1.2320141208d;
		debug("version1=" + version1 + "   => generated1:" + generated1);
		debug("version2=" + version1 + "   => generated2:" + generated2);
		assertTrue(generated1.compareTo(generated2) == -1);
	}

	public void test8() {

		String version = "1.999.999.29991231235959";

		String generated = Utils.getVersion(version);

		String expected = "1.99999929991231235959";
		debug("version=" + version + "   => generated:" + generated
				+ ", expected:" + expected);
		assertEquals(expected, generated);
	}
	
	
	public void test9() {

		String version = "2.201504011617.014";

		String generated = Utils.getVersion(version);

		String expected = "2.2015040116170140";
		debug("version=" + version + "   => generated:" + generated
				+ ", expected:" + expected);
		assertEquals(expected, generated);
	}

}
