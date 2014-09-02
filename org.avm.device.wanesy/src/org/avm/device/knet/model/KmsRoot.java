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
	
	private String status;
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private KmsMarshaller subRoll;

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

	public void addSubRoll(KmsMarshaller subRoll) {
		this.subRoll = subRoll;
	}

	public String getSubRole() {
		if (subRoll != null)
			return subRoll.getRole();
		return "";
	}

	public KmsMarshaller getSubKms() {
		return getSubKms(getSubRole());
	}

	public KmsMarshaller getSubKms(String role) {
		
		return subRoll;
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
		if (subRoll != null)
			doc.append(subRoll.toXMLString());
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
