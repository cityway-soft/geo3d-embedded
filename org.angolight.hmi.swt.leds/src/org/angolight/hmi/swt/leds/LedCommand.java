package org.angolight.hmi.swt.leds;

public class LedCommand {
	public static final byte TYPE_I=0;	
	public static final byte TYPE_J=1;
	public short getState() {
		return state;
	}

	public byte getPeriod() {
		return period;
	}
	
	public byte getType() {
		return type;
	}

	private byte type;
	private short state;
	private byte period;
	
	private LedCommand(byte type, short state, byte period) {
		super();
		this.state = state;
		this.period = period;
		this.type = type;
	}
	
	public static LedCommand createI(short state, byte period){
		return new LedCommand(TYPE_I, state, period);
		
	}
	
	public static LedCommand createJ(short state, byte period){
		return new LedCommand(TYPE_J, state, period);
		
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(Integer.toHexString(state));
		buf.append(" x ");
		buf.append(period*10);
		buf.append("ms");
		return buf.toString();
	}

}
