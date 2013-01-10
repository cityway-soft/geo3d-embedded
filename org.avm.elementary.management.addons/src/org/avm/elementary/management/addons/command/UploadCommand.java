package org.avm.elementary.management.addons.command;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementService;
import org.avm.elementary.management.addons.Utils;
import org.osgi.framework.BundleContext;

/**
 * Commande teledechargement de fichiers sur un serveur ftp
 * 
 * @author didier.lallemand@mercur.fr
 */
class UploadCommand extends AbstractCommand {

	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat HF = new SimpleDateFormat("HHMMss");

	public static final String PROP_FILEPATH = "filepath";

	private Logger _log = Logger.getInstance(ManagementService.class);

	/**
	 * @param context
	 * @param parameters
	 * <br>
	 *            Proprietes specifiques:
	 *            <ul>
	 *            <li><code>"keys"</code> : liste des cl&eacute;s dont il faut
	 *            retourner la valeur. <br>
	 *            Chaque cl&eacute; est s&eacute;par&eacute;e par une virgule. <br>
	 *            Ex: <code>keys=java.home,mavariableenv</code>
	 *            <li><code>"report"</code> [true/false] : envoi du resultat de
	 *            la commande sur le site ftp
	 *            </ul>
	 * @param out
	 * @param management
	 * @throws CommandException
	 * @throws IOException
	 */
	public void execute(BundleContext context, Properties parameters,
			PrintWriter out, ManagementService management)
			throws CommandException {

		try {
			String localFilename = parameters.getProperty(PROP_FILEPATH);
			boolean remove = Boolean.valueOf(
					parameters.getProperty("remove", "false")).booleanValue();
			boolean force = Boolean.valueOf(
					parameters.getProperty("force", "false")).booleanValue();
			trace(out, "remove files:" + remove);
			trace(out, "force copy:" + force);
			String url = Utils.formatURL(management.getUploadURL().toString());
			upload(out, localFilename, url, remove, force);

		} catch (Exception e) {
			throw new CommandException(e.getMessage());
		}
	}

	private void upload(PrintWriter out, String filepath, String remotedir,
			boolean removeAfterCopy, boolean forceCopy) throws IOException {
		_upload(out, filepath, remotedir, removeAfterCopy, true, forceCopy);
	}

	private void trace(PrintWriter out, String trace) {
		out.println(trace);
		if (_log.isDebugEnabled()) {
			_log.debug(trace);
		}
	}

	private void _upload(PrintWriter out, String filepath, String remotedir,
			boolean removeAfterCopy, boolean notify, boolean forceCopy) throws IOException {

		File file = new File(filepath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < files.length; i++) {
				buf.append(files[i].getName());
				buf.append("\n");
				if (files[i].isFile() && (forceCopy || isOlderThanToday(files[i])) ) {
					_upload(out, files[i].getAbsolutePath(), remotedir,
							removeAfterCopy, false, forceCopy);
				}
			}
			trace(out, "Uploading :");
			trace(out, buf.toString());
			if (files.length > 0) {
				ByteArrayInputStream in = new ByteArrayInputStream(buf
						.toString().getBytes());
				URL url = new URL(remotedir + "/"
						+ System.getProperty("org.avm.exploitation.id") + "_"
						+ System.getProperty("org.avm.vehicule.id") + ".ready");

				copy(in, url);
			}
		} else {
			String filename = file.getName();
			InputStream in = new BufferedInputStream(new FileInputStream(
					filepath));
			
			Date modified = new Date(file.lastModified());
			URL url = new URL(remotedir + "/"
					+ System.getProperty("org.avm.exploitation.id") + "_"
					+ System.getProperty("org.avm.vehicule.id") + "_"
					+ "M"+ DF.format(modified) + "-" + HF.format(modified)
					+ "R" + DF.format(new Date()) 
					+ "_" + filename);
			long t = System.currentTimeMillis();
			copy(in, url);
			trace(out, "File '" + filename + "' copied in "
					+ (System.currentTimeMillis() - t) + " ms.");

			if (removeAfterCopy) {
				if (isOlderThanToday(file)) {
					file.delete();
					trace(out, "File '" + file + "' removed.");
				} else {
					trace(out, "File '" + file + "' NOT removed.");
				}
			}

			if (notify == true) {
				trace(out, "Uploading :" + filename);
				ByteArrayInputStream in2 = new ByteArrayInputStream(filename
						.getBytes());
				url = new URL(remotedir + "/"
						+ System.getProperty("org.avm.exploitation.id") + "_"
						+ System.getProperty("org.avm.vehicule.id") + ".ready");
				copy(in2, url);
			}
		}
	}
	
	
	
	private boolean isOlderThanToday(File file){
		Date modifiedDate = new Date(file.lastModified());
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		Date today = cal.getTime();
		return modifiedDate.before(today);
	}

	private void copy(InputStream in, URL url) throws IOException {
		try {
			OutputStream os;
			if (url.getProtocol().equals("ftp")) {
				URLConnection connection = url.openConnection();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.out.println("writeFTPReport sleep interrupted !");
				}

				connection.setDoOutput(true);

				os = connection.getOutputStream();

			} else {
				String filename = url.getFile();
				File file = new File(filename);
				file = file.getParentFile();
				if (!file.exists()) {
					file.mkdirs();
				}
				os = new FileOutputStream(filename, false);
			}
			BufferedOutputStream out = new BufferedOutputStream(os);

			int c;
			while ((c = in.read()) > -1) {
				out.write(c);
			}

			out.flush();
			out.close();
		} catch (IOException e) {
			System.err
					.println("[Management Upload] Erreur pendant la copie  : "
							+ url);
			throw e;
		}

	}

	public static class UploadpCommandFactory extends CommandFactory {
		protected Command create() {
			return new UploadCommand();
		}
	}

	static {
		CommandFactory.factories.put(UploadCommand.class.getName(),
				new UploadpCommandFactory());
	}

}
