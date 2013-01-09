package org.avm.business.core.event;

import org.avm.business.protocol.phoebus.AvanceRetard;


public class Arret implements Event {
	private Point _point;

	private AvanceRetard _avanceRetard;

	private int _baliseId;
	
	private boolean _inside;


	public Arret(Point point, int baliseId, boolean inside, AvanceRetard ar) {
		_point = point;
		_inside=inside;
		_baliseId = baliseId;
		_avanceRetard = ar;
	}

	public Point getPoint() {
		return _point;
	}

	public int getBaliseId(){
		return _baliseId;
	}
	
	public boolean isInsideBalise(){
		return _inside;
	}
	
	public AvanceRetard getAvanceRetard(){
		return _avanceRetard;
	}
}
