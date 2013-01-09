package org.avm.elementary.can.generator;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.InputSource;

public class GeneratorTask extends Task {

	private String _name;

	private File _srcfile;

	private File _destdir;

	public File getDestdir() {
		return _destdir;
	}

	public void setDestdir(File destdir) {
		_destdir = destdir;
	}

	public File getSrcfile() {
		return _srcfile;
	}

	public void setSrcfile(File srcfile) {
		_srcfile = srcfile;
	}

	public void execute() throws BuildException {

		try {
			System.out.println("[DSU]" + _srcfile);
			System.out.println("[DSU]" + _destdir);
			InputSource spec = new InputSource(new FileReader(_srcfile));

			URL rules = this.getClass().getClassLoader()
					.getResource("rule.xml");
			System.out.println("[DSU]" + rules);
			String target = _destdir.getAbsolutePath();
			new JGenerator().generate(spec, rules, target, _name);
			spec = new InputSource(new FileReader(_srcfile));
			new JTestGenerator().generate(spec, rules, target, _name);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public static void main(String[] args) {
		Logger root = Logger.getRootLogger();
		root.addAppender(new ConsoleAppender(new PatternLayout()));
		root.setLevel(Level.ALL);

		GeneratorTask task = new GeneratorTask();
		task.setName("j1939");
		task.setDestdir(new File(
				"/root/affaires/geolia/avm/elementary/org.avm.elementary.can.generator/build"));
		task.setSrcfile(new File(
				"/root/affaires/geolia/avm/elementary/org.avm.elementary.can.generator/fms.xml"));
		task.execute();
	}

}