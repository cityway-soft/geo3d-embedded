import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PushbackInputStream;

import junit.framework.TestCase;

import org.avm.elementary.protocol.avm.parser.C_ACK;
import org.avm.elementary.protocol.avm.parser.C_CNX;
import org.avm.elementary.protocol.avm.parser.C_DCNX;
import org.avm.elementary.protocol.avm.parser.C_MSG;
import org.avm.elementary.protocol.avm.parser.Message;
import org.avm.elementary.protocol.avm.parser.PING;
import org.avm.elementary.protocol.avm.parser.PONG;
import org.avm.elementary.protocol.avm.parser.Parser;
import org.avm.elementary.protocol.avm.parser.S_ACK;
import org.avm.elementary.protocol.avm.parser.S_CNX;
import org.avm.elementary.protocol.avm.parser.S_DCNX;
import org.avm.elementary.protocol.avm.parser.S_MSG;

public class ParserTest extends TestCase {

	private Message test(Message msg) throws Exception {
		System.out.println(msg);

		Parser parser = new Parser();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		parser.put(msg, out);

		byte[] buffer = out.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		Message result = (Message) parser.get(new PushbackInputStream(in));
		System.out.println(result);
		return result;
	}

	public void testC_CNX() throws Exception {
		Message o1 = new C_CNX(1, 2, "AVM_00100001");
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testS_CNX() throws Exception {
		Message o1 = new S_CNX();
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testC_DCNX() throws Exception {
		Message o1 = new C_DCNX(1, 2);
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testS_DCNX() throws Exception {
		Message o1 = new S_DCNX();
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testPING() throws Exception {
		Message o1 = new PING(1, 2);
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testPONG() throws Exception {
		Message o1 = new PONG();
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testC_MSG() throws Exception {
		Message o1 = new C_MSG(1, 2, "LEA_00100000", "ABCDEF".getBytes());
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testS_MSG() throws Exception {
		Message o1 = new S_MSG("            ", "ABCDEF".getBytes());
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testC_ACK() throws Exception {
		Message o1 = new C_ACK(1, 2);
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

	public void testS_ACK() throws Exception {
		Message o1 = new S_ACK();
		Message o2 = test(o1);
		assertEquals(o1.toString(), o2.toString());
	}

}
