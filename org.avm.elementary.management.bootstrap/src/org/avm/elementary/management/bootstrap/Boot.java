package org.avm.elementary.management.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Dictionary;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class Boot implements BundleActivator {

	private static final String DEBUGTAG = "[DEBUG " + Boot.class.getName()
			+ "] ";

	private static final String ERRORTAG = "[ERROR " + Boot.class.getName()
			+ "] ";

	private boolean DEBUG = true;

	private BundleContext _context;

	public Boot() {

	}

	public void start(BundleContext context) throws Exception {
		Bundle bundle = null;

		_context = context;
		String home = System.getProperty("org.avm.home");
		String bundlename = System.getProperty(this.getClass().getPackage()
				.getName()
				+ ".management-bundle", "org.avm.elementary.management.core");

		bundle = getBundle(bundlename);

		String filename = home + System.getProperty("file.separator") + "lib"
				+ System.getProperty("file.separator") + bundlename + ".jar";

		File file = new File(filename);
		if (checkFile(new File(filename + ".md5")) == false) {
			error("Impossible d'installer " + file.getAbsolutePath());
		} else {

			URL url = new URL("file:///" + file.getAbsolutePath());
			debug("Management core URL :" + url);

			if (bundle == null) {
				bundle = install(bundlename, url);
			} else {

				JarFile jar = new JarFile(file);
				String candidate = jar.getManifest().getMainAttributes().getValue("Bundle-Version");
				
				jar.close();
				Dictionary headers = bundle.getHeaders();
				String currentVersion = (String) headers
						.get("Bundle-Version");
				debug("Current version : " + currentVersion);
				debug("Candidate version : " + candidate);
				
				if (! currentVersion.equals(candidate)) {
					debug("update Management ...");
					update(bundle, url);
				}
				else
				{
					debug("no update of Management");
				}
			}
		}

		try {
			debug("Starting bundle management...");
			bundle.start();
		} catch (Throwable t) {
			error("Erreur lors du demarrage de management.core: "
					+ t.getMessage());
			bundle.uninstall();
		}

	}

	private void update(Bundle bundle, URL url) throws IOException,
			BundleException {
		URLConnection connection = url.openConnection();
		bundle.update(connection.getInputStream());
	}

	private Bundle install(String bundleName, URL url) throws IOException,
			BundleException {
		URLConnection connection = url.openConnection();
		Bundle bundle = _context.installBundle(connection.getURL().toString(),
				connection.getInputStream());

		return bundle;
	}

	protected Bundle getBundle(String bundlename) {

		try {
			Bundle[] bundles = _context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				Bundle bundle = bundles[i];
				String installbundle = (String) bundle.getHeaders().get(
						"Bundle-SymbolicName");

				if (installbundle != null) {
					int idx = bundlename.indexOf(";");
					if (idx != -1) {
						installbundle = installbundle.substring(0, idx);
					}
				}

				if (installbundle != null && installbundle.equals(bundlename)) {
					return bundle;
				}
			}
		} catch (Exception e) {
		}

		return null;
	}

	public void stop(BundleContext context) throws Exception {

	}

	private void debug(String debug) {
		if (DEBUG) {
			System.out.println(DEBUGTAG + debug);
		}
	}

	private void error(String error) {
		System.err.println(ERRORTAG + error);
	}

	// --- MD5 TOOLS ---
	public static String genMD5(File file) {
		MessageDigest md;
		StringBuffer output = null;
		try {
			md = MessageDigest.getInstance("MD5");
			InputStream inStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int read = 0;
			while ((read = inStream.read(buffer)) > 0) {
				md.update(buffer, 0, read);
			}
			byte[] md5sum = md.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			output = new StringBuffer(bigInt.toString(16));

			inStream.close();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (output.length() < 32) {
			output.insert(0, "0");
		}
		return output.toString();
	}

	public static boolean checkFile(File md5file) {
		boolean result = false;

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					md5file.getAbsolutePath()));
			String line = br.readLine();
			int idx = line.indexOf(" ");
			String md5 = line.substring(0, idx).trim();
			String filename = line.substring(idx + 1).trim();

			String genmd5 = genMD5(new File(md5file.getParentFile()
					.getAbsoluteFile() + "/" + filename));

			result = (genmd5 != null && md5.equals(genmd5));
			System.out.println("[BOOTSTRAP] genmd5=" + genmd5 + ", md5=" + md5 + "(check="	+ result + ")");
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static boolean checkVersion(File versionFile, String candidateVersion) {
		boolean result = false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					versionFile.getAbsolutePath()));
			if (br.readLine().equals(candidateVersion)) {
				result = true;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static void saveVersion(String version, File versionFile) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(versionFile);
			fos.write(version.getBytes());
			fos.write('\n');
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
