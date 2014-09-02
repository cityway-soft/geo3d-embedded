/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author lbr
 * 
 */
public class KmsInput extends KmsMarshaller implements Kms {
	public static final String ROLE = "input";
	private String _digital;
	private String _period;
	private String _log;
	private String _addpos;
	private String _name;
	private String _temperature;
	private List _listkmsInputtrig = new ArrayList();
	private List _listkmsStats = new ArrayList();

	public KmsInput() {
		super();
	}

	public void setAttName(String name) {
		_name = name;
	}

	public String getDigital() {
		return _digital;
	}

	public void setDigital(String value) {
		_digital = value;
	}

	public void setPeriod(String value) {
		_period = value;
	}

	public void setLog(String value) {
		_log = value;
	}

	public void setAddpos(String value) {
		_addpos = value;
	}

	public void setTemperature(String value) {
		_temperature = value;
	}

	public void addInputtrig(KmsInputtrig kmsinputtrig) {
		_listkmsInputtrig.add(kmsinputtrig);
	}

	public void addStats(KmsStats stats) {
		_listkmsStats.add(stats);
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
		if (_period != null) {
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
		if (_addpos != null) {
			doc.append(" addpos=");
			doc.append("\"");
			doc.append(_addpos);
			doc.append("\" ");
		}
		if (_temperature != null) {
			doc.append(" temperature=");
			doc.append("\"");
			doc.append(_temperature);
			doc.append("\" ");
		}
		doc.append(">");
		if (_listkmsInputtrig != null) {
			ListIterator li = _listkmsInputtrig.listIterator();
			while (li.hasNext()) {
				KmsInputtrig ki = (KmsInputtrig) li.next();
				doc.append(ki.toXMLString());
			}
		}
		if (_listkmsStats != null) {
			ListIterator li = _listkmsStats.listIterator();
			while (li.hasNext()) {
				KmsStats ks = (KmsStats) li.next();
				doc.append(ks.toXMLString());
			}
		}

		doc.append("</" + ROLE + ">");
		return doc.toString();
	}

	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			marshal(out);
			out.close();
		} catch (Exception e) {

		}

		return ROLE + " : " + makeItBeautifull(out.toString());
	}

	public static class DefaultKmsFactory extends KmsFactory {
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
