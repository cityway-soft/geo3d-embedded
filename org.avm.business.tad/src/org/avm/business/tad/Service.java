package org.avm.business.tad;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

public class Service {
	private Vector _listMission;
	private HashMap _hashMission;
	
	public Service(){
		_listMission = new Vector();
		_hashMission = new HashMap();
	}
	
	public void add(Mission mission){
		if (exist(mission)){
			remove(mission.getId());
		}
		_listMission.add(mission);	
		_hashMission.put(mission.getId(), mission);
	}
	
	private boolean exist(Mission mission){
		return ( _hashMission.get( mission.getId() ) != null);
	}

	public void insert(Mission mission, int rang){
		if (exist(mission)){
			remove(mission.getId());
		}
		if (rang < _listMission.size()){
			_listMission.insertElementAt(mission, rang);
		}
		else{
			_listMission.add(mission);
		}
		_hashMission.put(mission.getId(), mission);
	}
	
	public void remove(Long id){
		Mission m = (Mission)_hashMission.get(id);
		if (m != null){
			_listMission.remove(m);
			_hashMission.remove(m);
		}
	}
	
	public Enumeration elements(){
		return _listMission.elements();
	}

	public Mission get(Long id) {
		return (Mission)_hashMission.get( id );
	}
	
	
}
