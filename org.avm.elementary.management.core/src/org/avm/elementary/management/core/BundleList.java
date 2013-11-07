package org.avm.elementary.management.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.avm.elementary.management.core.utils.DataUploadClient;
import org.avm.elementary.management.core.utils.MultiMemberGZIPInputStream;

/*
 * Created on 1 sept. 2005
 * Copyright (c) Mercur
 */

public class BundleList {
	public static final String FILENAME = "bundles.list";
	public static final String COMPRESSED_EXT = ".gz";

	private Hashtable _hashBundle = new Hashtable();

	public BundleList() {

	}

	public void put(String name, BundleProperties bp) {
		_hashBundle.put(name, bp);
	}

	public static void create(String bundlesDir, String bundlesListDir)
			throws IOException {
		BundleList bundleList = null;
		File dir = new File(bundlesDir);

		if (dir.isDirectory()) {
			// String file=dir.getAbsolutePath() + "/" + BUNDLELIST_FILENAME;
			File file = new File(bundlesListDir);
			file = file.getAbsoluteFile();
			if (file.isFile()) {
				file = file.getParentFile();
			}
			String bundlelListFile = file.getAbsolutePath()
					+ System.getProperty("file.separator") + FILENAME;
			bundleList = load(bundlelListFile);
			if (bundleList == null) {
				bundleList = new BundleList();
			}

			bundleList.readDirectory(dir);
			bundleList.save(bundlelListFile);
		} else {
			System.err.println(bundlesDir + " is not a directory");
		}

	}

