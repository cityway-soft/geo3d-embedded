package org.avm.business.tad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Mission {
	public static final int ETAT_A_FAIRE = -1;	
	public static final int ETAT_EFFECTUE = 0;
	public static final int ETAT_NON_REALISE_ANNULE = 1;
	public static final int ETAT_NON_REALISE_CLIENT_ABSENT = 2;
	
	public static final int TYPE_MONTEE = 0;
	public static final int TYPE_DESCENTE = 1;
	public static final int TYPE_MONTEE_DESCENTE=2;
	
	private Long _id;
	
	private String _destination;
	
	private String _description;
	
	private int _state;
	
	private Date _date;
	
	private int _type;
	
	private static SimpleDateFormat df = new SimpleDateFormat("ddMMyy;HHmm");

	private  Mission(long id, int type, String destination, String description){
		_id = new Long(id);
		_destination = destination;
		_description = description;
		_state = ETAT_A_FAIRE;
		_type = type;
	}
	
	public boolean isValid(){
		boolean b=true;
		Calendar cal = Calendar.getInstance();
		
		Calendar mcal = Calendar.getInstance();
		mcal.setTime(_date);
		
//		System.out.println("cal= " + cal);
		
//		System.out.println("mcal= " + mcal);
		
		b = b && (cal.get(Calendar.DAY_OF_MONTH) == mcal.get(Calendar.DAY_OF_MONTH));		
//		System.out.println("cal(day) = " + cal.get(Calendar.DAY_OF_MONTH) + " mcal(day)="+mcal.get(Calendar.DAY_OF_MONTH)+", day equals= " + b);
		
		b = b && (cal.get(Calendar.MONTH) == mcal.get(Calendar.MONTH));
//		System.out.println("day equals,month equals= " + b);
		
		b = b && (cal.get(Calendar.YEAR) == mcal.get(Calendar.YEAR));
//		System.out.println("year equals, day equals,month equals= " + b);
		
		return b;
	}
	
	public Mission(long id, int type, String destination, String description, String sdate){
		this(id, type, destination, description);
		_date = parseDate(sdate);
	}

	public static Date parseDate(String sdate){
		try {
			return df.parse(sdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String formatDate(Date date){
		return df.format(date);
	}
	

	public Mission(long id, int type, String destination, String description, Date date){
		this(id, type, destination, description);
		_date = date;
	}

	public Long getId() {
		return _id;
	}

	public String getDescription() {
		return _description;
	}
	
	public String getDestination() {
		return _destination;
	}

	public void setState(int state){
		_state = state;
	}
	
	public int getState() {
		return _state;
	}

	public Date getDate() {
		return _date;
	}
	
	public String getStringState(){
		String result="??";
		switch (_state) {
		case ETAT_A_FAIRE:
			result= "--";
			break;
		case ETAT_EFFECTUE:
			result= "OK";			
			break;
		case ETAT_NON_REALISE_ANNULE:
			result= "ANNULE";
			break;
		case ETAT_NON_REALISE_CLIENT_ABSENT:
			result= "ABSENT";
			break;

		default:
			break;
		}
		return result;
	}
	
	public String getStringType(){
		String result="??";
		switch (_type) {
		case TYPE_DESCENTE:
			result= "D";
			break;
		case TYPE_MONTEE:
			result= "M";			
			break;
		case TYPE_MONTEE_DESCENTE:
			result= "M+D";
			break;
		default:
			break;
		}
		return result;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(_id);
		buf.append(") ");
		buf.append(_date);
		buf.append(" : ");
		buf.append(getStringType());
		buf.append("> ");
		buf.append(_description);
		buf.append(" => ");
		buf.append(_destination);
		buf.append(" [");
		buf.append(getStringState());
		buf.append("]");
		
		return buf.toString();
	}

	public int getType() {
		return _type;
	}
}
