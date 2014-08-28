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
public class KmsList extends KmsMarshaller implements Kms {

	public static final String ROLE = "list";

	private List _listkmsPosition = new ArrayList();
	private List _listkmsInputtrig = new ArrayList();
	private List _listkmsInput = new ArrayList();
	private List _listkmsStats = new ArrayList();
	private List _listkmsCalltrig = new ArrayList();;

	public KmsList() {
		super();
	}

	public void addPosition(KmsPosition position) {
		_listkmsPosition.add(position);
	}

	public void addInputtrig(KmsInputtrig inputtrig) {
		_listkmsInputtrig.add(inputtrig);
	}

	public void addInput(KmsInput kmsinput) {
		_listkmsInput.add(kmsinput);
	}

	public void addStats(KmsStats stats) {
		_listkmsStats.add(stats);
	}

	public void addCalltrig(KmsCalltrig calltrig) {
		_listkmsCalltrig.add(calltrig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.knet.model.KmsMarshaller#getRole()
	 */
	public String getRole() {
		return ROLE;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.avm.device.knet.model.KmsMarshaller#toStringBuffer()
	 */
	public String toXMLString() {
		StringBuffer doc = new StringBuffer();
		doc.append("<" + ROLE + ">");
		if (_listkmsPosition != null) {
			ListIterator li = _listkmsPosition.listIterator();
			while (li.hasNext()) {
				KmsPosition k = (KmsPosition) li.next();
				doc.append(k.toXMLString());
			}
		}
		if (_listkmsInputtrig != null) {
			ListIterator li = _listkmsInputtrig.listIterator();
			while (li.hasNext()) {
				KmsInputtrig k = (KmsInputtrig) li.next();
				doc.append(k.toXMLString());
			}
		}
		if (_listkmsInput != null) {
			ListIterator li = _listkmsInput.listIterator();
			while (li.hasNext()) {
				KmsInput k = (KmsInput) li.next();
				doc.append(k.toXMLString());
			}
		}
		if (_listkmsStats != null) {
			ListIterator li = _listkmsStats.listIterator();
			while (li.hasNext()) {
				KmsStats ks = (KmsStats) li.next();
				doc.append(ks.toXMLString());
			}
		}
		if (_listkmsCalltrig != null) {
			ListIterator li = _listkmsCalltrig.listIterator();
			while (li.hasNext()) {
				KmsCalltrig kc = (KmsCalltrig) li.next();
				doc.append(kc.toXMLString());
			}
		}
		doc.append("</" + ROLE + ">");
		return doc.toString();
	}

	public static class DefaultKmsFactory extends KmsFactory {
		public KmsMarshaller create(int from) {
			KmsRoot kro = new KmsRoot();
			kro.setAttFrom(String.valueOf(from));
			KmsList list = new KmsList();
			kro.addSubRoll(list);
			return kro;
		}

	}

	static {
		KmsFactory.factories.put(ROLE, new DefaultKmsFactory());
	}

}
