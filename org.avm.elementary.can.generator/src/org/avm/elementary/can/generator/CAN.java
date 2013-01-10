package org.avm.elementary.can.generator;

import java.util.ArrayList;
import java.util.Iterator;

public class CAN extends ArrayList<PGN> {

	public static final String CANMODE_NORMAL = "normal";
	public static final String CANMODE_EXTENDED = "extended";
	
	private String name;
	private String mask;
	private String mode;
	private String check;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n name:" + name);
		buffer.append(" pgn-mask:" + mask);
		buffer.append(" mode:" + mode);
		buffer.append(" check-spn:" + check);
		buffer.append("[");

		for (Iterator iter = this.iterator(); iter.hasNext();) {
			PGN pgn = (PGN) iter.next();
			buffer.append(pgn.toString() + ",\n");
		}

		buffer.append("]");

		return buffer.toString();
	}

	public void accept(CANVisitor v) {
		v.visit(this);
	}

	interface CANVisitor {
		public void visit(CAN spec);
	}

}
