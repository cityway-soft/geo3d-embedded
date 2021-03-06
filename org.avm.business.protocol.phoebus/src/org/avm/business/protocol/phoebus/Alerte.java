/* @flavorc
 *
 * Alerte.java
 * 
 * This file was automatically generated by flavorc
 * from the source file:
 *     'phoebus.fl'
 *
 * For information on flavorc, visit the Flavor Web site at:
 *     http://www.ee.columbia.edu/flavor
 *
 * -- Do not edit by hand --
 *
 */

package org.avm.business.protocol.phoebus;
import java.io.IOException;
import java.io.InputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

import flavor.Bitstream;
import flavor.IBitstream;
import flavor.MapResult;

public class Alerte extends Message {
    
	public static final int MESSAGE_TYPE = 3;

	public static final String MESSAGE_NAME = "alerte";
	
	public Alerte() {
		super();		
		_entete._type =  MESSAGE_TYPE;
		_entete._champs._date = 1;
		_entete._champs._position = 1;
		_entete._champs._service = 1;
		_entete._champs._anomalie = 1;		
		_entete._champs._options = 1;
		
		_entete._anomalie = new Anomalie();
		//_entete._anomalie._alerte = 1;
		
		_entete._options = new Options(7,1);
		
	}

	public String toString() {	
		return MESSAGE_NAME + " [" + super.toString() + "]";
	}
	
	public static class DefaultMessageFactory extends MessageFactory {

		protected Message unmarshal(InputStream in) throws Exception {
			IBindingFactory factory = BindingDirectory
					.getFactory(Alerte.class);
			IUnmarshallingContext context = factory
					.createUnmarshallingContext();
			return (Message) context.unmarshalDocument(in, null);

		}

		protected Message get(InputStream in) throws Exception {
			Bitstream bs = new Bitstream(in);
			Message message = new Alerte();
			message.get(bs);
			bs.close();
			in.reset();
			return message;
		}

	}

	static {
		MessageFactory.factories.put(new Integer(MESSAGE_TYPE),
				new DefaultMessageFactory());
	}
	

    public int get(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _F_ret += super.get(_F_bs);
        return _F_ret;
    }

    public int put(IBitstream _F_bs) throws IOException {
        int _F_ret = 0;
        MapResult _F_mr;
        _F_ret += super.put(_F_bs);
        return _F_ret;
    }
}
