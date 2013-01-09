package org.stringtree.json;

public class Test {

	public long getEntier() {
		return 1;
	}

	public Double getFloatant() {
		return new Double(32.42);
	}

	public Inner[] getInner() {
		Inner[] x = { new Inner(), new Inner() };
		return x;
	}

	public String getText() {
		return "message de test";
	}

}
