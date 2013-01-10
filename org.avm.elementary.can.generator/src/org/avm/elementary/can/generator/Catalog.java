package org.avm.elementary.can.generator;

import java.util.HashMap;

public class Catalog extends HashMap<String, Slot> {

	private static Catalog singleton;

	public Catalog() {
		super();
		singleton = this;
	}

	public static Catalog getInstance() {
		return singleton;
	}

	public void add(Slot slot) {
		put(slot.getName(), slot);
	}

	public String toString() {
		return super.toString();
	}
}
