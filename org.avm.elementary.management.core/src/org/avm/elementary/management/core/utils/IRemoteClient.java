package org.avm.elementary.management.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author MERCUR
 * 
 */
public interface IRemoteClient {

	public String put(StringBuffer buffer, String remoteFilename)
			throws IOException;

	public String put(File file, String remoteFilename) throws IOException;

	public InputStream get() throws IOException;

	public void setMime(String mime);

	public void setEncoding(String encoding);

}
