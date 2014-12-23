package org.avm.elementary.management.core;

import org.avm.elementary.management.core.utils.Utils;

import junit.framework.TestCase;

public class TestVersion extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	public void test1() {
		
		String version="1.2.3.20141208";
		
		double generated = Utils.getVersion(version);

		double expected = 1.00200320141208d;
		//double expected = 1.2320141208d;
		debug("version=" + version + "   => generated:" + generated + ", expected:"+expected);
		assertEquals(Double.toString(expected), Double.toString(generated));
	}
	
	public void test2() {
		
		String v1="1.0.10.20141208";
		
		String v2="1.0.9.20141108";
		
		double g1 = Utils.getVersion(v1);
		double g2 = Utils.getVersion(v2);


		double e1 = 1.00001020141208;
		double e2 = 1.00000920141108;
		
		
		debug("v1=" + v1 + "   => g1:" + g1 + ", e1:"+e1);
		debug("v2=" + v2 + "   => g2:" + g2 + ", e2:"+e2);
		assertEquals(Double.toString(e1), Double.toString(g1));
		assertEquals(Double.toString(e2), Double.toString(g2));
		assertTrue(g1 > g2);
	}
	
	public void test3() {
		
		String version="1.2222.3.20141208";
		
		double generated = Utils.getVersion(version);

		double expected = 3.22200320141208d;
		//double expected = 1.2320141208d;
		debug("version=" + version + "   => generated:" + generated + ", expected:"+expected);
		assertEquals(Double.toString(expected), Double.toString(generated));
	}
	
	public void test4() {
		
		String version="1.0.2222.20141208";
		
		double generated = Utils.getVersion(version);

		double expected = 1.00222220141208;
		//double expected = 1.2320141208d;
		debug("version=" + version + "   => generated:" + generated + ", expected:"+expected);
		assertEquals(Double.toString(expected), Double.toString(generated));
	}
	
	public void test5() {
		
		String version="1.0.0.201412111656";
		
		double generated = Utils.getVersion(version);

		double expected = 1.000000201412111656;
		//double expected = 1.2320141208d;
		debug("version=" + version + "   => generated:" + generated + ", expected:"+expected);
		assertEquals(Double.toString(expected), Double.toString(generated));
	}
	
	public void test6() {
		
		String version="1.0.0.20141211165688";
		
		double generated = Utils.getVersion(version);

		double expected = 1.00000020141211165688;
		//double expected = 1.2320141208d;
		debug("version=" + version + "   => generated:" + generated + ", expected:"+expected);
		assertEquals(Double.toString(expected), Double.toString(generated));
	}


}
