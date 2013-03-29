package org.avm.elementary.management.core.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import org.avm.elementary.management.core.Management;

public class Terminal {
	private static final String PERSISTANCE_PROPERTY_FILE=System.getProperty(Management.AVMHOME_TAG)+System.getProperty("file.separator")+"bin"
			+ System.getProperty("file.separator")+"terminal.properties";
	public static final String VEHICULE_PROPERTY = "org.avm.vehicule.id";// deprecated
	public static final String EXPLOITATION_PROPERTY = "org.avm.exploitation.id";// deprecated
	public static final String PLATEFORM_ID_PROPERTY = "org.avm.plateform.id";// deprecated
	public static final String PLATEFORM_PROPERTY = "org.avm.plateform";// deprecated

	public static final String TERMINAL_PLATEFORM_PROPERTY = "org.avm.terminal.plateform";
	public static final String TERMINAL_ID_PROPERTY = "org.avm.terminal.id";
	public static final String TERMINAL_OWNER_PROPERTY = "org.avm.terminal.owner";
	public static final String TERMINAL_NAME_PROPERTY = "org.avm.terminal.name";

	String name;
	String owner;
	String plateform;
	String id;

	private static Terminal _instance = null;

	private Terminal() {
		load();

	}

	public static Terminal getInstance() {
		if (_instance == null) {
			_instance = new Terminal();
		}
		return _instance;
	}
	
	public void setName(String name){
		this.name = name;
		System.setProperty(TERMINAL_NAME_PROPERTY, name);
		
	}
	
	public void setOwner(String owner){
		this.owner = owner;
		System.setProperty(TERMINAL_OWNER_PROPERTY, owner);
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public String getPlateform() {
		return plateform;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("id=");
		buf.append(id);
		buf.append(",name=");

		buf.append(name);
		buf.append(",owner=");

		buf.append(owner);
		buf.append(",plateform=");

		buf.append(plateform);
		return buf.toString();
	}
	
	private void load(){
		boolean mustBeSaved=false;
		Properties p = Utils.loadProperties(PERSISTANCE_PROPERTY_FILE);
		
		String name = p.getProperty(TERMINAL_NAME_PROPERTY);
		if (name == null) {
			name = System.getProperty(TERMINAL_NAME_PROPERTY);
			if (name == null){
				name = "0";
			}
			mustBeSaved = true;
		}
		setName(name);
		
		String owner = p.getProperty(TERMINAL_OWNER_PROPERTY);
		if (owner == null) {
			owner = System.getProperty(TERMINAL_OWNER_PROPERTY);
			if (owner == null){
				owner = "0";
			}
			mustBeSaved=true;
		}
		setOwner(owner);
		if (mustBeSaved){
			try {
				save();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		id = System.getProperty(TERMINAL_ID_PROPERTY);
		if (id == null) {
			id = System.getProperty(PLATEFORM_ID_PROPERTY, buildDefault()); // deprecated
		}
		//-- suppression des ":" dans l'identifiant
		StringBuffer buf = new StringBuffer();
		StringTokenizer t = new StringTokenizer(id, ":");
		while(t.hasMoreElements()){
			buf.append(t.nextElement());
		}
		id = buf.toString().toUpperCase();

		plateform = System.getProperty(TERMINAL_PLATEFORM_PROPERTY);
		if (plateform == null) {
			plateform = System.getProperty(PLATEFORM_PROPERTY); // deprecated
			System.setProperty(TERMINAL_PLATEFORM_PROPERTY, plateform);
		}
	}
	
	public String buildDefault(){
		StringBuffer buf=new StringBuffer();
		buf.append("FF");
		while(buf.length()<16){
			int rand = (int)(Math.random()*256);
			if (rand < 10){
				buf.append("0");
			}
			buf.append(Integer.toHexString(rand));
		}
		return buf.toString().substring(0,16).toUpperCase();
	}
	
	public void save() throws FileNotFoundException, IOException{
		Properties p = new Properties();
		p.setProperty(TERMINAL_NAME_PROPERTY, name);
		p.setProperty(TERMINAL_OWNER_PROPERTY, owner);
		Utils.saveProperties(p, PERSISTANCE_PROPERTY_FILE);
	}
}
