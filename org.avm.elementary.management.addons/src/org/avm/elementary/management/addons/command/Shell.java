/**
 * @version $Id: Shell.java,v 1.11 2011/07/13 16:37:50 geolia Exp $
 * @author $Author: geolia $
 */
package org.avm.elementary.management.addons.command;

// TODO Auto-generated catch block

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * @author Didier
 * 
 */
public class Shell {

	private SimpleDateFormat DF = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

	private File _currentDir;

	private PrintWriter _out = new PrintWriter(System.out);

	private Logger _log = Logger.getInstance(this.getClass());

	private static Shell _instance;

	private Shell() {
		defaultDir();
	}

	public static Shell getInstance() {
		if (_instance == null) {
			_instance = new Shell();
		}
		return _instance;
	}

	private void defaultDir() {
		_currentDir = new File(System.getProperty("org.avm.home", "/"));
	}

	public void execute(String args) {
		_log.debug("args= " + args);
		StringTokenizer t = new StringTokenizer(args, " ");
		if (t.hasMoreElements()) {
			String cmdname = ((String) t.nextElement()).toLowerCase();
			Method methods[] = this.getClass().getMethods();
			Method cmd = null;
			for (int i = 0; i < methods.length; i++) {
				if (cmdname.equals(methods[i].getName().toLowerCase())) {
					cmd = methods[i];
					break;
				}
			}

			if (cmd != null) {
				Class parameters[] = cmd.getParameterTypes();
				Object obj[] = new Object[parameters.length];
				int j = 0;
				for (j = 0; j < obj.length; j++) {
					if (t.hasMoreElements()) {
						obj[j] = t.nextElement();
					} else {
						obj[j] = null;
					}
				}
				j--;
				if (j > 0) {
					Object val = obj[j];
					if (val != null) {
						StringBuffer buf = new StringBuffer();
						buf.append(val);
						while (t.hasMoreElements()) {
							buf.append(" ");
							buf.append(t.nextElement());
						}
						val = buf.toString();
					} else {
						val = "";
					}
					obj[j] = val;
				}
				try {
					cmd.invoke(this, obj);
				} catch (Exception e) {
					e.printStackTrace();
					if (_out != null) {
						_out.println("Error when executing " + args + " : " + e);
					}
				}
			} else {
				_out.println("No such Shell command : " + cmdname);
			}
		}
	}

	public void ls(String dir) {
		StringBuffer buf = new StringBuffer();

		File[] list;
		File currentDir = _currentDir;
		String sfilter = null;
		File file;

		if (dir != null) {
			file = new File(dir);
			if (file.isDirectory()) {
				currentDir = file;
			} else if (file.getParentFile().isDirectory()) {
				currentDir = file.getParentFile();
				sfilter = file.getName();
			} else {
				sfilter = dir;
			}
		}

		if (sfilter != null) {
			FilenameFilter filter = new MyFilenameFilter(sfilter);
			list = currentDir.listFiles(filter);
		} else {
			list = currentDir.listFiles();
		}

		for (int i = 0; i < list.length; i++) {
			file = list[i];
			buf.append(file.isDirectory() ? "d " : "- ");
			buf.append(DF.format(new Date(file.lastModified())));
			buf.append(" ");
			buf.append(formatFileSize(file));
			buf.append(" ");
			buf.append(file.getName());
			buf.append("\n");
		}
		println(buf.toString());
	}

	private String formatFileSize(File file) {
		long size = file.length();

		if (size < 1024) {
			return size + "o";
		} else {
			size = size / 1024;
			if (size < 1024) {
				return size + "ko";
			} else {
				size = size / 1024;
				return size + "mo";
			}
		}
	}

