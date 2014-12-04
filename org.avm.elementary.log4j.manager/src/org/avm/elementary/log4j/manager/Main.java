package org.avm.elementary.log4j.manager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
	public String _pattern;
	public String _filenamePart1;
	public String _filenamePart2;

	public static final void main(String[] argc) {

		Main m = new Main();

		m.split("/home/didier/avm-yyyy-MM-dd.log}.gz");

		System.err.println("pattern = " + m._pattern);
		System.err.println("filename.part1 = " + m._filenamePart1);
		System.err.println("filename.part2 = " + m._filenamePart2);

		SimpleDateFormat df = new SimpleDateFormat(m._pattern);
		
		String result= m._filenamePart1 +  df.format(new Date()) + m._filenamePart2;
		System.err.println("result = " + result);

	}

	private void split(String f) {
		int idx1 = f.indexOf("{");
		int idx2 = f.indexOf("}");

		if (idx1 > 0) {
			if (idx2 > 0) {
				_pattern = f.substring(idx1 + 1, idx2);
				_filenamePart1 = f.substring(0, idx1);
				_filenamePart2 = f.substring(idx2 + 1);
			} else {
				_pattern = f.substring(idx1 + 1);
				_filenamePart1 = f.substring(0, idx1);
				_filenamePart2 = "";
			}
		} else {
			_pattern = "";
			_filenamePart1 = f;
			_filenamePart2 = "";
		}

	}
}
