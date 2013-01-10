package org.avm.device.protocol.canebsf;

import java.io.IOException;

public interface Client {
	public void open(int port, String addgroup, int buffersize) throws IOException;
	public void close()throws IOException;
	public byte[] receive() throws IOException, Exception;
	public int getBufferSize();
}
