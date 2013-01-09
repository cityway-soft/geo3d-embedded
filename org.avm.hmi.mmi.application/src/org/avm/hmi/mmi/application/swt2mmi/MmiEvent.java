/**
 * 
 */
package org.avm.hmi.mmi.application.swt2mmi;

/**
 * @author lbr
 * 
 */
public class MmiEvent {
	private String _key;
	private String _state;
	private String _value;

	protected MmiEvent(String k, String s, String value) {
		setKey(k);
		setState(s);
		setValue(value);
	}

	public MmiEvent(MmiEvent mmiEvent) {
		setKey(mmiEvent.getKey());
		setState(mmiEvent.getState());
		setValue(mmiEvent.getValue());
	}

	public void setKey(String k) {
		_key = k;
	}

	public String getKey() {
		return _key;
	}

	public void setState(String s) {
		_state = s;
	}

	public String getState() {
		return _state;
	}

	public void setValue(String val) {
		_value = val;
	}

	public String getValue() {
		return _value;
	}

	public String toString() {
		return "MmiEvent [" + _key + ", " + _state + ", " + _value + "]";
	}
}