	public static void compress(String bundlesListDir) throws IOException {
		// String file=dir.getAbsolutePath() + "/" + BUNDLELIST_FILENAME;
		File file = new File(bundlesListDir);
		file = file.getAbsoluteFile();
		if (file.isFile()) {
			file = file.getParentFile();
		}
		String bundleListFile = file.getAbsolutePath()
				+ System.getProperty("file.separator") + FILENAME;

		String bundleListGZFile = file.getAbsolutePath()
				+ System.getProperty("file.separator") + FILENAME
				+ COMPRESSED_EXT;

		// Create the GZIP output stream
		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
				bundleListGZFile));

		// Open the input file
		FileInputStream in = new FileInputStream(bundleListFile);

		// Transfer bytes from the input file to the GZIP output stream
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();

		// Complete the GZIP file
		out.finish();
		out.close();

	}

	public BundleProperties getBundleProperties(String bundleName) {
		return (BundleProperties) _hashBundle.get(bundleName);
	}

	/**
	 * Chargement de la liste des bundles via un InputStream
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private void load(InputStream in) throws IOException {
		BufferedReader inbuf = new BufferedReader(new InputStreamReader(in));
		String line;

		while ((line = inbuf.readLine()) != null) {
			if (line == null || line.equals(""))
				continue;

			BundleProperties bp = new BundleProperties();

			StringTokenizer t = new StringTokenizer(line, ";");
			int startLevel;
			// -- startlevel
			String token = t.nextToken();
			try {
				startLevel = Integer.parseInt(token);
			} catch (NumberFormatException e) {
				System.out.println("[Warning] startlevel '" + token
						+ "' invalid in " + line + " IGNORED !");
				continue;
			}
			// -- name
			String name = t.nextToken();
			String nameOptions = null;
			int idx = name.indexOf("#");
			if (idx != -1) {
				nameOptions = name.substring(idx + 1);
				name = name.substring(0, idx);
			}
			bp.setName(name);
			bp.setNameOptions(nameOptions);

			// -- version
			String version = t.nextToken();
			bp.setVersion(version);

			if (startLevel == 0) {
				System.out.println("[*Warning*] startlevel for bundle <" + name
						+ "> NOT defined !?");
			}
			bp.setStartlevel(startLevel);

			// // -- disabled ?
			// if (t.hasMoreTokens()) {
			// String disabled = t.nextToken();
			// if (disabled != null && disabled.equalsIgnoreCase("disabled")) {
			// System.out.println("[INFO] bundle <"
			// + name + ">  will be DISABLED !");
			// bp.setEnable(false);
			// }
			// }

			// // -- pack ?
			if (t.hasMoreTokens()) {
				String pack = t.nextToken();
				if (pack != null && pack.trim().length() != 0) {
					bp.setPack(pack);
				} else {
					bp.setPack(null);
				}
			}

			updateBundleProperties(bp, false);
		}
	}

	/**
	 * Chargement de la liste des bundles depuis un fichier
	 * 
	 * @param filename
	 * @throws IOException
	 */
	private static BundleList load(String filename) throws IOException {
		URL url = new URL("file://" + filename);
		return load(url);
	}

	public static BundleList load(URL url) throws IOException, ConnectException {

		BundleList bundleList = new BundleList();
		//URLConnection connection = url.openConnection();
		InputStream primaryInputStream;
		
		DataUploadClient client = new DataUploadClient(url);
		primaryInputStream = client.get();

		InputStream in = null;

		if (url.getFile().endsWith(COMPRESSED_EXT)) {
			try {
				in = new MultiMemberGZIPInputStream(primaryInputStream);
			} catch (IOException e) {
				// e.printStackTrace();
				return null;
			}
		} else {
//			try {
				in = primaryInputStream;
//			} catch (IOException e) {
//				// e.printStackTrace();
//				return null;
//			}
		}
		if (in != null) {
			bundleList.load(in);
			in.close();
		}

		return bundleList;
	}

	public Enumeration elements() {
		Vector v = new Vector();
		Enumeration e = _hashBundle.elements();
		while (e.hasMoreElements()) {
			v.add(e.nextElement());
		}
		Object[] tab = v.toArray();

		Arrays.sort(tab, new BundleComparator());
		v = new Vector();
		for (int i = 0; i < tab.length; i++) {
			v.add(tab[i]);
		}
		return v.elements();
	}

	private boolean updateBundleProperties(BundleProperties bp, boolean report) {
		boolean newone = true;
		BundleProperties existingBp = (BundleProperties) _hashBundle.get(bp
				.getCompleteName());
		if (existingBp != null) {
			if (bp.getStartlevel() == BundleProperties.NOT_SET) {
				newone = false;
				if (report && !existingBp.getVersion().equals(bp.getVersion())) {
					System.out.println("[Info] modified Bundle-Name : "
							+ bp.getCompleteName() + "(" + bp.getVersion()
							+ ")");
				}
				bp.setStartlevel(existingBp.getStartlevel());
			}
			// bp.setEnable(existingBp.isEnable());
			bp.setPack(existingBp.getPack());
		} else {
			if (bp.getStartlevel() == BundleProperties.NOT_SET) {
				System.out
						.println("[Warn] StartLevel must be set for bundle : "
								+ bp.getCompleteName() + "   !!!!");
			}
		}
		_hashBundle.put(bp.getCompleteName(), bp);
		return newone;
	}

	private void readDirectory(File dir) {
		FilenameFilter filter = new JarFileFilter();

		File[] children = dir.listFiles(filter);
		for (int i = 0; i < children.length; i++) {
			BundleProperties bp = new BundleProperties();
			try {
				bp.loadProperties(children[i].getAbsolutePath());
				if (bp.getName() == null) {
					System.out.println("[Warning] Bundle-Name null for "
							+ children[i].getAbsolutePath());
					continue;
				}
				updateBundleProperties(bp, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Enumeration e = elements();
		while (e.hasMoreElements()) {
			BundleProperties bp = (BundleProperties) e.nextElement();
			buffer.append(bp);
			buffer.append(System.getProperty("line.separator"));
		}
		return buffer.toString();
	}

	private void save(String filename) {

		Enumeration e = elements();
		PrintWriter out = null;
		try {
			if (filename.endsWith(COMPRESSED_EXT)) {
				out = new PrintWriter(new GZIPOutputStream(
						new FileOutputStream(filename, false)));
			} else {
				out = new PrintWriter(new FileOutputStream(filename, false));
			}
			while (e.hasMoreElements()) {
				BundleProperties bp = (BundleProperties) e.nextElement();
				out.println(bp);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
//		if (args.length != 2) {
//			System.out
//					.println("Usage <bundles repository dir> <bundles list file>");
//		} else {
//			BundleList.create(args[0], args[1]);
//			BundleList.compress(args[1]);
//		}
		
		//BundleList b = BundleList.load(new URL("http://avm:avm++@10.10.1.199:8080/frontal.terminal-manager/rest/terminal/0200001002FF67FD/aton/999/100/download/bundles.list"));
		BundleList b = BundleList.load(new URL("http://avm:avm++@10.10.1.199:8080/frontal.terminal-manager/rest/terminal/0200001002FF67FD/aton/999/100/download/bundles.list.gz"));
		System.out.println("BundleList="+b);
	}

	public class JarFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith(".jar") || name.endsWith(".JAR");
		}
	}

	public class BundleComparator implements java.util.Comparator {

		public int compare(Object o1, Object o2) throws ClassCastException {
			if (!(o1 instanceof BundleProperties)) {
				throw new ClassCastException();
			}
			if (!(o2 instanceof BundleProperties)) {
				throw new ClassCastException();
			}

			BundleProperties c1 = (BundleProperties) o1;
			BundleProperties c2 = (BundleProperties) o2;
			String sl1 = c1.getStartlevel() + c1.getName();

			String sl2 = c2.getStartlevel() + c2.getName();

			return sl1.compareTo(sl2);

		}
	}

	public int size() {
		return _hashBundle.size();
	}

	public void remove(String bundleName) {
		_hashBundle.remove(bundleName);
	}

	public boolean exist(String bundleName) {
		return (_hashBundle.get(bundleName) != null);
	}
	


}