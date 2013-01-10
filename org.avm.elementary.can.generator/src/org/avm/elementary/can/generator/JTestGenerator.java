package org.avm.elementary.can.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JTestGenerator extends Generator {

	private static final String PATTERN_STRING = "\\$\\{(\\w+)\\}";

	private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

	private static final MessageFormat fmtBeginPGN = new MessageFormat(
			"\tpublic void test{0}() throws Exception '{'\n\t\t{0} pgn = new {0}();\n\t\tlong v = 0;\n");
	private static final MessageFormat fmtBegin1SPN = new MessageFormat(
			"\t\tv = (long) (((({1,number,0.0#########} - {0,number,0.0#########}) * Math.random() + {0,number,0.0#########})- {3,number,0.0#########}) / {2,number,0.0#########});\n",
			Locale.US);
	private static final MessageFormat fmtBegin2SPN = new MessageFormat(
			"\t\tpgn.{0}.value = ({1,number,0.0#########} * v + {2,number,0.0#########});\n",
			Locale.US);

	private static final MessageFormat fmtEndPGN = new MessageFormat(
			"\t\ttestPGN(pgn);\n\t'}'\n");

	protected String pkg;

	private StringBuffer tests;

	private StringBuffer test;

	private File root;

	public JTestGenerator() {
		super();
	}

	protected void beginCAN(CAN can) {
		System.out.println("[CATALOG]" + Catalog.getInstance());
		System.out.println("[BEGIN CAN]" + can);
		tests = new StringBuffer();
		pkg = "org.avm.elementary.can.parser." + _name.toLowerCase();
	}

	protected void endCAN(CAN can) {
		System.out.println("[END CAN]" + can);

		StringBuffer buffer = null;
		try {
			buffer = processTestCaseTemplate(pkg, tests.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		write("PGNTest", buffer);
	}

	protected void beginPGN(PGN pgn) {
		System.out.println("[BEGIN PGN]" + pgn);
		test = new StringBuffer();
		test.append(fmtBeginPGN.format(new Object[] { "PGN_" + pgn.getId() }));
	}

	protected void endPGN(PGN pgn) {
		System.out.println("[END PGN]" + pgn);
		test.append(fmtEndPGN.format(new Object[] {}));
		tests.append(test);
		tests.append('\n');
	}

	protected void beginSPN(SPN spn) {
		System.out.println("[BEGIN SPN]" + spn);

		Slot slot = Catalog.getInstance().get(spn.getSlot());

		double min = slot.getRangeMin();
		double max = slot.getRangeMax();
		double scale = slot.getScaling();
		double offset = slot.getOffset();
		test.append(fmtBegin1SPN.format(new Object[] { min, max , scale, offset}));
		test.append(fmtBegin2SPN.format(new Object[] {
				spn.getId().toLowerCase(), scale, offset}));
	}

	protected void endSPN(SPN spn) {
		System.out.println("[END SPN]" + spn);
	}

	private void write(String clazz, StringBuffer buffer) {
		try {
			File root = getPackage(pkg);
			File fd = new File(root, clazz + ".java");
			PrintStream writer = new PrintStream(fd);
			writer.println(buffer);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File getPackage(String packageName) {
		if (root == null) {
			String path = packageName.replace(".", File.separator);
			root = new File(_target + File.separator + path);
			root.mkdirs();
		}
		return root;
	}

	private StringBuffer processTestCaseTemplate(String pkg, String tests)
			throws Exception {

		StringBuffer template = loadTemplate("_PGNTest_.txt");

		Matcher matcher = PATTERN.matcher(template);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String var = matcher.group();
			if (var.equals("${package}")) {
				matcher.appendReplacement(result, pkg);
			} else if (var.equals("${test}")) {
				matcher.appendReplacement(result, tests);
			}
		}
		matcher.appendTail(result);
		return result;
	}

	private StringBuffer loadTemplate(String name) throws Exception {
		StringBuffer buffer = new StringBuffer();

		URL url = this.getClass().getClassLoader().getResource(name);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url
				.openStream()));

		String text = null;
		while ((text = reader.readLine()) != null) {
			buffer.append(text);
			buffer.append('\n');
		}
		return buffer;
	}

}