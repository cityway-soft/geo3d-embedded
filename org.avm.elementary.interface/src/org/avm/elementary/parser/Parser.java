package org.avm.elementary.parser;

import java.io.InputStream;
import java.io.OutputStream;

public interface Parser {
	public String getProtocolName();

	public int getProtocolId();

	public Object get(InputStream in) throws Exception;

	public Object unmarshal(InputStream in) throws Exception;

	public void put(Object n, OutputStream out) throws Exception;

	public void marshal(Object n, OutputStream out) throws Exception;
}