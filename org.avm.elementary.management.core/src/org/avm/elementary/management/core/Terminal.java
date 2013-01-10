package org.avm.elementary.management.core;

public class Terminal {
	String name;
	String owner;
	String plateform;
	String id;
	
	private static Terminal _instance=null;
	
	private Terminal(){
		name = System
				.getProperty(Management.TERMINAL_NAME_PROPERTY);
		if (name == null) {
			name = System.getProperty(Management.VEHICULE_PROPERTY); // deprecated
		}
		owner  = System.getProperty(Management.TERMINAL_OWNER_PROPERTY);
		if (owner == null){
			 owner  =System
						.getProperty(Management.EXPLOITATION_PROPERTY); //deprecated
		}
		
		id  = System.getProperty(Management.TERMINAL_ID_PROPERTY);
		if (id == null){
			id  =System
						.getProperty(Management.PLATEFORM_ID_PROPERTY); //deprecated
		}
		
		plateform  = System.getProperty(Management.TERMINAL_PLATEFORM_PROPERTY);
		if (plateform == null){
			plateform  =System
						.getProperty(Management.PLATEFORM_PROPERTY); //deprecated
		}
		
	}
	
	public static Terminal getInstance(){
		if (_instance == null){
			_instance = new Terminal();
		}
		return _instance;
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
}
