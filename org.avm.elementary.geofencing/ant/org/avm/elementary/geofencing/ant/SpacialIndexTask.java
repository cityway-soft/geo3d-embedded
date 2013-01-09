package org.avm.elementary.geofencing.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;

import spatialindex.rtree.RTree;
import spatialindex.spatialindex.ISpatialIndex;
import spatialindex.spatialindex.Region;
import spatialindex.storagemanager.DiskStorageManager;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;
import spatialindex.storagemanager.RandomEvictionsBuffer;

public class SpacialIndexTask extends MatchingTask {

	private File srcfile;

	private Vector filesets = new Vector();

	private Vector balises = new Vector();

	private String destfile;

	private double fillfactor = 0.95d;

	private int leafcapacity = 10;

	private int indexcapacity = 10;

	private int pagesize = 512;

	private int buffercapacity = 3000;

	private DiskStorageManager _disk;

	public SpacialIndexTask() {
		super();
	}

	public void setSrcfile(File srcfile) {
		this.srcfile = srcfile;
	}

	public void addFileset(FileSet set) {
		this.filesets.addElement(set);
	}

	public void setDestfile(String destfile) {
		this.destfile = destfile;
	}

	public void setFillfactor(double fillfactor) {
		this.fillfactor = fillfactor;
	}

	public void setIndexcapacity(int indexcapacity) {
		this.indexcapacity = indexcapacity;
	}

	public void setLeafcapacity(int leafcapacity) {
		this.leafcapacity = leafcapacity;
	}

	public void setBuffercapacity(int buffercapacity) {
		this.buffercapacity = buffercapacity;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public Balise createBalise() {
		Balise balise = new Balise();
		balises.add(balise);
		return balise;
	}

	public void execute() throws BuildException {

		if (srcfile == null && filesets.size() == 0) {
			throw new BuildException("At least one of the file or dir "
					+ "attributes, or a fileset element, " + "must be set.");
		}
		if (destfile == null) {
			throw new BuildException("The destfile must be set.");
		}

		try {
			initialiseSpatialIndex();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

	private void initializeSpatialIndexWGS84(ISpatialIndex tree, int id,
			double x1, double y1, double x2, double y2) {
		final int LON = 0;

		final int LAT = 1;

		double[] low = new double[2];
		low[LON] = x1 / 180d * Math.PI;
		low[LAT] = y1 / 180d * Math.PI;
		double[] high = new double[2];
		high[LON] = x2 / 180d * Math.PI;
		high[LAT] = y2 / 180d * Math.PI;
		Region r = new Region(low, high);
		tree.insertData(null, r, id);
		System.out.println("[DSU] adding balise " + id + " " + r);
	}

	private void initializeSpatialIndex(ISpatialIndex tree)
			throws NumberFormatException, IOException {

		if (srcfile != null && srcfile.exists()) {
			System.out.println("[DSU] initialize spatial index from file "
					+ srcfile);
			indexFile(srcfile, tree);
		}

		if (filesets.size() > 0)
			System.out.println("[DSU] initialize spatial index from filesets "
					+ filesets);

		for (int i = 0; i < filesets.size(); i++) {
			FileSet fs = (FileSet) filesets.elementAt(i);
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] files = ds.getIncludedFiles();
			String[] dirs = ds.getIncludedDirectories();
			indexFiles(fs.getDir(getProject()), files, dirs, tree);
		}

		for (Iterator it = balises.iterator(); it.hasNext();) {
			Balise balise = (Balise) it.next();
			initializeSpatialIndexWGS84(tree, balise.getId(), balise.getX1(),
					balise.getY1(), balise.getX2(), balise.getY2());
		}

		System.out.println("[DSU] spatial index definition : \n" + tree);
	}

	private boolean indexFile(File f, ISpatialIndex tree)
			throws NumberFormatException, IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("#"))
				continue;
			StringTokenizer t = new StringTokenizer(line, ";");
			int id = Integer.parseInt(t.nextToken());
			int idu = Integer.parseInt(t.nextToken());
			double x1 = Double.parseDouble(t.nextToken());
			double y1 = Double.parseDouble(t.nextToken());
			double x2 = Double.parseDouble(t.nextToken());
			double y2 = Double.parseDouble(t.nextToken());

			initializeSpatialIndexWGS84(tree, idu, x1, y1, x2, y2);
		}
		return true;
	}

	private void indexDir(File d, ISpatialIndex tree)
			throws NumberFormatException, IOException {
		String[] list = d.list();
		if (list == null)
			return;

		for (int i = 0; i < list.length; i++) {
			String s = list[i];
			File f = new File(d, s);
			if (f.isDirectory()) {
				continue;
			} else {
				indexFile(f, tree);
			}
		}
	}

	private void indexFiles(File d, String[] files, String[] dirs,
			ISpatialIndex tree) throws NumberFormatException, IOException {
		if (files.length > 0) {
			for (int j = 0; j < files.length; j++) {
				File f = new File(d, files[j]);
				indexFile(f, tree);
			}
		}

		if (dirs.length > 0) {
			for (int j = dirs.length - 1; j >= 0; j--) {
				File dir = new File(d, dirs[j]);
				String[] dirFiles = dir.list();
				if (dirFiles == null || dirFiles.length == 0) {
					indexDir(dir, tree);
				}
			}
		}
	}

	private IStorageManager getStorageManager() throws SecurityException,
			NullPointerException, FileNotFoundException,
			IllegalArgumentException, IOException {
		IStorageManager storage = null;
		PropertySet properties = new PropertySet();
		properties.setProperty("Overwrite", Boolean.TRUE);
		properties.setProperty("FileName", destfile);
		properties.setProperty("PageSize", new Integer(pagesize));
		_disk = new DiskStorageManager(properties);
		storage = new RandomEvictionsBuffer(_disk, buffercapacity, false);

		return storage;
	}

	private void initialiseSpatialIndex() throws SecurityException,
			NullPointerException, FileNotFoundException,
			IllegalArgumentException, IOException {

		RTree tree = null;
		PropertySet ps = new PropertySet();
		ps.setProperty("FillFactor", new Double(fillfactor));
		ps.setProperty("IndexCapacity", new Integer(indexcapacity));
		ps.setProperty("LeafCapacity", new Integer(leafcapacity));

		IStorageManager storage = getStorageManager();

		tree = new RTree(ps, storage);

		initializeSpatialIndex(tree);
		tree.flush();
		_disk.close();
	}

	public class Balise {
		int id;

		double _x1, y1, x2, y2;

		public double getX1() {
			return _x1;
		}

		public void setX1(double x1) {
			_x1 = x1;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public double getX2() {
			return x2;
		}

		public void setX2(double x2) {
			this.x2 = x2;
		}

		public double getY1() {
			return y1;
		}

		public void setY1(double y1) {
			this.y1 = y1;
		}

		public double getY2() {
			return y2;
		}

		public void setY2(double y2) {
			this.y2 = y2;
		}

	}

}
