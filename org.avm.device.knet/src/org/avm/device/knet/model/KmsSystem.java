/**
 * 
 */
package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;

/**
 * @author lbr
 * 
 */
public class KmsSystem extends KmsMarshaller implements Kms {
	public static final String ROLE = "system";

	private String _cpu;
	private String _flash;
	private String _mem;
	private String _report;
	private String _action;
	private String _delay;
	private String _class;
	private String _adress;
	private String _network;
	private String _key;

	public KmsSystem() {
		super();
	}

	public void setAttCpu(String value) {
		_cpu = value;
	}

	public void setAttFlash(String value) {
		_flash = value;
	}

	public void setAttMem(String value) {
		_mem = value;
	}

	public void setAttReport(String value) {
		_report = value;
	}

	public void setAttAct(String value) {
		_action = value;
	}

	public void setAttClass(String value) {
		_class = value;
		setIdentifiant(value);
	}

	public void setAttNetwork(String ssid) {
		_network = ssid;
	}

	public void setAttKey(String key) {
		_key = key;
	}

	public String getRole() {
		return ROLE;
	}

	public String getAct() {
		return _action;
	}

	public String getAttClass() {
		return _class;
	}

	public String getReport() {
		return _report;
	}

	// <system mem="218" cpu="8" flash="62" />
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE);
		if (_action != null) {
			doc.append(" act=");
			doc.append("\"");
			doc.append(_action);
			doc.append("\"");
		}
		if (_class != null) {
			doc.append(" class=");
			doc.append("\"");
			doc.append(_class);
			doc.append("\"");
		}
		if (_mem != null) {
			doc.append(" mem=");
			doc.append("\"");
			doc.append(_mem);
			doc.append("\"");
		}
		if (_cpu != null) {
			doc.append(" cpu=");
			doc.append("\"");
			doc.append(_cpu);
			doc.append("\"");
		}
		if (_flash != null) {
			doc.append(" flash=");
			doc.append("\"");
			doc.append(_flash);
			doc.append("\"");
		}
		if (_network != null) {
			doc.append(" network=");
			doc.append("\"");
			doc.append(_network);
			doc.append("\"");
		}
		if (_key != null) {
			doc.append(" key=");
			doc.append("\"");
			doc.append(_key);
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
		public KmsMarshaller create(int from, String cmde, String bearerName,
				String ssid, String key) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			KmsSystem system = new KmsSystem();
			system.setAttAct(cmde);
			system.setAttClass(bearerName);
			system.setAttNetwork(ssid);
			system.setAttKey(key);
			kro.addSystem(system);
			return kro;
		}

		public KmsMarshaller create(int from, String cmde, String bearerName) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			KmsSystem system = new KmsSystem();
			system.setAttAct(cmde);
			system.setAttClass(bearerName);
			kro.addSystem(system);
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
