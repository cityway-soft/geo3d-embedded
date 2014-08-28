/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsInputtrig extends KmsMarshaller implements Kms {
	public static final String ROLE = "inputtrig";
	public static final String IOTRIG = "IOtrig";
	private String _digital;
	private String _name;
	private String _way;
	private String _value;
	private String _log;
	private String _addpos;
	private String _status;
	private String _ident;
	private String _date;

	public KmsInputtrig() {
		super();
	}

	public KmsInputtrig(String sensorId) {
		_digital = sensorId;
		setAttName(IOTRIG + sensorId);
		_log = "no";
	}

	public KmsInputtrig(String sensorId, String way) {
		_digital = sensorId;
		setAttName(IOTRIG + sensorId);
		_way = way;
		_log = "no";
	}

	public void setAttName(String name) {
		_name = name;
	}

	public String getDigital() {
		return _digital;
	}

	public String getValue() {
		return _value;
	}

	public void setDigital(String value) {
		_digital = value;
	}

	public void setWay(String value) {
		_way = value;
	}

	public void setLog(String value) {
		_log = value;
	}

	public void setAddpos(String value) {
		_addpos = value;
	}

	public void setAttStatus(String value) {
		_status = value;
	}

	public void setAttIdent(String value) {
		_ident = value;
	}

	public void setAttDate(String value) {
		_date = value;
	}

	public void setAttValue(String value) {
		_value = value;
	}

	public String getRole() {
		return ROLE;
	}

	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_digital != null) {
			doc.append(" digital=");
			doc.append("\"");
			doc.append(_digital);
			doc.append("\" ");
		}
		if (_name != null) {
			doc.append(" name=");
			doc.append("\"");
			doc.append(_name);
			doc.append("\" ");
		}
		if (_way != null) {
			doc.append(" way=");
			doc.append("\"");
			doc.append(_way);
			doc.append("\" ");
		}
		if (_log != null) {
			doc.append(" log=");
			doc.append("\"");
			doc.append(_log);
			doc.append("\" ");
		}
		if (_addpos != null) {
			doc.append(" addpos=");
			doc.append("\"");
			doc.append(_addpos);
			doc.append("\" ");
		}
		if (_status != null) {
			doc.append(" status=");
			doc.append("\"");
			doc.append(_status);
			doc.append("\" ");
		}
		if (_ident != null) {
			doc.append(" ident=");
			doc.append("\"");
			doc.append(_ident);
			doc.append("\" ");
		}
		if (_date != null) {
			doc.append(" date=");
			doc.append("\"");
			doc.append(_date);
			doc.append("\" ");
		}
		if (_value != null) {
			doc.append(" value=");
			doc.append("\"");
			doc.append(_value);
			doc.append("\" ");
		}
		doc.append("/>");
		return doc.toString();
	}

	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			marshal(out);
			out.close();
		} catch (Exception e) {

		}

		return ROLE + " : " + out.toString();
	}

	// public void marshal(OutputStream out) throws Exception {
	// marshal(toStringBuffer(), out);
	// }
	public static class DefaultKmsFactory extends KmsFactory {
		/*
		 * <inputtrig digital=\"" + Sensor.PORTE_ARR_ID + "\" log=\"no\"
		 * name=\"iotrig " + Sensor.PORTE_ARR_ID + "\"/>
		 */
		public KmsMarshaller create(int from, String sensorId) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			kro.addSubRoll(new KmsInputtrig(sensorId));
			return kro;
		}

		public KmsMarshaller create(int from, String sensorId, String way) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			kro.addSubRoll(new KmsInputtrig(sensorId, way));
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
