package org.avm.business.hmi.web.avm.servlet;

import org.avm.business.core.event.Event;

public class SimpleCourse implements Event{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7634801819003555703L;
	
	public String idu;
	public String hdepart;
	public String terminee;
	public String terminus;

	public SimpleCourse(int idu, String hdepart, String terminus,
			boolean terminee) {
		this.idu = idu + "";
		this.hdepart = hdepart;
		this.terminus = terminus;
		this.terminee = (terminee) ? "T" : "";
	}
}
