package org.avm.elementary.management.addons.command;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.elementary.management.addons.AbstractCommand;
import org.avm.elementary.management.addons.Command;
import org.avm.elementary.management.addons.CommandException;
import org.avm.elementary.management.addons.ManagementService;
import org.avm.elementary.management.core.utils.DataUploadClient;
import org.avm.elementary.management.core.utils.Utils;
import org.osgi.framework.BundleContext;

/**
 * Commande teledechargement de fichiers sur un serveur ftp
 * 
 * @author didier.lallemand@mercur.fr
 */
class UploadCommand extends AbstractCommand {



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
			boolean remove = Boolean.valueOf(
					parameters.getProperty("remove", "false")).booleanValue();
			boolean force = Boolean.valueOf(
					parameters.getProperty("force", "false")).booleanValue();
			String localFilename = parameters.getProperty(PROP_FILEPATH);
			if (localFilename != null){
				if (localFilename.startsWith("/") == false){
					localFilename = System.getProperty("org.avm.home") + "/data/" + localFilename;
				}
				out.println("File to upload : " + localFilename);
			}
			
			trace(out, "remove files:" + remove);
			trace(out, "force copy:" + force);
			URL u = management.getCurrentUploadUrl();
			if (u == null){
				throw new Exception("Upload URL not set.");
			}

			String surl = Utils.formatURL(u.toString(), false);
	
			URL url = new URL(surl);

			upload(out, localFilename, url, remove, force);

		} catch (Exception e) {
			trace(out, e.getMessage());
			throw new CommandException(e.getMessage());
		}
	}

	private void upload(PrintWriter out, String filepath, URL urlRemoteDir,
			boolean removeAfterCopy, boolean forceCopy) throws IOException {
		File[] files = _upload(out, filepath, urlRemoteDir, removeAfterCopy, forceCopy);
		
		
		if (files != null ){
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < files.length; i++) {
				buf.append(files[i].getName());
				buf.append("\n");
				
			}
			trace(out, "Uploading :");
			trace(out, buf.toString());
			
			if (urlRemoteDir.getProtocol().equalsIgnoreCase("ftp")){
				String remotefilename  = Utils.getRemoteFilename(null, ".ready");
				copy(buf, urlRemoteDir, remotefilename, out);
			}
			
		}
			
		
		

	}

	private void trace(PrintWriter out, String trace) {
		out.println(trace);
		if (_log.isDebugEnabled()) {
			_log.debug(trace);
		}
	}
	


	private File[] _upload(PrintWriter out, String filepath, URL urlRemoteDir,
			boolean removeAfterCopy, boolean forceCopy) throws IOException {

		File file = new File(filepath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < files.length; i++) {
				buf.append(files[i].getName());
				buf.append("\n");
				if (files[i].isFile() && (forceCopy || isOlderThanToday(files[i])) ) {
					_upload(out, files[i].getAbsolutePath(), urlRemoteDir,
							removeAfterCopy, forceCopy);
				}
			}
			return files;
		} else {

			String localfilename = file.getAbsolutePath();
			String remotefilename = Utils.getRemoteFilename(file, file.getName());
			
			long t = System.currentTimeMillis();

			copy(localfilename, urlRemoteDir, remotefilename);
			trace(out, "File '" + localfilename + "' copied in "
					+ (System.currentTimeMillis() - t) + " ms.");

			if (removeAfterCopy) {
				if (isOlderThanToday(file)) {
					file.delete();
					trace(out, "File '" + file + "' removed.");
				} else {
					trace(out, "File '" + file + "' NOT removed.");
				}
			}
			return new File[]{file};
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

	private void copy(String localfile, URL url, String remotefilename) throws IOException {
		try {
			File file = new File(localfile);
			DataUploadClient client = new DataUploadClient(url);
			if (remotefilename==null){
				remotefilename = file.getName();
			}
			client.put(file, remotefilename);

		} catch (IOException e) {
			System.err
					.println("[Management Upload] Erreur pendant la copie de " + localfile + " vers "
							+ url + " dans " + remotefilename + ":" + e.getMessage() );
			throw e;
		}
	}
	
	private void copy(StringBuffer buffer, URL url, String remotefilename, PrintWriter out) throws IOException {
		try {
			DataUploadClient client = new DataUploadClient(url);
			String result = client.put(buffer, remotefilename);
			out.println("Server:" + result);
		} catch (IOException e) {
			System.err
			.println("[Management Upload] Erreur pendant la copie de " + buffer + " vers "
					+ url + " dans " + remotefilename);
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
