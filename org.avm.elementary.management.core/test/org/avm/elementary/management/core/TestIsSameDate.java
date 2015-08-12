package org.avm.elementary.management.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.avm.elementary.management.core.utils.Utils;

public class TestIsSameDate extends TestCase {

	public void debug(String string) {
		System.out.println(string);
	}

	public void testNotSame() {

		File f1 = new File("/tmp/testissamedate-f1.txt");
		FileOutputStream os;
		try {
			os = new FileOutputStream(f1);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -3);
			f1.setLastModified(cal.getTimeInMillis());
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File f2 = new File("/tmp/testissamedate-f2.txt");
		try {
			os = new FileOutputStream(f2);
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		debug("File 1 :" + f1.getName() + " modified:"
				+ new Date(f1.lastModified()));

		debug("File 2 :" + f2.getName() + " modified:"
				+ new Date(f2.lastModified()));

		boolean result = Utils.isSameDate(f1, f2);
		assertFalse(result);
	}
	
	public void testSame1() {

		File f1 = new File("/tmp/testissamedate-f1.txt");
		FileOutputStream os;
		try {
			os = new FileOutputStream(f1);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 5);
			f1.setLastModified(cal.getTimeInMillis());
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File f2 = new File("/tmp/testissamedate-f2.txt");
		try {
			os = new FileOutputStream(f2);
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		debug("File 1 :" + f1.getName() + " modified:"
				+ new Date(f1.lastModified()));

		debug("File 2 :" + f2.getName() + " modified:"
				+ new Date(f2.lastModified()));

		boolean result = Utils.isSameDate(f1, f2);
		assertTrue(result);
	}
	
	public void testSame2() {

		File f1 = new File("/tmp/testissamedate-f1.txt");
		FileOutputStream os;
		try {
			os = new FileOutputStream(f1);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 5);
			cal.add(Calendar.SECOND, -3);
			f1.setLastModified(cal.getTimeInMillis());
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File f2 = new File("/tmp/testissamedate-f2.txt");
		try {
			os = new FileOutputStream(f2);
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		debug("File 1 :" + f1.getName() + " modified:"
				+ new Date(f1.lastModified()));

		debug("File 2 :" + f2.getName() + " modified:"
				+ new Date(f2.lastModified()));

		boolean result = Utils.isSameDate(f1, f2);
		assertTrue(result);
	}

}
