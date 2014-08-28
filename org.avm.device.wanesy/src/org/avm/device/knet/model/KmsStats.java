/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lbr
 * 
 */
public class KmsStats extends KmsMarshaller implements Kms {
	public static final String ROLE = "stats";
	private String _date;
	private String _period;
	private String _name;
	private String _log;
	private KmsSystem _kmsSystem;
	private KmsKnetpOut _kmsKnetPOut;
	private List _kmsKnetPInList = new ArrayList();

	public KmsStats() {
		super();
	}

	protected KmsStats(String period, String name) {
		_period = period;
		_name = name;
	}

	public void setAttPeriod(String value) {
		_period = value;
	}

	public void setAttName(String value) {
		_name = value;
	}

	public void setAttLog(String value) {
		_log = value;
	}

	public String getRole() {
		return ROLE;
	}

	public void setDate(String value) {
		_date = value;
	}

	// <stats date="2006-09-26 14:18:43">
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_date != null) {
			doc.append(" date=");
			doc.append("\"");
			doc.append(_date);
			doc.append("\"");
		}
		if (_period != null) {
			doc.append(" period=");
			doc.append("\"");
			doc.append(_period);
			doc.append("\"");
		}
		if (_name != null) {
			doc.append(" name=");
			doc.append("\"");
			doc.append(_name);
			doc.append("\"");
		}
		if (_log != null) {
			doc.append(" log=");
			doc.append("\"");
			doc.append(_log);
			doc.append("\"");
		}
		doc.append(">");
		if (_kmsSystem != null)
			doc.append(_kmsSystem.toXMLString());
		if (_kmsKnetPOut != null)
			doc.append(_kmsKnetPOut.toXMLString());
		if (_kmsKnetPInList != null)
			for (int i = 0; i < _kmsKnetPInList.size(); i++) {
				doc.append(((KmsKnetpIn) _kmsKnetPInList.get(i)).toXMLString());
			}
		doc.append("</stats>");
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

	public void addSystem(KmsSystem system) {
		_kmsSystem = system;
	}

	public void addKnetpOut(KmsKnetpOut knetpout) {
		_kmsKnetPOut = knetpout;

	}

	public void addKnetpIn(KmsKnetpIn _knetpin) {
		_kmsKnetPInList.add(_knetpin);
	}

	public static class DefaultKmsFactory extends KmsFactory {
		public KmsMarshaller create(int from, String period, String name) {
			// System.out.println("KmsStats::create("+from+","+period+","+name+")");
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			kro.addSubRoll(new KmsStats(period, name));
			// System.out.println("kro creee :"+kro);
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

	public String getStatus() {
		return _kmsKnetPOut.getStatus();
	}

	public String getBearer() {
		return _kmsKnetPOut.getBearer();
	}

}
