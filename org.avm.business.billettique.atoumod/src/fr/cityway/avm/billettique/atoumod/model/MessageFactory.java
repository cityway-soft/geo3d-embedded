package fr.cityway.avm.billettique.atoumod.model;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public abstract class MessageFactory {

	public static Map factories = new HashMap();

	protected abstract Message create(String frame) throws ParseException, Exception;
	protected abstract Message create() throws ParseException;

	public static final Message parse(String frame) throws Exception {
		String stype = frame.substring(0, 2);
		int type = Integer.parseInt(stype, 16);
		if (!factories.containsKey(new Integer(type))) {
			throw new ParseException("Unknown message " + type , 0);
		}
		MessageFactory factory = (MessageFactory) factories.get(new Integer(type));
		Message result = factory.create(frame);
		return result;
	}
	
	public static final Message create(int type) throws ParseException {
		if (!factories.containsKey(new Integer(type))) {
			throw new ParseException("Unknown message " + type , 0);
		}
		MessageFactory factory = (MessageFactory) factories.get(new Integer(type));
		Message result = factory.create();
		return result;
	}
	
	



	static {
		try {
			Class.forName(MessageInterrogationSurveillance.class.getName());
			Class.forName(MessageReponseSurveillance.class.getName());

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
