package org.avm.elementary.media.mqtt;

import org.stringtree.json.JSONWriter;

public class M2MMessageHelper {

	public static M2MMessage createM2MMessage(BaseData base,
			LocalisedData location, byte[] data) {
		M2MMessage m = new M2MMessage();
		m.setBase(base);
		m.setLocation(location);
		m.setData(new String(data));

		return m;
	}

	public static String toJson(M2MMessage m) {
		JSONWriter writer = new JSONWriter();
		String text = writer.write(m);
		return text;
	}

}
