/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsKnetpOut extends KmsMarshaller {

	public static final String ROLE = "knetpOut";
	private String _status;
	private String _bearer;
	private String _knetId;
	private String _start;
	private String _stop;
	private String _voltx;
	private String _volrx;
	private String _id;
	private String _class;

	public KmsKnetpOut() {
		super();
	}

	public String getRole() {
		return ROLE;
	}

	public void setStatus(String value) {
		_status = value;
	}

	public void setKnetId(String value) {
		_knetId = value;
	}

	public void setStart(String value) {
		_start = value;
	}

	public void setStop(String value) {
		_stop = value;
	}

	public void setVolTx(String value) {
		_voltx = value;
	}

	public void setVolRx(String value) {
		_volrx = value;
	}

	public void setId(String value) {
		_id = value;
	}

	public void setAttClass(String value) {
		_class = value;
	}

	public String getStatus() {
		return _status;
	}

	public String getBearer() {
		return _class;
	}

	// <knetpOUT status="disconnected"/>
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_knetId != null) {
			doc.append(" knetid=");
			doc.append("\"");
			doc.append(_knetId);
			doc.append("\"");
		}
		if (_status != null) {
			doc.append(" status=");
			doc.append("\"");
			doc.append(_status);
			doc.append("\"");
		}
		if (_start != null) {
			doc.append(" start=");
			doc.append("\"");
			doc.append(_start);
			doc.append("\"");
		}
		if (_stop != null) {
			doc.append(" stop=");
			doc.append("\"");
			doc.append(_stop);
			doc.append("\"");
		}
		if (_voltx != null) {
			doc.append(" voltx=");
			doc.append("\"");
			doc.append(_voltx);
			doc.append("\"");
		}
		if (_volrx != null) {
			doc.append(" volrx=");
			doc.append("\"");
			doc.append(_volrx);
			doc.append("\"");
		}
		if (_id != null) {
			doc.append(" id=");
			doc.append("\"");
			doc.append(_id);
			doc.append("\"");
		}
		if (_class != null) {
			doc.append(" class=");
			doc.append("\"");
			doc.append(_class);
			doc.append("\"");
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

	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
