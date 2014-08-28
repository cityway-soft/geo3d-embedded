package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KmsPosition extends KmsMarshaller implements Kms {
	public static final String ROLE = "position";
	private String _fix;
	private String _lat;
	private String _long;
	private String _alt;
	private String _course;
	private String _speed;
	private String _date;
	private String _nbSat;
	private String _hdop;
	private int _period;
	private String _log;
	private String _name = ROLE;
	private String _addpos;

	public KmsPosition() {
		super();
	}

	public KmsPosition(String name, int period) {
		setName(name);
		setPeriod(period);
		setLog("no");
	}

	public void setName(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	public String getRole() {
		return ROLE;
	}

	// <position date="2006-09-26 14:18:44" fix="0" lat="0" long="0" alt="0"
	// course="0" speed="0" nsat="0" hdop="0"/>
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_date != null) {
			doc.append(" date=");
			doc.append("\"");
			doc.append(_date);
			doc.append("\" ");
		}
		if (_fix != null) {
			doc.append(" fix=");
			doc.append("\"");
			doc.append(_fix);
			doc.append("\" ");
		}
		if (_lat != null) {
			doc.append(" lat=");
			doc.append("\"");
			doc.append(_lat);
			doc.append("\" ");
		}
		if (_long != null) {
			doc.append(" long=");
			doc.append("\"");
			doc.append(_long);
			doc.append("\" ");
		}
		if (_alt != null) {
			doc.append(" alt=");
			doc.append("\"");
			doc.append(_alt);
			doc.append("\" ");
		}
		if (_course != null) {
			doc.append(" course=");
			doc.append("\"");
			doc.append(_course);
			doc.append("\" ");
		}
		if (_speed != null) {
			doc.append(" speed=");
			doc.append("\"");
			doc.append(_speed);
			doc.append("\" ");
		}
		if (_hdop != null) {
			doc.append(" hdop=");
			doc.append("\"");
			doc.append(_hdop);
			doc.append("\" ");
		}
		if (_nbSat != null) {
			doc.append(" nbSat=");
			doc.append("\"");
			doc.append(_nbSat);
			doc.append("\" ");
		}
		if (_period != -1) {
			doc.append(" period=");
			doc.append("\"");
			doc.append(_period);
			doc.append("\" ");
		}
		if (_log != null) {
			doc.append(" log=");
			doc.append("\"");
			doc.append(_log);
			doc.append("\" ");
		}
		if (_name != null) {
			doc.append(" name=");
			doc.append("\"");
			doc.append(_name);
			doc.append("\" ");
		}
		if (_addpos != null) {
			doc.append(" addpos=");
			doc.append("\"");
			doc.append(_addpos);
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

	public void setFix(String value) {
		_fix = value;
	}

	public void setLat(String value) {
		_lat = value;
	}

	public void setLong(String value) {
		_long = value;
	}

	public void setAlt(String value) {
		_alt = value;
	}

	public void setCourse(String value) {
		_course = value;
	}

	public void setSpeed(String value) {
		_speed = value;
	}

	public void setDate(String value) {
		_date = value;
	}

	public void setAttNbSat(String value) {
		_nbSat = value;
	}

	public void setAttHdop(String value) {
		_hdop = value;
	}

	public void setPeriod(String value) {
		setPeriod(Integer.parseInt(value));
	}

	public void setPeriod(int i) {
		_period = i;
	}

	public void setLog(String yesNo) {
		_log = yesNo;
	}

	public void setAddPos(String yesNo) {
		_addpos = yesNo;
	}

	public static class DefaultKmsFactory extends KmsFactory {
		public KmsMarshaller create(int from, String name, int period) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			KmsPosition pos = new KmsPosition(name, period);
			kro.addSubRoll(pos);
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

	public double getLatitude() {
		if (_lat != null)
			return Double.parseDouble(_lat);
		return 0d;
	}

	public long getDateAsLong() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date();
		try {
			d = sdf.parse(_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime();
	}

	public double getLongitude() {
		if (_long != null)
			return Double.parseDouble(_long);
		return 0d;
	}

	public double getAltitude() {
		if (_alt != null)
			return Double.parseDouble(_alt);
		return 0d;
	}

	public double getSpeed() {
		if (_speed != null)
			return Double.parseDouble(_speed);
		return 0d;
	}

	public double getCourse() {
		if (_course != null)
			return Double.parseDouble(_course);
		return 0d;
	}

	public int getFix() {
		if (_fix != null)
			return Integer.parseInt(_fix);
		return 0;
	}

}
