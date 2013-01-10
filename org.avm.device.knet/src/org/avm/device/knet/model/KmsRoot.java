/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsRoot extends KmsMarshaller implements Kms {
	public static final String ROLE = "kms";

	private String _from;

	private String _to;

	private String _knetid;

	private String _rto;

	private String _conf;

	private String _ref;

	private String _date;

	private String _rKnetId;

	private String _name;

	private KmsCalltrig _kmsCalltrig;
	private KmsCall _kmsCall;
	private KmsInputtrig _kmsInputtrig;
	private KmsBeep _kmsBeep;
	private KmsList _kmsList;
	private KmsSystem _kmsSystem;
	private KmsStats _kmsStats;
	private KmsPosition _kmsPosition;
	private KmsConnect _kmsConnect;
	private KmsConf _kmsConf;
	private KmsMsg _kmsMsg;
	private KmsStop _kmsStop;
	private KmsPoweroff _kmsPoweroff;

	public KmsRoot() {
		super();
	}

	public KmsRoot(String name, String from, String to, String id) {
		this();
		setAttFrom(from);
		setName(name);
		setAttTo(to);
		setAttKnetId(id);
	}

	public void setAttFrom(String value) {
		_from = value;
	}

	public void setAttTo(String value) {
		if (value != null && "-1".equals(value))
			_to = null;
		_to = value;
	}

	public void setAttKnetId(String value) {
		_knetid = value;
		if (value != null && "-1".equals(value))
			_knetid = null;
	}

	public void setAttRto(String value) {
		_rto = value;
	}

	public void setAttRknetId(String value) {
		_rKnetId = value;
	}

	public void setAttConf(String value) {
		_conf = value;
	}

	public void setAttRef(String value) {
		_ref = value;
	}

	public void setAttDate(String value) {
		_date = value;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	public String getDate() {
		return _date;
	}

	public void addStats(KmsStats stats) {
		_kmsStats = stats;
	}

	public void addPosition(KmsPosition position) {
		_kmsPosition = position;
	}

	public void addConnect(KmsConnect connect) {
		_kmsConnect = connect;
		setIdentifiant(KmsConnect.ROLE);
	}

	public void addMsg(KmsMsg msg) {
		_kmsMsg = msg;
	}

	public void addConf(KmsConf conf) {
		_kmsConf = conf;
		setIdentifiant(getName());
	}

	public void addStop(KmsStop stop) {
		_kmsStop = stop;
	}

	public void addCalltrig(KmsCalltrig calltrig) {
		_kmsCalltrig = calltrig;
	}

	public void addCall(KmsCall call) {
		_kmsCall = call;
	}

	public void addInputtrig(KmsInputtrig inputtrig) {
		_kmsInputtrig = inputtrig;
	}

	public void addBeep(KmsBeep beep) {
		_kmsBeep = beep;
	}

	public void addList(KmsList list) {
		_kmsList = list;
		setName(list.getIdentifiant());
	}

	public void addSystem(KmsSystem system) {
		_kmsSystem = system;
	}

	public void addPoweroff(KmsPoweroff poweroff) {
		_kmsPoweroff = poweroff;
	}

	public String getSubRole() {
		if (_kmsConnect != null)
			return KmsConnect.ROLE;
		if (_kmsPosition != null)
			return KmsPosition.ROLE;
		if (_kmsStats != null)
			return KmsStats.ROLE;
		if (_kmsMsg != null)
			return KmsMsg.ROLE;
		if (_kmsConf != null)
			return KmsConf.ROLE;
		if (_kmsStop != null)
			return KmsStop.ROLE;
		if (_kmsCalltrig != null)
			return KmsCalltrig.ROLE;
		if (_kmsCall != null)
			return KmsCall.ROLE;
		if (_kmsInputtrig != null)
			return KmsInputtrig.ROLE;
		if (_kmsBeep != null)
			return KmsBeep.ROLE;
		if (_kmsList != null)
			return KmsList.ROLE;
		if (_kmsSystem != null)
			return KmsSystem.ROLE;
		if (_kmsPoweroff != null)
			return KmsPoweroff.ROLE;
		return "";
	}

	public Kms getSubKms() {
		return getSubKms(getSubRole());
	}

	public Kms getSubKms(String role) {
		if (role == null)
			return null;
		if (role.equalsIgnoreCase(KmsConf.ROLE))
			return _kmsConf;
		if (role.equalsIgnoreCase(KmsStats.ROLE))
			return _kmsStats;
		if (role.equalsIgnoreCase(KmsMsg.ROLE))
			return _kmsMsg;
		if (role.equalsIgnoreCase(KmsPosition.ROLE))
			return _kmsPosition;
		if (role.equalsIgnoreCase(KmsConnect.ROLE))
			return _kmsConnect;
		if (role.equalsIgnoreCase(KmsStop.ROLE))
			return _kmsStop;
		if (role.equalsIgnoreCase(KmsCalltrig.ROLE))
			return _kmsCalltrig;
		if (role.equalsIgnoreCase(KmsCall.ROLE))
			return _kmsCall;
		if (role.equalsIgnoreCase(KmsInputtrig.ROLE))
			return _kmsInputtrig;
		if (role.equalsIgnoreCase(KmsBeep.ROLE))
			return _kmsBeep;
		if (role.equalsIgnoreCase(KmsList.ROLE))
			return _kmsList;
		if (role.equalsIgnoreCase(KmsSystem.ROLE))
			return _kmsSystem;
		if (role.equalsIgnoreCase(KmsPoweroff.ROLE))
			return _kmsPoweroff;
		return null;
	}

	public String getRole() {
		return ROLE;
	}

	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_name != null) {
			doc.append(" name=");
			doc.append("\"");
			doc.append(_name);
			doc.append("\" ");
		}
		if (_from != null) {
			doc.append(" from=");
			doc.append("\"");
			doc.append(_from);
			doc.append("\" ");
		}
		if (_to != null) {
			doc.append(" to=");
			doc.append("\"");
			doc.append(_to);
			doc.append("\" ");
		}
		if (_knetid != null) {
			doc.append(" knetid=");
			doc.append("\"");
			doc.append(_knetid);
			doc.append("\" ");
		}
		if (_rto != null) {
			doc.append(" rto=");
			doc.append("\"");
			doc.append(_rto);
			doc.append("\" ");
		}
		if (_conf != null) {
			doc.append(" conf=");
			doc.append("\"");
			doc.append(_conf);
			doc.append("\" ");
		}
		if (_ref != null) {
			doc.append(" ref=");
			doc.append("\"");
			doc.append(_ref);
			doc.append("\" ");
		}
		if (_date != null) {
			doc.append(" date=");
			doc.append("\"");
			doc.append(_date);
			doc.append("\" ");
		}
		doc.append(">");
		if (_kmsConnect != null)
			doc.append(_kmsConnect.toXMLString());
		if (_kmsPosition != null)
			doc.append(_kmsPosition.toXMLString());
		if (_kmsStop != null)
			doc.append(_kmsStop.toXMLString());
		if (_kmsStats != null)
			doc.append(_kmsStats.toXMLString());
		if (_kmsMsg != null)
			doc.append(_kmsMsg.toXMLString());
		if (_kmsConf != null)
			doc.append(_kmsConf.toXMLString());
		if (_kmsCalltrig != null)
			doc.append(_kmsCalltrig.toXMLString());
		if (_kmsCall != null)
			doc.append(_kmsCall.toXMLString());
		if (_kmsInputtrig != null)
			doc.append(_kmsInputtrig.toXMLString());
		if (_kmsBeep != null)
			doc.append(_kmsBeep.toXMLString());
		if (_kmsList != null)
			doc.append(_kmsList.toXMLString());
		if (_kmsSystem != null)
			doc.append(_kmsSystem.toXMLString());
		if (_kmsPoweroff != null)
			doc.append(_kmsPoweroff.toXMLString());
		doc.append("</kms>");
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

	public static class DefaultKmsFactory extends KmsFactory {
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
