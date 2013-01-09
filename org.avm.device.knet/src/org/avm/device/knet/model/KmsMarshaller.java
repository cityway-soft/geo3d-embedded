package org.avm.device.knet.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.avm.device.knet.KmsHandler;
import org.avm.device.knet.KnetException;

public abstract class KmsMarshaller implements Runnable {
	private String _identifiant;
	private KmsHandler _handler;

	protected static long _count = 0;

	// protected KmsMarshaller(String id){
	// if (_count == Long.MAX_VALUE)
	// _count = 0;
	// setIdentifiant(id);
	// }

	protected KmsMarshaller() {
		if (_count == Long.MAX_VALUE)
			_count = 0;
		_identifiant = null;
	}

	/**
	 * Identifie un message de façon unique. Les differents champs qui sont
	 * concaténés servent surtout au deboggage.
	 * 
	 * @return un identifiant unique pour le message.
	 */
	public String getIdentifiant() {
		// System.out.println("getName="+_name);
		if (getRole().equals(KmsAuth.ROLE)) {
			_identifiant = KmsAuth.ROLE;
		}
		if (_identifiant != null)
			return _identifiant;

		StringBuffer sb = new StringBuffer();
		sb.append(getRole());
		sb.append("_");
		sb.append(getDate());
		sb.append("_");
		sb.append(getName());
		sb.append("_");
		sb.append(++_count);

		_identifiant = sb.toString();
		return _identifiant;
	}

	public void setIdentifiant(String id) {
		_identifiant = id;
	}

	public KmsHandler getHandler() {
		return _handler;
	}

	public void setHandler(KmsHandler handler) {
		_handler = handler;
	}

	public void run() {
		if (_handler != null)
			_handler.handleMsg(this);
		_handler = null;
	}

	public abstract String toXMLString();

	// public abstract String toTabString(int tab);
	public abstract String getRole();

	public String getDate() {
		return "";
	}

	/**
	 * @return
	 */
	public String getName() {
		return "";
	}

	/**
	 * @param xml :
	 *            Element KMS a rendre presentable par exemple : <?xml version=
	 *            "1.0"?><kms name="_list" date="20061013-074602" ><list><position
	 *            period="0" name="position" /><inputtrig digital="12"
	 *            name="IOtrig12" way="both" log="no" /><inputtrig digital="14"
	 *            name="IOtrig14" way="both" log="no" /><inputtrig digital="15"
	 *            name="IOtrig15" way="up" log="no" /><input> name="LCD_MON"
	 *            period="60" log="no" temperature="0" </input><stats
	 *            period="3600" name="stats" log="no"></stats></list></kms>
	 *            devient kms [name="_list" date="20061013-074602"] list []
	 *            position [period="0" name="position"] inputtrig [digital="12"
	 *            name="IOtrig12" way="both" log="no"] etc...
	 * @return l'element KMS sous forme root [att1 att2 att3, ...] sous-root
	 *         [att1 att2 att3 ....]
	 * 
	 */
	protected String makeItBeautifull(String xml) {
		String out = xml.replace('<', '\n');
		out = out.replace('>', ' ');

		return out;
	}

	/**
	 * Inscrit dans le flux out, un document xml créé dans la classe concrete.
	 * 
	 * @param out
	 *            flux outputstream vers la socket Knet
	 * @throws KnetException
	 * @throws Exception
	 */
	public void marshal(OutputStream out) throws KnetException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version= \"1.0\"?>");
			sb.append(toXMLString().toString());
			OutputStreamWriter osw = new OutputStreamWriter(out, "ISO8859_1");
			PrintWriter pw = new PrintWriter(osw, true);
			char[] req = new char[sb.length() + 1];
			sb.getChars(0, sb.length(), req, 0);
			req[sb.length()] = 0;
			pw.print(req);
			pw.flush();
			out.flush();
		} catch (UnsupportedEncodingException e) {
			throw new KnetException("UnsupportedEncodingException ");
		} catch (IOException e) {
			throw new KnetException("IOException socket " + out);
		}
	}

}
