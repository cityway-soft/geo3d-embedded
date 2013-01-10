package org.avm.elementary.can.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JGenerator extends Generator {

	private static final String PATTERN_STRING = "\\$\\{(\\w+)\\}";

	private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

	private static final MessageFormat fmtName = new MessageFormat(
			"public static final String name = \"{0}\";\n");

	private static final MessageFormat fmtDescription = new MessageFormat(
			"\tpublic static final String description = \"{0}\";\n");

	private static final MessageFormat fmtType = new MessageFormat(
			"\tpublic static final String type = \"{0}\";\n");

	private static final MessageFormat fmtUnit = new MessageFormat(
			"\tpublic static final String unit = \"{0}\";\n");

	private static final MessageFormat fmtMin = new MessageFormat(
			"\tpublic static final double min = {0,number,0.0#########};\n", Locale.US);
	private static final MessageFormat fmtMax = new MessageFormat(
			"\tpublic static final double max = {0,number,0.0#########};\n", Locale.US);;
	private static final MessageFormat fmtScale = new MessageFormat(
			"\tpublic static final double scale = {0,number,0.0#########};\n", Locale.US);
	private static final MessageFormat fmtOffset = new MessageFormat(
			"\tpublic static final double offset = {0,number,0.0#########};\n", Locale.US);

	private static final String DOUBLEVALUE = "\tpublic double value;\n";

	private static final MessageFormat fmtGetValue = new MessageFormat(
			"\tlong v = bs.getbits({0}, {1});\n");

	private static final MessageFormat fmtGetLittleValue = new MessageFormat(
			"\tlong v = bs.little_getbits({0}, {1});\n");

//	private static final MessageFormat fmtGetAvailable = new MessageFormat(
//			"\t\tavailable = !(((v & {0}) == {0}) || ((v & {1}) == {1}));\n");

	private static final MessageFormat fmtPutValue = new MessageFormat(
			"\t\tbs.setbits(v, {0}, {1});\n");

	private static final MessageFormat fmtPutLittleValue = new MessageFormat(
			"\t\tbs.little_setbits(v, {0}, {1});\n");

//	private static final MessageFormat fmtPutAvailable = new MessageFormat(
//			"\t\tv = (available) ? v : (v | {0});\n");

	// private static final MessageFormat fmtScalling = new MessageFormat(
	// "\t\tvalue = {0,number,0.0#########} * v + {1,number,0.0#########};\n",
	// Locale.US);

	private static final MessageFormat fmtScalling = new MessageFormat(
			"\t\tvalue = scale * v + offset;\n", Locale.US);

	// private static final MessageFormat fmtValid = new MessageFormat(
	// "\t\tvalid = !(value < {0,number,0.0#########} || value > {1,number,0.0#########});\n",
	// Locale.US);

	private static final MessageFormat fmtValid = new MessageFormat(
			"\t\tvalid = !(value < min || value > max);\n", Locale.US);

	// private static final MessageFormat fmtUnScalling = new MessageFormat(
	// "long v = (long) ((value - {1,number,0.0#########}) / {0,number,0.0#########});\n",
	// Locale.US);

	private static final MessageFormat fmtUnScalling = new MessageFormat(
			"\tlong v = (long) ((value - offset) / scale);\n", Locale.US);

	private static final MessageFormat fmtSPN = new MessageFormat(
			"\tpublic {0} {1};\n");

	private static final MessageFormat fmtInitSPN = new MessageFormat(
			"\t\t{0} = new {1}();\n\t\t_map.put({0}.getName(),{0});\n");

	private static final MessageFormat fmtGet = new MessageFormat(
			"\t\t{0}.get(bs);\n");

	private static final MessageFormat fmtPut = new MessageFormat(
			"\t\t{0}.put(bs);\n");

	private static final MessageFormat fmtLoad = new MessageFormat(
			"\t\tClass.forName(\"{0}\");\n");

	protected String packageName;

	private StringBuffer spns;

	private StringBuffer init;

	private StringBuffer get;

	private StringBuffer put;

	private StringBuffer load;

	private File root;

	public JGenerator() {
		super();
	}

	protected void beginCAN(CAN can) {
		System.out.println("[CATALOG]" + Catalog.getInstance());
		System.out.println("[BEGIN CAN]" + can);

		load = new StringBuffer();

		String mask = "0x" + can.getMask().toLowerCase();
		packageName = "org.avm.elementary.can.parser." + _name.toLowerCase();

		String mode = "CANParser.CANMODE_NORMAL";
		if (can.getMode().compareToIgnoreCase(CAN.CANMODE_EXTENDED) == 0) {
			mode = "CANParser.CANMODE_EXTENDED";
		}

		int value = Integer.parseInt(can.getMask(), 16);
		int decalage = 0;
		while ((value & 0x1) == 0) {
			decalage++;
			value >>= 1;
		}

		StringBuffer buffer = null;
		try {
			buffer = processParserTemplate(packageName, mask, mode, ""
					+ decalage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		write("Activator", buffer);

	}

	protected void endCAN(CAN can) {
		System.out.println("[END CAN]" + can);

		StringBuffer buffer = null;
		try {
			buffer = processPGNFactoryTemplate(packageName, load.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		write("Factory", buffer);
	}

	protected void beginPGN(PGN pgn) {
		System.out.println("[BEGIN PGN]" + pgn);
		spns = new StringBuffer();
		init = new StringBuffer();
		put = new StringBuffer();
		get = new StringBuffer();
	}

	protected void endPGN(PGN pgn) {

		System.out.println("[END PGN]" + pgn);

		String clazz = "PGN_" + pgn.getId();
		String id = "0x" + pgn.getId();

		String name = packageName + "." + clazz;
		load.append(fmtLoad.format(new Object[] { name }));

		StringBuffer buffer = null;
		try {
			buffer = processPGNTemplate(packageName, clazz, id,
					spns.toString(), init.toString(), get.toString(), put
							.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		write(clazz, buffer);

	}

	protected void beginSPN(SPN spn) {
		System.out.println("[BEGIN SPN]" + spn);

		Slot slot = Catalog.getInstance().get(spn.getSlot());

		// class
		String clazz = spn.getId().substring(0, 1).toUpperCase()
				+ spn.getId().substring(1);

		spns.append(fmtSPN.format(new Object[] { clazz, clazz.toLowerCase() }));
		init.append(fmtInitSPN
				.format(new Object[] { clazz.toLowerCase(), clazz }));
		get.append(fmtGet.format(new Object[] { clazz.toLowerCase() }));
		put.append(fmtPut.format(new Object[] { clazz.toLowerCase() }));

		// value
		StringBuffer value = new StringBuffer();
		value.append(fmtName.format(new Object[] { spn.getId() }));
		value.append(fmtDescription.format(new Object[] { spn.getName() }));
		value.append(fmtType.format(new Object[] { slot.getType() }));
		value.append(fmtUnit.format(new Object[] { slot.getUnit() }));

		value.append(fmtMin.format(new Object[] { slot.getRangeMin() }));
		value.append(fmtMax.format(new Object[] { slot.getRangeMax() }));
		value.append(fmtScale.format(new Object[] { slot.getScaling() }));
		value.append(fmtOffset.format(new Object[] { slot.getOffset() }));

		// value.append("\n");
		// value.append(DOUBLEVALUE);

		int start = (slot.getValType().equals(Slot.BYTE)) ? ((spn
				.getStartBytePosition() - 1 + 6) * 8) : ((spn
				.getStartBytePosition() - 1 + 6) * 8 + (spn
				.getStartBitPosition() - 1));
		int length = (slot.getValType().equals(Slot.BYTE)) ? slot.getLength() * 8
				: slot.getLength();

		// getbits
		StringBuffer getbits = new StringBuffer();
		if (spn.isMsb()) {
			getbits.append(fmtGetValue.format(new Object[] { start, length }));
		} else {
			getbits.append(fmtGetLittleValue.format(new Object[] { start,
					length }));
		}
		getbits.append(fmtScalling.format(new Object[] { slot.getScaling(),
				slot.getOffset() }));

//		if (slot.getValType().equals(Slot.BYTE)) {
//			String mask_available = "0x" + Integer.toHexString(0xff << (slot.getLength() - 1) * 8);
//			String mask_error = "0x" + Integer.toHexString(0xfe << (slot.getLength() - 1) * 8);
//			getbits.append(fmtGetAvailable.format(new Object[] {
//					mask_available, mask_error }));
//		}

		getbits.append(fmtValid.format(new Object[] { slot.getRangeMin(),
				slot.getRangeMax() }));

		// putbits
		StringBuffer putbits = new StringBuffer();
		putbits.append(fmtUnScalling.format(new Object[] { slot.getScaling(),
				slot.getOffset() }));
		
//		if (slot.getValType().equals(Slot.BYTE)) {
//			String mask_available = "0x" + Integer.toHexString(0xff << (slot.getLength() - 1) * 8);
//			String mask_error = "0x" + Integer.toHexString(0xfe << (slot.getLength() - 1) * 8);
//			putbits.append(fmtPutAvailable.format(new Object[] {
//					mask_available, mask_error }));
//		}

		if (spn.isMsb()) {
			putbits.append(fmtPutValue.format(new Object[] { start, length }));
		} else {
			putbits.append(fmtPutLittleValue.format(new Object[] { start,
					length }));
		}

		StringBuffer buffer = null;
		try {
			buffer = processSPNTemplate(packageName, clazz, value.toString(),
					getbits.toString(), putbits.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		write(clazz, buffer);
	}

	protected void endSPN(SPN spn) {
		System.out.println("[END SPN]" + spn);
	}

	private void write(String clazz, StringBuffer buffer) {
		try {
			File root = getPackage(packageName);
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

	private StringBuffer processPGNTemplate(String pkg, String clazz,
			String id, String spn, String init, String get, String put)
			throws Exception {

		StringBuffer template = loadTemplate("_PGN_.txt");

		Matcher matcher = PATTERN.matcher(template);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String var = matcher.group();
			if (var.equals("${class}")) {
				matcher.appendReplacement(result, clazz);
			} else if (var.equals("${package}")) {
				matcher.appendReplacement(result, pkg);
			} else if (var.equals("${id}")) {
				matcher.appendReplacement(result, id);
			} else if (var.equals("${spn}")) {
				matcher.appendReplacement(result, spn);
			} else if (var.equals("${init}")) {
				matcher.appendReplacement(result, init);
			} else if (var.equals("${get}")) {
				matcher.appendReplacement(result, get);
			} else if (var.equals("${put}")) {
				matcher.appendReplacement(result, put);
			}
		}
		matcher.appendTail(result);
		return result;
	}

	private StringBuffer processSPNTemplate(String pkg, String clazz,
			String value, String getbits, String putbits) throws Exception {

		StringBuffer template = loadTemplate("_SPN_.txt");

		Matcher matcher = PATTERN.matcher(template);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String var = matcher.group();
			if (var.equals("${class}")) {
				matcher.appendReplacement(result, clazz);
			} else if (var.equals("${package}")) {
				matcher.appendReplacement(result, pkg);
			} else if (var.equals("${value}")) {
				matcher.appendReplacement(result, value);
			} else if (var.equals("${getbits}")) {
				matcher.appendReplacement(result, getbits);
			} else if (var.equals("${putbits}")) {
				matcher.appendReplacement(result, putbits);
			}
		}
		matcher.appendTail(result);
		return result;
	}

	private StringBuffer processParserTemplate(String pkg, String mask,
			String mode, String decalage) throws Exception {

		StringBuffer template = loadTemplate("_Parser_.txt");

		Matcher matcher = PATTERN.matcher(template);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String var = matcher.group();
			if (var.equals("${mask}")) {
				matcher.appendReplacement(result, mask);
			} else if (var.equals("${package}")) {
				matcher.appendReplacement(result, pkg);
			} else if (var.equals("${decalage}")) {
				matcher.appendReplacement(result, decalage);
			} else if (var.equals("${mode}")) {
				matcher.appendReplacement(result, mode);
			}
		}
		matcher.appendTail(result);
		return result;
	}

	private StringBuffer processPGNFactoryTemplate(String pkg, String load)
			throws Exception {

		StringBuffer template = loadTemplate("_PGNFactory_.txt");

		Matcher matcher = PATTERN.matcher(template);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String var = matcher.group();
			if (var.equals("${load}")) {
				matcher.appendReplacement(result, load);
			} else if (var.equals("${package}")) {
				matcher.appendReplacement(result, pkg);
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