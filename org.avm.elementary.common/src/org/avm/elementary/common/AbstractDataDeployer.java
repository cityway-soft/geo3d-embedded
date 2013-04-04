package org.avm.elementary.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class AbstractDataDeployer implements DataDeployer, BundleActivator,
		Constants {

	public static final String DEPLOYED = "deployed";

	private Logger _log = Logger.getInstance(this.getClass());

	private BundleContext _context;

	private Properties _properties;

	private ServiceRegistration _registration;

	private static final SimpleDateFormat _jdf = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.FRANCE);

	private static final SimpleDateFormat _df = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.FRANCE);

	private static final SimpleDateFormat _sdf = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public Date getJexDateDeb() {
		try {
			return _jdf.parse(_properties.getProperty(JEX_DATE_DEB));
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public Date getJexDateFin() {
		try {
			return _jdf.parse(_properties.getProperty(JEX_DATE_FIN));
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public Date getVdrDateExp() {
		try {
			return _df.parse(_properties.getProperty(VDR_DATE_EXP));
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public String getVdrId() {
		return _properties.getProperty(VDR_ID);
	}

	public String getVdrNom() {
		return _properties.getProperty(VDR_NOM);
	}

	public String getURLConnection() {
		return _properties.getProperty(URL_CONNECTION);
	}

	public boolean isDeployed(String rootPath) {
		String path = rootPath
				+ (rootPath.endsWith(File.separator) ? "" : File.separator)
				+ _sdf.format(getVdrDateExp()) + File.separator + DEPLOYED;
		File file = new File(path.trim());
		return file.exists();
	}

	public void deploy(String rootPath) throws IOException {

		if (isDeployed(rootPath)) {
			_log.info("[DSU] data already deployed to " + rootPath);
			return;
		}

		// creation du repertoire
		String path = rootPath
				+ (rootPath.endsWith(File.separator) ? "" : File.separator)
				+ _sdf.format(getVdrDateExp());
		File file = new File(path);
		if (file.exists()) {
			rmdir(file);
		}

		file.mkdir();

		// unzip data.jar
		URL url = null;
		String text = getURLConnection();
		if (text == null) {
			url = _context.getBundle().getEntry("data.jar");
		} else {
			url = new URL(text);
		}

		_log.info("[DSU] data deployed to " + rootPath + " from " + url);
		try {
			unzip(url, file.getAbsolutePath());
			file = new File(path + File.separator + DEPLOYED);
			file.createNewFile();

		} catch (Throwable e) {
			throw new IOException(e.getMessage());
		}

	}

	public void undeploy(String path) {
		rmdir(new File(path));
	}

	public void start(BundleContext context) throws Exception {
		_context = context;
		_log.info("[DSU] load resource from: "
				+ _context.getBundle().getLocation());

		for (Enumeration iter = _context.getBundle().findEntries("/",
				"resource.properties", true); iter.hasMoreElements();) {
			URL url = (URL) iter.nextElement();
			_log.info("[DSU] entry: " + url);
			
			// load resources
			try {
				InputStream in = url.openStream();
				_properties = new Properties();
				_properties.load(in);
			} catch (IOException e) {
				_log.error(e.getMessage());
			}
		}

		// register data deployer
		Properties properties = new Properties();
		String servicePid = _properties.getProperty(SERVICE_PID);
		properties.put(SERVICE_PID, _properties.getProperty(SERVICE_PID));
		_registration = _context.registerService(DataDeployer.class.getName(),
				this, properties);
	}

	public void stop(BundleContext context) throws Exception {

		// unregister data deployer
		_registration.unregister();
	}

	private void unzip(URL url, String rootPath) throws IOException {
		final int BUFFER = 2048;

		BufferedOutputStream dest = null;
		URLConnection conn = url.openConnection();
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(conn
				.getInputStream()));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			_log.info("Extracting: " + entry + " to " + rootPath);
			int count;
			byte data[] = new byte[BUFFER];

			// write the files to the disk
			String path = rootPath
					+ (rootPath.endsWith(File.separator) ? "" : File.separator)
					+ entry.getName();

			FileOutputStream fos = new FileOutputStream(path);
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((count = zis.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
		}
		zis.close();
	}

	private void rmdir(File dir) {

		if (dir.isDirectory()) {
			String[] list = dir.list();
			for (int i = 0; i < list.length; i++) {
				File f = new File(dir, list[i]);
				rmdir(f);
			}
			_log.debug("Remove " + dir + " directory");
			dir.delete();
			return;
		}

		_log.debug("Remove " + dir + " file");
		dir.delete();
		return;
	}

}
