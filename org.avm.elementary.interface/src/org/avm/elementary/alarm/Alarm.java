package org.avm.elementary.alarm;

import java.util.Date;

public class Alarm {
	public static final String INDEX = "index";
	public static final String NAME = "name";
	public static final String KEY = "key";
	public static final String NOTIFY_UP = "notify-up";
	public static final String NOTIFY_DOWN = "notify-down";
	public static final String ACKNOWLEDGE = "acknowledge";
	public static final String TYPE = "type";
	public static final String ORDER = "order";
	public static final String READONLY = "readonly";

	
	public static final int ALARM_EXPLOITATION=1;
	public static final int ALARM_TECHNICAL=0;

	private boolean status;
	private String name;
	private Date date;
	private String key;
	private int order;
	private int type;
	private Integer index;
	private boolean readonly=false;

	public boolean isStatus() {
		return status;
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	public String getKey() {
		return key;
	}

	public int getOrder() {
		return order;
	}

	public Integer getIndex() {
		return index;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	public Alarm(Integer index) {
		super();
		this.index= index;
	}
	
	public Alarm(Integer index, int order, boolean status, String name, Date date, String key, int type, boolean readonly) {
		super();
		this.index= index;
		this.order=order;
		this.status = status;
		this.name = name;
		this.date = date;
		this.key = key;
		this.type = type;
		this.readonly= readonly;
	}

	public String toString() {
		return "index: "+index+ ", order: " + order +", status: " + status + ", name: " + name + ", date: "
				+ date +  " type:"+type + " key: " + key + " readonly: " + readonly;
	}
	
	
	public int getType(){
		return this.type;
	}

	public void setStatus(boolean status) {
		this.status=status;
	}

	public boolean isReadOnly() {
		return readonly;
	}
}