	public void cd(String dir) {
		if (dir == null) {
			defaultDir();
			return;
		} else if (dir.equals("..")) {
			cd(_currentDir.getParent());
			return;
		}

		File newDir;
		try {
			newDir = getDir(dir);
			_currentDir = new File(newDir.getCanonicalPath());
			println(_currentDir.getAbsolutePath());
		} catch (Exception e) {
			println("Echec :" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void mkdir(String dir) {
		try {
			if (dir.startsWith("/")) {
				File newDir = new File(dir);
				newDir.mkdirs();
			} else {
				String path = _currentDir.getAbsolutePath() + "/" + dir;
				File newDir = new File(path);
				newDir.mkdirs();
			}
		} catch (Exception e) {
			println("Echec : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void pwd() {
		println(_currentDir.getAbsolutePath());
	}

	public void rm(String fichier) {
		File file;
		try {
			file = getFile(fichier);
			file.delete();
			println("Fichier :" + file + " supprime.");
		} catch (Exception e) {
			println("Echec : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void wget(String sremote, String slocal) throws IOException {
		try {
			URL remote = new URL(sremote);
			InputStream in = null;
			String protocol = remote.getProtocol();
			if (protocol.toLowerCase().equals("ftp")) {
				URLConnection connection;
				connection = remote.openConnection();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					_log.error("FTP sleep interrupted !");
				}
				// connection.setDoOutput(true);
				in = connection.getInputStream();
			} else {
				throw new MalformedURLException(
						"Remote URL must start with ftp://");
			}

			File file;
			String filename = null;
			try {
				file = getDir(slocal);

				File remoteFile = new File(remote.getPath());

				filename = file.getAbsolutePath() + "/" + remoteFile.getName();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (filename != null) {
				FileOutputStream os = new FileOutputStream(filename, false);
				byte[] data = new byte[1500];
				int n;
				while ((n = in.read(data)) != -1) {
					os.write(data, 0, n);
				}
				os.flush();
				os.close();
			}
		} catch (IOException e) {
			_log.error("Shell wget exception :", e);
			throw new IOException("Shell wget exception :" + e.getMessage());
		}
	}

	public void cat(String filename) {
		File file;
		try {
			file = getFile(filename);
			DataInputStream zip = new DataInputStream(new FileInputStream(
					file.getAbsoluteFile()));
			String line;
			while ((line = zip.readLine()) != null) {
				println(line);
			}
		} catch (Exception e) {
			println("Echec : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void zcat(String filename) {
		File file;
		try {
			file = getFile(filename);
			DataInputStream zip = new DataInputStream(
					new MultiMemberGZIPInputStream(new FileInputStream(
							file.getAbsoluteFile())));
			String line;
			while ((line = zip.readLine()) != null) {
				println(line);
			}
		} catch (Exception e) {
			println("Echec : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void touch(String filename) throws IOException {
		File file = new File(filename);
		file.createNewFile();
	}

	public void nmap(String host, String port) {
		Socket socket = null;
		try {
			socket = new Socket(host, Integer.parseInt(port));
			_out.println("OK");
		} catch (NumberFormatException e) {
			_out.println("Error (NumberFormatException) : " + e);
		} catch (UnknownHostException e) {
			_out.println("Error (UnknownHostException) : " + e);
		} catch (IOException e) {
			_out.println("Error (IOException) : " + e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					_out.println("Error on close (IOException) : " + e);
				}
			}
		}

	}

	public void ip(String iface) {
		String server = null;
		int port = 0;
		String ip = "unknown";

		if (iface != null) {
			if (iface.equalsIgnoreCase("gprs")) {
				server = "saml.avm.org";
				port = 8094;
			} else if (iface.equalsIgnoreCase("wifi")) {
				server = "ftpserver.avm.org";
				port = 21;
			}

			Socket socket = null;
			try {
				socket = new Socket(server, port);
				InetAddress ia = socket.getLocalAddress();
				ip = ia.getHostAddress();
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		_out.println(iface + ":" + ip);
	}

	public void exec(String name, String args) {
		_log.debug("exec : " + name + " " + args);
		long result = org.avm.device.plateform.System.exec(name, args)
				.longValue();
		_log.debug("exec : " + name + " " + args);
	}

	public void kill(String spid) {
		int pid = Integer.parseInt(spid);
		_log.debug("kill : " + pid);
		long result = org.avm.device.plateform.System.kill(new Long(pid));
		_log.debug("kill returns : " + result);
	}

	private File getFile(String filename) throws Exception {
		File file = null;
		if (filename.startsWith("/")) {
			file = new File(filename);
		} else {
			file = new File(_currentDir.getAbsoluteFile() + "/" + filename);
		}

		if (file.exists() == false) {
			throw new FileNotFoundException(filename
					+ " : ce fichier n'existe pas !");
		} else if (file.isFile() == false) {
			throw new FileNotFoundException(filename
					+ " : n'est pas un fichier regulier !");
		}
		return file;
	}

	private File getDir(String dirname) throws Exception {
		File file = null;
		if (dirname == null) {
			_currentDir = new File(System.getProperty("org.avm.home"));
			return _currentDir;
		}
		if (dirname.startsWith("/")) {
			file = new File(dirname);
		} else {
			file = new File(_currentDir.getAbsoluteFile() + "/" + dirname);
		}

		if (file.exists() == false) {
			throw new FileNotFoundException(dirname
					+ " : ce repertoire n'existe pas !");
		} else if (file.isDirectory() == false) {
			throw new FileNotFoundException(dirname
					+ " : n'est pas un repertoire !");
		}
		return file;
	}

	public void setOutput(PrintWriter out) {
		_out = out;
	}

	private void println(String buf) {
		_log.debug(buf);
		if (_out == null) {
			System.out.println(buf);
		} else {
			_out.println(buf);
		}
	}

	class MyFilenameFilter implements FilenameFilter {
		String _filter;

		public MyFilenameFilter(String filter) {
			_filter = filter;
		}

		public boolean accept(File dir, String filename) {
			if (_filter.startsWith("*")) {
				_filter = " " + _filter;
			}
			if (_filter.endsWith("*")) {
				_filter += " ";
			}
			StringTokenizer t = new StringTokenizer(_filter, "*");
			int i = 0;
			while (t.hasMoreTokens()) {
				String token = t.nextToken().trim();
				if (i == 0) {
					if (filename.startsWith(token) == false) {
						_log.debug("filename doesn't start with " + token);
						return false;
					} else {
						_log.debug("filename start with " + token);
					}
				} else if (t.hasMoreElements() == false) {
					if (filename.endsWith(token) == false) {
						return false;
					}
				} else if (filename.indexOf(token) == -1) {
					return false;
				}
				i++;
			}
			return true;
		}

	}

}