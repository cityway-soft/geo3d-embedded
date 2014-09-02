package org.avm.device.knet.model;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class KmsMmi extends KmsMarshaller {
	public static final String ROLE = "mmi";

	private KmsUpdate _kmsupdate;
	private KmsNew _kmsnew;
	// private KmsRsp _kmsrsp;
	private KmsText _kmstext;
	private List _listkmslabel = new ArrayList();

	public KmsMmi() {
		super();
	}

	public String getRole() {
		return ROLE;
	}

	// <mmi><new name="SAISIE_VALEUR" id="attente saisie
	// authentification"/><label name="msg1" text=""/><label name="msg2"
	// text=""/><label name="saisie" text="Matricule:"/></mmi>
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE + ">");
		if (_kmsupdate != null) {
			doc.append(_kmsupdate.toXMLString());
		}
		if (_kmsnew != null) {
			doc.append(_kmsnew.toXMLString());
		}
		// if (_kmsrsp != null){
		// doc.append(_kmsrsp.toStringBuffer().toString());
		// }
		if (_kmstext != null) {
			doc.append(_kmstext.toXMLString());
		}
		if (_listkmslabel != null) {
			ListIterator li = _listkmslabel.listIterator();
			while (li.hasNext()) {
				KmsLabel kl = (KmsLabel) li.next();
				doc.append(kl.toXMLString());
			}
		}
		doc.append("</" + ROLE + ">");
		return doc.toString();
	}

	public void addUpdate(KmsUpdate kmsupdate) {
		_kmsupdate = kmsupdate;
	}

	public void addNew(KmsNew kmsnew) {
		_kmsnew = kmsnew;
	}

	public void addLabel(KmsLabel kmslabel) {
		_listkmslabel.add(kmslabel);
	}

	// public void addRsp(KmsRsp kmsrsp) {
	// _kmsrsp = kmsrsp;
	// }
	public void addText(KmsText kmstext) {
		_kmstext = kmstext;
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
		public KmsMarshaller create(int from, int to, int knetid,
				Hashtable mmiSubTrees) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			kro.setAttTo(String.valueOf(to));
			kro.setAttKnetId(String.valueOf(knetid));
			KmsMsg m = new KmsMsg();
			KmsMmi mmi = new KmsMmi();
			Collection col = mmiSubTrees.values();
			Iterator it = col.iterator();
			while (it.hasNext()) {
				KmsMarshaller km = (KmsMarshaller) it.next();
				if (km.getRole().equalsIgnoreCase(KmsNew.ROLE)) {
					mmi.addNew((KmsNew) km);
					continue;
				}
				if (km.getRole().equalsIgnoreCase(KmsUpdate.ROLE)) {
					mmi.addUpdate((KmsUpdate) km);
					continue;
				}
				if (km.getRole().equalsIgnoreCase(KmsLabel.ROLE)) {
					mmi.addLabel((KmsLabel) km);
					continue;
				}
				if (km.getRole().equalsIgnoreCase(KmsText.ROLE)) {
					mmi.addText((KmsText) km);
					continue;
				}
			}
			m.addMmi(mmi);
			kro.addSubRoll(m);
			return kro;
		}
	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

	// public String getAppId() {
	// if(_kmsrsp != null)
	// return _kmsrsp.getAppId();
	// return null;
	// }
	// public String getAction() {
	// if(_kmsrsp != null)
	// return _kmsrsp.getAction();
	// return null;
	// }
	public String getTextName() {
		if (_kmstext != null)
			return _kmstext.getName();
		return null;
	}

	public String getTextValue() {
		if (_kmstext != null)
			return _kmstext.getTextValue();
		return null;
	}
}
