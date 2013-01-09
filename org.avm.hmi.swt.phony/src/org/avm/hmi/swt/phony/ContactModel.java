package org.avm.hmi.swt.phony;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.avm.elementary.directory.Directory;

public class ContactModel {
	private static final String PHONE_ENTRY = "phone";

	private Dictionary _hashContacts;

	private Directory _directory;

	public ContactModel() {
		_hashContacts = new Hashtable();
	}

	public int size(){
		return _hashContacts.size();
	}
	
	
	public Enumeration elements() {
		if (_hashContacts == null)
			return null;
		Vector v = new Vector();
		Enumeration e = _hashContacts.keys();
		while (e.hasMoreElements()) {
			v.add(e.nextElement());
		}
		Object[] tab = v.toArray();
		Arrays.sort(tab, new ContactComparator());
		v = new Vector();
		for (int i = 0; i < tab.length; i++) {
			v.add(tab[i]);
		}
		return v.elements();
	}

	public String getPhoneNumber(String name) {
		return (String) _hashContacts.get(name);
	}

	public void update(Directory directory) {
		_directory = directory;
		if (_directory != null) {
			Dictionary dic = _directory.getProperty(null);
			_hashContacts = new Hashtable();
			Enumeration e = dic.keys();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				Properties p = (Properties) dic.get(name);
				if (p != null) {
					String phone = p.getProperty(PHONE_ENTRY);
					if (phone != null) {
						_hashContacts.put(name, phone);
					}
				}
			}
		}
		else{
			_hashContacts = new Hashtable();
		}
	}

	public void unsetDirectory(Directory directory) {
		_directory = null;
	}

	public class ContactComparator implements java.util.Comparator {

		public int compare(Object o1, Object o2) throws ClassCastException {
			if (!(o1 instanceof String)) {
				throw new ClassCastException();
			}
			if (!(o2 instanceof String)) {
				throw new ClassCastException();
			}
			String c1 = (String) o1;
			String c2 = (String) o2;
			return c1.compareTo(c2);
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
