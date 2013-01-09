package org.avm.elementary.can.generator;

import java.util.TreeSet;

public class PGN extends TreeSet<SPN> {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n\tpgnID: " + id);
		buffer.append(super.toString());
		return buffer.toString();
	}

	public void accept(PGNVisitor v) {
		v.visit(this);
	}

	interface PGNVisitor {
		public void visit(PGN pgn);
	}
}
