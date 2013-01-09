package org.avm.elementary.can.generator;

public class SPN implements Comparable<SPN> {

	private String id;

	private String name;

	private String slot;

	private int startBytePosition;

	private int startBitPosition;

	private boolean msb;

	public boolean isMsb() {
		return msb;
	}

	public void setMsb(boolean msb) {
		this.msb = msb;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public int getStartBitPosition() {
		return startBitPosition;
	}

	public void setStartBitPosition(int startBitPosition) {
		this.startBitPosition = startBitPosition;
	}

	public int getStartBytePosition() {
		return startBytePosition;
	}

	public void setStartBytePosition(int startBytePosition) {
		this.startBytePosition = startBytePosition;
	}

	public int compareTo(SPN o) {
		int result = startBytePosition - o.startBytePosition;
		if (result == 0) {
			result = startBitPosition - o.startBitPosition;
		}
		return result;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" {");
		buffer.append(" name:" + name);
		buffer.append(" slot:" + slot);
		buffer.append(" start-byte-position:" + startBytePosition);
		buffer.append(" start-bit-position:" + startBitPosition);
		buffer.append(" msb" + msb);
		buffer.append("}\n");
		return buffer.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
